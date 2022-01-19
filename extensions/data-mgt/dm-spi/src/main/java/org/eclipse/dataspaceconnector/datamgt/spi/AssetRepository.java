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

import org.eclipse.dataspaceconnector.datamgt.spi.exceptions.IdentifierAlreadyExistsException;
import org.eclipse.dataspaceconnector.datamgt.spi.exceptions.NotFoundException;
import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Repository to create, read, update and delete assets.
 */
public interface AssetRepository {

    /**
     * Query the repository using one or many {@link Criterion}.
     * <p>
     * Please note when using criteria:
     * - to select all assets use only one {@link Criterion} and "*" as left and right operand and "=" as operator
     * or
     * - the left operand must be a string and represents the key of a {@link Asset} property
     * - the operator must either be "=" or "eq"
     * - the right operand can be an object, representing the value of a {@link Asset} property
     *
     * @param criteria to select items
     * @return list of assets
     */
    @NotNull List<Asset> queryAssets(@NotNull List<Criterion> criteria);

    /**
     * Stores the asset and its corresponding data address.
     *
     * @param asset asset to store
     */
    void create(@NotNull Asset asset) throws IdentifierAlreadyExistsException;

    /**
     * Updates the asset.
     *
     * @param asset asset to update
     */
    void update(@NotNull Asset asset) throws NotFoundException;

    /**
     * Deletes the asset and its corresponding data address.
     *
     * @param assetId id of the asset to delete
     */
    void delete(@NotNull String assetId) throws NotFoundException;

}
