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

package org.eclipse.dataspaceconnector.sql.asset.index;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;
import javax.sql.DataSource;

class LazyDataSource implements DataSource {
    private final Supplier<DataSource> dataSourceSupplier;

    private DataSource instance = null;

    protected synchronized DataSource getDataSource() {
        if (instance == null) {
            instance = dataSourceSupplier.get();
        }
        return instance;
    }

    public LazyDataSource(Supplier<DataSource> dataSourceSupplier) {
        this.dataSourceSupplier = Objects.requireNonNull(dataSourceSupplier);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String s, String s1) throws SQLException {
        return getDataSource().getConnection(s, s1);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        getDataSource().setLogWriter(printWriter);
    }

    @Override
    public void setLoginTimeout(int i) throws SQLException {
        getDataSource().setLoginTimeout(i);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getDataSource().getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getDataSource().getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return getDataSource().unwrap(aClass);
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return getDataSource().isWrapperFor(aClass);
    }
}
