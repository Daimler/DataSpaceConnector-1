package org.eclipse.dataspaceconnector.sql.operations;

import java.sql.SQLException;

@FunctionalInterface
public interface Transaction {
    void execute() throws SQLException;
}
