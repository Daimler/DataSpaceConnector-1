/*
 *  Copyright (c) 2021 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Daimler TSS GmbH - Initial API and Implementation
 *
 */

package org.eclipse.datspaceconnector.postgresql.assetindex;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.assertj.core.api.Assertions;
import org.eclipse.dataspaceconnector.kafka.TransactionalKafkaProducer;
import org.eclipse.dataspaceconnector.postgresql.assetindex.PostgresqlAssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.operations.Query;
import org.eclipse.dataspaceconnector.sql.operations.QueryBuilder;
import org.eclipse.dataspaceconnector.sql.operations.SqlConnectionExtension;
import org.eclipse.dataspaceconnector.sql.operations.Transaction;
import org.eclipse.dataspaceconnector.sql.operations.TransactionBuilder;
import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.eclipse.dataspaceconnector.transaction.spi.TransactionContext;
import org.eclipse.dataspaceconnector.transaction.spi.TransactionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(SqlConnectionExtension.class)
public class PostgresqlAndKafkaAssetIndexComponentTest {
    private AssetIndex assetIndex;
    private ConnectionPool connectionPool;
    private TransactionManager transactionManager;
    private Producer kafkaProducer;

    @BeforeEach
    public void setup(ConnectionPool connectionPool, TransactionManager transactionManager) {
        this.connectionPool = connectionPool;
        this.transactionManager = transactionManager;
        this.assetIndex = new PostgresqlAssetIndex(connectionPool);

        Properties kafkaProperties = new Properties();
        kafkaProperties.setProperty("bootstrap.servers", "localhost:9092");
        kafkaProperties.setProperty("zookeeper.connect", "zookeeper:2181");
        kafkaProperties.setProperty("producer.type", "async");
        kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        //kafkaProperties.setProperty("transactional.id", "testproducer");

        KafkaProducer<Object, Object> kafkaProducerDelegate = new KafkaProducer<>(kafkaProperties);

        this.kafkaProducer = new TransactionalKafkaProducer<>(transactionManager, kafkaProducerDelegate);

    }

    @Test
    public void myTest() throws SQLException {
//        TransactionContext context = transactionManager.beginTransaction();

//        Transaction transaction = new TransactionBuilder(connectionPool)
//                .create(Asset.Builder.newInstance().id("trans-asset").build())
//                .build();
//
//        transaction.execute(); // no commit

//        context.rollback(); // rollback
//        context.commit(); // rollback
//
//        Query<Asset> query = new QueryBuilder(connectionPool)
//                .assets()
//                .with(Asset.PROPERTY_ID, "trans-asset")
//                .build();
//
//        List<Asset> assets = query.execute();
//
//        TransactionContext t1 = transactionManager.beginTransaction();
//
//
//
//        TransactionContext t2 = transactionManager.beginTransaction();
//
//        Transaction transaction = new TransactionBuilder(connectionPool)
//                .create(Asset.Builder.newInstance().id("trans-asset").build())
//                .build();
//
//        transaction.execute(); // commit on connection
//
//        t2.commit();


        TransactionContext t3 = transactionManager.beginTransaction();
        var record = new ProducerRecord<String, String>("hallo", null, "welt");
        kafkaProducer.send(record);
        t3.commit();

//
//
//        t1.commit();
//
//        assets = query.execute();
//
//        Assertions.assertThat(assets).size().isEqualTo(1);
    }

    @Test
    public void testQueryExpression() throws SQLException {
        List<Criterion> criteria = new ArrayList<>();
        criteria.add(new Criterion("foo", "=", "bar"));

        Asset asset = Asset.Builder.newInstance().property("foo", "bar").build();
        createAsset(asset);

        AssetSelectorExpression expression =
                AssetSelectorExpression.Builder.newInstance().criteria(criteria).build();
        Stream<Asset> result = assetIndex.queryAssets(expression);

        Assertions.assertThat(result.map(Asset::getId).collect(Collectors.toUnmodifiableList()))
                .contains(asset.getId());
    }

    @Test
    @Disabled
    public void testQueryCriteria() throws SQLException {
        List<Criterion> criteria = new ArrayList<>();
        criteria.add(new Criterion("foo", "=", "bar"));

        Asset asset = Asset.Builder.newInstance().property("foo", "bar").build();
        createAsset(asset);

        Stream<Asset> result = assetIndex.queryAssets(criteria);

        Assertions.assertThat(result.map(Asset::getId).collect(Collectors.toUnmodifiableList()))
                .contains(asset.getId());
    }

    @Test
    @Disabled
    public void testQueryId() throws SQLException {
        Asset asset = Asset.Builder.newInstance().build();
        createAsset(asset);

        Asset result = assetIndex.findById(asset.getId());

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(asset.getId());
    }

    private void createAsset(Asset asset) throws SQLException {
        Transaction transaction = new TransactionBuilder(connectionPool)
                .create(asset)
                .build();
        transaction.execute();
    }
}
