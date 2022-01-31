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

package org.eclipse.dataspaceconnector.sql.contractdefinitionstore;

import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.contract.offer.store.ContractDefinitionStore;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractDefinition;
import org.eclipse.dataspaceconnector.sql.operations.SqlDataSourceExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import javax.sql.DataSource;

@ExtendWith(SqlDataSourceExtension.class)
public class SqlContractDefinitionStoreComponentTest {

    private ContractDefinitionStore contractDefinitionStore;

    // mocks
    private FakeTransactionContext transactionContext;

    @BeforeEach
    public void setup(DataSource dataSource) {
        this.transactionContext = new FakeTransactionContext();
        contractDefinitionStore = new SqlContractDefinitionStore(dataSource, transactionContext);
    }

    @Test
    public void testSuccess() {
        ContractDefinition contractDefinition = createDefinition();
        ContractDefinition contractDefinition2 = createDefinition();

        contractDefinitionStore.save(contractDefinition);
        contractDefinitionStore.save(contractDefinition2);

        ContractDefinition updatedDefinition = createDefinition(contractDefinition.getId(),
                Policy.Builder.newInstance().assignee("foo").build());

        contractDefinitionStore.update(updatedDefinition);
        contractDefinitionStore.delete(contractDefinition2);

        Collection<ContractDefinition> definitions = contractDefinitionStore.findAll();

        Assertions.assertEquals(1, definitions.size());
        Assertions.assertEquals("foo",
                definitions.stream().findFirst()
                        .map(d -> d.getContractPolicy().getAssignee())
                        .orElse("bar"));
    }

    @Test
    public void testSaveOneInTransaction() {
        ContractDefinition contractDefinition = createDefinition();

        contractDefinitionStore.save(contractDefinition);
        Assertions.assertTrue(transactionContext.isTransactionExecuted());
    }


    @Test
    public void testSaveManyInTransaction() {
        ContractDefinition contractDefinition = createDefinition();

        contractDefinitionStore.save(Collections.singletonList(contractDefinition));
        Assertions.assertTrue(transactionContext.isTransactionExecuted());
    }

    @Test
    public void testUpdateInTransaction() {
        ContractDefinition contractDefinition = createDefinition();

        contractDefinitionStore.update(contractDefinition);
        Assertions.assertTrue(transactionContext.isTransactionExecuted());
    }

    @Test
    public void testDeleteInTransaction() {
        ContractDefinition contractDefinition = createDefinition();

        contractDefinitionStore.update(contractDefinition);
        Assertions.assertTrue(transactionContext.isTransactionExecuted());
    }

    private ContractDefinition createDefinition() {
        return createDefinition(UUID.randomUUID().toString(),
                Policy.Builder.newInstance().build());
    }

    private ContractDefinition createDefinition(String id, Policy contractPolicy) {
        return ContractDefinition.Builder.newInstance().id(id)
                .selectorExpression(AssetSelectorExpression.SELECT_ALL)
                .contractPolicy(contractPolicy)
                .accessPolicy(Policy.Builder.newInstance().build())
                .build();
    }

    private static class FakeTransactionContext implements TransactionContext {

        public boolean transactionExecuted = false;

        @Override
        public void execute(TransactionBlock block) {
            try {
                transactionExecuted = true;
                block.execute();
            } catch (Exception e) {
                // ignore
            }
        }

        public boolean isTransactionExecuted() {
            return transactionExecuted;
        }
    }
}
