package org.eclipse.dataspaceconnector.sql.repository.connection;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface TransactionFunction {
    void invoke(Connection connection) throws SQLException;
}
