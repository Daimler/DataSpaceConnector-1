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

package org.eclipse.dataspaceconnector.datamgt.spi;

import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractDefinition;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataAddress;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * PostgreSQL Asset ContractDefinitionRepository. Query, Read and Write operations are directly done in an PostgreSQL instance.
 */
public interface ContractDefinitionRepository {

    /**
     * Query the repository using one or many {@link Criterion}.
     * <p>
     * Please note when using criteria:
     * - to select all assets use only one {@link Criterion} and "*" as left and right operand and "=" as operator
     * or
     * - the left operand must be a string and represents the key of a {@link Asset} property
     * - the operator must either be "=" or "eq"
     * - the right operand can be an object and represents the value of a {@link Asset} property
     *
     * @param criteria to select items
     * @return list of assets
     * @throws SQLException exception
     */
    @NotNull List<Asset> queryAssets(@NotNull List<Criterion> criteria) throws SQLException;

    /**
     * Query the repository using one or many {@link Criterion}.
     * <p>
     * Please note when using criteria:
     * - to select all assets use only one {@link Criterion} and "*" as left and right operand and "=" as operator
     * or
     * - to select the data address of a specific asset use only one {@link Criterion} and  "asset_id" as left, as right operand the {@link Asset} id and "=" as operator
     * or
     * - the left operand must be a and represents the key of a {@link DataAddress} property
     * - the operator must either be "=" or "eq"
     * - the right operand must be a and represents the value of a {@link DataAddress} property
     *
     * @param criteria to select items
     * @return list of data address
     * @throws SQLException exception
     */
    @NotNull List<DataAddress> queryAddress(@NotNull List<Criterion> criteria) throws SQLException;

    /**
     * Query the repository for all contract definitions.
     *
     * @return all contract definitions
     * @throws SQLException exception
     */
    @NotNull List<ContractDefinition> queryAllContractDefinitions() throws SQLException;

    /**
     * Stores the asset and its corresponding data address.
     *
     * @param asset       asset to store
     * @param dataAddress address to store
     * @throws SQLException exception
     */
    void create(@NotNull Asset asset, @NotNull DataAddress dataAddress) throws SQLException;

    /**
     * Stores the contract definition.
     *
     * @param contractDefinition definition to store
     * @throws SQLException exception
     */
    void create(@NotNull ContractDefinition contractDefinition) throws SQLException;

    /**
     * Stores the contract definition in one transaction
     *
     * @param contractDefinitions definitions to store
     * @throws SQLException exception
     */
    void create(@NotNull Collection<ContractDefinition> contractDefinitions) throws SQLException;

    /**
     * Updates the asset.
     *
     * @param asset asset to update
     * @throws SQLException exception
     */
    void update(@NotNull Asset asset) throws SQLException;

    /**
     * Updates the address.
     *
     * @param asset       corresponding asset
     * @param dataAddress address to update
     * @throws SQLException exception
     */
    void update(@NotNull Asset asset, @NotNull DataAddress dataAddress) throws SQLException;

    /**
     * Updates the contract definition.
     *
     * @param contractDefinition contract definition to update
     * @throws SQLException exception
     */
    void update(@NotNull ContractDefinition contractDefinition) throws SQLException;

    /**
     * Deletes the asset and its corresponding data address.
     *
     * @param asset asset to delete
     * @throws SQLException exception
     */
    void delete(@NotNull Asset asset) throws SQLException;

    /**
     * Deletes the contrat definition.
     *
     * @param contractDefinition contract definition to delete
     * @throws SQLException exception
     */
    void delete(@NotNull ContractDefinition contractDefinition) throws SQLException;
}
