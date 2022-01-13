package org.eclipse.dataspaceconnector.sql.operations;

import java.sql.SQLException;
import java.util.List;

@FunctionalInterface
public interface Query<T> {
    List<T> execute() throws SQLException;
}
