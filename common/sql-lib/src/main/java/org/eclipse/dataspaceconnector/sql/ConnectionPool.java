package org.eclipse.dataspaceconnector.sql;

import java.sql.Connection;

public interface ConnectionPool {

    Connection getConnection();

    void returnConnection(Connection connection);

}
