package greencity.repository.impl;

import greencity.dto.exportsettings.TableRowsDto;
import greencity.dto.exportsettings.TablesMetadataDto;
import greencity.exception.exceptions.DatabaseMetadataException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportSettingsRepoImplTest {
    private static final String TABLE_NAME = "users";
    private static final int LIMIT = 10;
    private static final int OFFSET = 1;

    @InjectMocks
    private ExportSettingsRepoImpl settingsRepo;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private DatabaseMetaData databaseMetaData;

    @Mock
    private ResultSetMetaData resultSetMetaData;

    @Mock
    private ResultSet resultSet;

    @Mock
    private PreparedStatement preparedStatement;

    @Test
    void getTablesMetadataTest() throws Exception{
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getTables(null, null, null, new String[] {"TABLE"})).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("TABLE_NAME")).thenReturn(TABLE_NAME);
        when(databaseMetaData.getColumns(null, null, TABLE_NAME, "%")).thenReturn(resultSet);
        when(resultSet.getString("COLUMN_NAME")).thenReturn("id");

        TablesMetadataDto result = settingsRepo.getTablesMetadata();

        assertNotNull(result);
    }

    @Test
    void getTablesMetadataSQLExceptionThrownTest() throws Exception{
        when(dataSource.getConnection()).thenThrow(new SQLException());

        assertThrows(DatabaseMetadataException.class , () -> settingsRepo.getTablesMetadata());
    }

    @Test
    void selectPortionFromTableWithValidParamsTest() throws Exception {
        String query = String.format("SELECT * FROM %s LIMIT %d OFFSET %d;", TABLE_NAME, LIMIT, OFFSET);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        when(resultSetMetaData.getColumnCount()).thenReturn(1);
        when(resultSetMetaData.getColumnName(1)).thenReturn("id");
        when(resultSet.getString(1)).thenReturn("1");

        TableRowsDto result = settingsRepo.selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET);

        assertNotNull(result);
    }

    @Test
    void selectPortionFromTableSQLExceptionThrownTest() throws Exception{
        when(dataSource.getConnection()).thenThrow(new SQLException());

        assertThrows(DatabaseMetadataException.class,
            () -> settingsRepo.selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET));
    }
}
