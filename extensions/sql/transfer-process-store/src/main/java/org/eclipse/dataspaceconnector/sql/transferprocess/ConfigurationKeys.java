/*
 *  Copyright (c) 2022 Mercedes Benz Tech Innovation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Mercedes Benz Tech Innovation - Add Configuration Keys and default value
 *
 */

package org.eclipse.dataspaceconnector.sql.transferprocess;

import org.eclipse.dataspaceconnector.spi.EdcSetting;

/**
 * Defines configuration keys used by the SqlTransferProcessStoreExtension.
 */
public interface ConfigurationKeys {

    /**
     * Name of the datasource to use for accessing transfer processes.
     */
    @EdcSetting
    String DATASOURCE_SETTING_NAME = "edc.transferprocess.datasource.name";

    String DATASOURCE_SETTING_NAME_DEFAULT = "transferprocess";
}
