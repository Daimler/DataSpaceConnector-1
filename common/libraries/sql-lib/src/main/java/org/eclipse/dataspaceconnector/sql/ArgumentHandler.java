package org.eclipse.dataspaceconnector.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Component capable of setting a certain parameter type to it's
 * corresponding position within a {@link java.sql.PreparedStatement}
 */
interface ArgumentHandler {

    /**
     * Tests whether an argument can be used by the current handler
     *
     * @param value to be associated with the prepared statement
     * @return true if the current argument handler can act on the given argument
     */
    boolean accepts(Object value);

    /**
     * Associates an argument with a given SQL statement at its specific position
     *
     * @param statement to be carrying the argument
     * @param position  to be used for carrying the argument
     * @param argument  to be used together with the statement
     * @throws SQLException if something went wrong
     */
    void handle(PreparedStatement statement, int position, Object argument) throws SQLException;
}
