package org.eclipse.dataspaceconnector.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.eclipse.dataspaceconnector.kafka.util.FutureUtil;
import org.eclipse.dataspaceconnector.transaction.spi.TransactionManager;
import org.eclipse.dataspaceconnector.transaction.spi.TransactionStatus;

import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class TransactionalKafkaProducer<K, V> extends AbstractTransactionalKafkaProducer<K, V> {
    private final KafkaProducer<K, V> kafkaProducer;
    private final TransactionManager transactionManager;
    private final ThreadLocal<Stack<CompletableProducerRecord<K, V>>> messages;

    public TransactionalKafkaProducer(KafkaProducer<K, V> kafkaProducer) {
        this(null, Objects.requireNonNull(kafkaProducer));
    }

    public TransactionalKafkaProducer(TransactionManager transactionManager, KafkaProducer<K, V> kafkaProducer) {
        super(kafkaProducer);

        this.transactionManager = transactionManager;
        this.kafkaProducer = Objects.requireNonNull(kafkaProducer);
        this.messages = ThreadLocal.withInitial(Stack::new);

        if (transactionManager != null) {
            //kafkaProducer.initTransactions();
            registerListener();
        }
    }

    private void registerListener() {
        transactionManager.onStatusChanged(status -> {
            switch (status) {
                case NEW:
                    break;
                case ACTIVE:
                    break;
                case ROLLBACK:
                    rollback();
                    break;
                case ROLLBACK_COMPLETE:
                    break;
                case COMMIT:
                    sendEnqueuedMessages();
                    break;
                case COMMIT_COMPLETE:
                    break;
            }
        });
    }

    // since no kafka transactiokn is running, no message has been sent, so it's sufficient to clear the stack
    private void rollback() {
        Stack<CompletableProducerRecord<K, V>> stack = messages.get();
        if (stack == null) {
            return;
        }

        while (!stack.isEmpty()) {
            stack.pop().future.completeExceptionally(new TransactionRollbackException("rolled back"));
        }
    }

    private void sendMessage(CompletableProducerRecord<K, V> completableProducerRecord) {
        CompletableFuture<?> completableFuture = completableProducerRecord.future;
        ProducerRecord<K, V> producerRecord = completableProducerRecord.record;
        Callback callback = completableProducerRecord.callback;

        Future<?> kafkaProducerFuture = kafkaProducer.send(producerRecord, callback);
        FutureUtil.createCompletableFuture(kafkaProducerFuture)
                .thenApply(recordMetadata -> {
                    completableFuture.complete(null);
                    return null;
                }).exceptionally(throwable -> {
                    completableFuture.completeExceptionally(throwable);
                    return null;
                });
    }

    private void enqueueMessage(CompletableProducerRecord<K, V> completableProducerRecord) {
        messages.get().add(completableProducerRecord);
    }

    private void sendEnqueuedMessages() {
        Stack<CompletableProducerRecord<K, V>> stack = messages.get();
        if (stack == null) {
            return;
        }

        try {
            //kafkaProducer.beginTransaction();

            while (!stack.isEmpty()) {
                sendMessage(stack.pop());
            }
        } catch (Exception ex) {
            while (!stack.isEmpty()) {
                stack.pop().future.completeExceptionally(ex);
            }

            //kafkaProducer.abortTransaction();

            throw new TransactionRollbackException(String.format("Transaction failed: %s", ex.getMessage()), ex);
        }
    }

    private static class CompletableProducerRecord<K, V> {
        public final ProducerRecord<K, V> record;
        public final CompletableFuture<?> future;
        public final Callback callback;

        private CompletableProducerRecord(
                ProducerRecord<K, V> record,
                CompletableFuture<?> future,
                Callback callback) {
            this.record = Objects.requireNonNull(record);
            this.future = Objects.requireNonNull(future);
            this.callback = Objects.requireNonNull(callback);
        }
    }
    
    @Override
    public Future<RecordMetadata> send(ProducerRecord<K, V> record) {
        return send(record, (m, e) -> {});
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<K, V> record, Callback callback) {
        CompletableFuture<RecordMetadata> completableFuture = new CompletableFuture<>();
        CompletableProducerRecord<K, V> completableProducerRecord = new CompletableProducerRecord<>(record, completableFuture, callback);

        // no transaction planned/ opened so fire and forget
        if (transactionManager == null || transactionManager.getTransactionStatus() == TransactionStatus.INACTIVE) {
            sendMessage(completableProducerRecord);
        } else {
            // enqueue message to transaction
            enqueueMessage(completableProducerRecord);
        }

        return completableFuture;
    }

}
