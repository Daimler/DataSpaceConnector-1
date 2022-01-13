package org.eclipse.dataspaceconnector.sql.operations.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IdMapperTest {
    private IdMapper idMapper;

    @BeforeEach
    public void setup() {
        idMapper = new IdMapper();
    }

    @Test
    public void testMapping() throws SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.getString("id")).thenReturn("1");

        String result = idMapper.mapResultSet(resultSet);
        Assertions.assertEquals(result, "1");
    }
}
