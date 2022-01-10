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

package org.eclipse.dataspaceconnector.sql.connection.pool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The connection pool maintains a cache of database connections,
 * which can be reused when future requests to a database are needed.
 */
public interface ConnectionPool extends AutoCloseable {

    Connection getConnection() throws SQLException;

    void returnConnection(Connection connection) throws SQLException;
}
