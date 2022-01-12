package org.eclipse.dataspaceconnector.sql.operations.transaction.operations;

import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionOperation {
    void execute(Connection connection) throws SQLException;
}
