package greencity.repository.impl;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.exportsettings.TableRowsDto;
import greencity.dto.exportsettings.TablesMetadataDto;
import greencity.exception.exceptions.DatabaseMetadataException;
import greencity.repository.ExportSettingsRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ExportSettingsRepoImpl implements ExportSettingsRepo {
    private final DataSource dataSource;

    @Override
    public TablesMetadataDto getTablesMetadata() {
        Map<String, List<String>> tablesMetaDada = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            // null for database, schema, and catalog means the method will return all
            // tables in the current database.
            ResultSet tables = metaData.getTables(null, null, null, new String[] {AppConstant.TABLE});
            while (tables.next()) {
                List<String> columnsNames = new ArrayList<>();
                String tableName = tables.getString(AppConstant.TABLE_NAME);
                // null for database, schema, and catalog means it will return columns for the
                // current table across all schemas.
                // "%" means "all columns" for the given table.
                ResultSet columns = metaData.getColumns(null, null, tableName, "%");
                while (columns.next()) {
                    String columnName = columns.getString(AppConstant.COLUMN_NAME);
                    columnsNames.add(columnName);
                }
                tablesMetaDada.put(tableName, columnsNames);
            }
            return new TablesMetadataDto(tablesMetaDada);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DatabaseMetadataException(e.getMessage(), e);
        }
    }

    @Override
    public TableRowsDto selectPortionFromTable(String tableName, int limit, int offset) {
        String query = String.format(AppConstant.SELECT_FROM_WITH_LIMIT_AND_OFFSET, tableName, limit, offset);
        List<Map<String, String>> tableData = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new LinkedHashMap<>();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        row.put(rs.getMetaData().getColumnName(i), rs.getString(i));
                    }
                    tableData.add(row);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DatabaseMetadataException(ErrorMessage.SQL_METADATA_EXCEPTION_MESSAGE + tableName, e);
        }
        return new TableRowsDto(tableName, tableData);
    }

    @Override
    public int countRowsInTable(String tableName) {
        String query = String.format(AppConstant.SELECT_COUNT_FROM, tableName);

        try (Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {
            rs.next();

            return rs.getInt(1);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DatabaseMetadataException(ErrorMessage.SQL_METADATA_EXCEPTION_MESSAGE + tableName, e);
        }
    }
}
