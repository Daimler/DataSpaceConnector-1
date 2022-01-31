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

package org.eclipse.dataspaceconnector.sql.operations.contract.definition;

import org.assertj.core.api.Assertions;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractDefinition;
import org.eclipse.dataspaceconnector.sql.operations.SqlDataSourceExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

@ExtendWith(SqlDataSourceExtension.class)
public class SqlContractDefinitionUpdateTest {

    private DataSource dataSource;

    @BeforeEach
    public void setup(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Test
    public void testContractDefinitionCreation() throws SQLException {

        ContractDefinition baseDefinition = createDefinition();

        Connection connection = dataSource.getConnection();
        SqlContractDefinitionInsert create = new SqlContractDefinitionInsert(connection);
        create.execute(baseDefinition);

        Criterion criterion = new Criterion("hello", "=", "world");
        AssetSelectorExpression selectorExpression = AssetSelectorExpression.Builder.newInstance()
                .criteria(Collections.singletonList(criterion))
                .build();
        Policy contractPolicy = Policy.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .build();
        Policy accessPolicy = Policy.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .build();

        ContractDefinition contractDefinition = ContractDefinition.Builder.newInstance()
                .id(baseDefinition.getId())
                .selectorExpression(selectorExpression)
                .contractPolicy(contractPolicy)
                .accessPolicy(accessPolicy)
                .build();

        SqlContractDefinitionUpdate update = new SqlContractDefinitionUpdate(connection);
        update.execute(contractDefinition);

        SqlContractDefinitionQuery query = new SqlContractDefinitionQuery(connection);
        List<ContractDefinition> storedDefinitions = query.execute();
        ContractDefinition storedDefinition = storedDefinitions.stream()
                .filter(d -> d.getId().equals(contractDefinition.getId()))
                .findFirst()
                .orElse(null);

        Assertions.assertThat(storedDefinition).isNotNull();
        Assertions.assertThat(storedDefinition.getSelectorExpression()).isNotNull();
        Assertions.assertThat(storedDefinition.getSelectorExpression().getCriteria().get(0)).isEqualTo(criterion);
        Assertions.assertThat(storedDefinition.getAccessPolicy()).isNotNull();
        Assertions.assertThat(storedDefinition.getAccessPolicy().getUid()).isEqualTo(accessPolicy.getUid());
        Assertions.assertThat(storedDefinition.getContractPolicy()).isNotNull();
        Assertions.assertThat(storedDefinition.getContractPolicy().getUid()).isEqualTo(contractPolicy.getUid());
    }

    private ContractDefinition createDefinition() {
        AssetSelectorExpression selectorExpression = AssetSelectorExpression.Builder.newInstance()
                .build();
        Policy contractPolicy = Policy.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .build();
        Policy accessPolicy = Policy.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .build();

        return ContractDefinition.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .selectorExpression(selectorExpression)
                .contractPolicy(contractPolicy)
                .accessPolicy(accessPolicy)
                .build();
    }
}
