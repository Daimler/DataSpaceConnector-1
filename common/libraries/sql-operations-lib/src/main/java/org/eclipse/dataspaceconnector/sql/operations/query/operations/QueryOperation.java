package org.eclipse.dataspaceconnector.sql.operations.query.operations;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface QueryOperation<T> {

    List<T> invoke(Connection connection) throws SQLException;
}
