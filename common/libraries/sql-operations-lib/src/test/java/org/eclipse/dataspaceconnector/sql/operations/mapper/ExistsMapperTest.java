package org.eclipse.dataspaceconnector.sql.operations.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExistsMapperTest {

    private ExistsMapper existsMapper;

    @BeforeEach
    public void setup() {
        existsMapper = new ExistsMapper();
    }

    @Test
    public void testExistsTrue() throws SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.getBoolean(1)).thenReturn(true);

        boolean result = existsMapper.mapResultSet(resultSet);
        Assertions.assertTrue(result);
    }

    @Test
    public void testExistsFalse() throws SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.getBoolean(1)).thenReturn(false);

        boolean result = existsMapper.mapResultSet(resultSet);
        Assertions.assertFalse(result);
    }

}
