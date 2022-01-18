package org.eclipse.dataspaceconnector.kafka;

import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.ProducerFencedException;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

abstract class AbstractTransactionalKafkaProducer<K, V> implements Producer<K, V> {
    private final KafkaProducer<K, V> delegate;

    protected AbstractTransactionalKafkaProducer(KafkaProducer<K, V> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }


    @Override
    public void initTransactions() {
        delegate.initTransactions();
    }

    @Override
    public void beginTransaction() throws ProducerFencedException {
        delegate.beginTransaction();
    }

    @Override
    public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> offsets, String consumerGroupId) throws ProducerFencedException {
        delegate.sendOffsetsToTransaction(offsets, consumerGroupId);
    }

    @Override
    public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> offsets, ConsumerGroupMetadata groupMetadata) throws ProducerFencedException {
        delegate.sendOffsetsToTransaction(offsets, groupMetadata);
    }

    @Override
    public void commitTransaction() throws ProducerFencedException {
        delegate.commitTransaction();
    }

    @Override
    public void abortTransaction() throws ProducerFencedException {
        delegate.abortTransaction();
    }

    @Override
    public void flush() {
        delegate.flush();
    }

    @Override
    public List<PartitionInfo> partitionsFor(String topic) {
        return delegate.partitionsFor(topic);
    }

    @Override
    public Map<MetricName, ? extends Metric> metrics() {
        return delegate.metrics();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public void close(Duration timeout) {
        delegate.close(timeout);
    }
}
