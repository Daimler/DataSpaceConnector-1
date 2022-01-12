package org.eclipse.dataspaceconnector.sql.repository.test;

import org.eclipse.dataspaceconnector.sql.connection.ConnectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class ConnectionFactoryImpl implements ConnectionFactory {
    @Override
    public Connection create() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection("jdbc:h2:~/test", "", "");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
