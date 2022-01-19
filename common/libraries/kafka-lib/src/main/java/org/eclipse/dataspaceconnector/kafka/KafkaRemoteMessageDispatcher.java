package org.eclipse.dataspaceconnector.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.eclipse.dataspaceconnector.kafka.util.FutureUtil;
import org.eclipse.dataspaceconnector.spi.message.MessageContext;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcher;
import org.eclipse.dataspaceconnector.spi.types.domain.message.RemoteMessage;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * The RemoteMessageDispatcher is an EDC interface. A Kafka Extension would implement this interface and register itself at the dispatcher registry.
 */
public class KafkaRemoteMessageDispatcher implements RemoteMessageDispatcher {
    private static final String PROTOCOL = "kafka";

    private final Producer<Object, Object> kafkaProducer;
    private final RemoteMessageTopicResolver remoteMessageTopicResolver;

    public KafkaRemoteMessageDispatcher(Producer<Object, Object> kafkaProducer, RemoteMessageTopicResolver remoteMessageTopicResolver) {
        this.kafkaProducer = Objects.requireNonNull(kafkaProducer);
        this.remoteMessageTopicResolver = Objects.requireNonNull(remoteMessageTopicResolver);
    }

    @Override
    public String protocol() {
        return PROTOCOL;
    }

    @Override
    public <T> CompletableFuture<T> send(Class<T> responseType, RemoteMessage remoteMessage, MessageContext context) {
        Objects.requireNonNull(remoteMessage);

        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        String topic = resolveTopic(remoteMessage, context);

        ProducerRecord<Object, Object> producerRecord = new ProducerRecord<>(topic, null, remoteMessage);
        Future<RecordMetadata> kafkaFuture = kafkaProducer.send(producerRecord);

        FutureUtil.createCompletableFuture(kafkaFuture)
                .thenApply(recordMetadata -> {
                    completableFuture.complete(null);
                    return null;
                }).exceptionally(throwable -> {
                    completableFuture.completeExceptionally(throwable);
                    return null;
                });

        return completableFuture;
    }

    private String resolveTopic(RemoteMessage remoteMessage, MessageContext context) {
        return remoteMessageTopicResolver.resolveTopic(remoteMessage, context);
    }
}
