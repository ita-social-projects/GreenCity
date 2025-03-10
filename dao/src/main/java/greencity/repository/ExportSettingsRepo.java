package greencity.repository;

import greencity.dto.exportsettings.TableRowsDto;
import greencity.dto.exportsettings.TablesMetadataDto;

public interface ExportSettingsRepo {
    /**
     * Method for receiving metadata about DB table.
     *
     * @return dto {@link TablesMetadataDto}
     */
    TablesMetadataDto getTablesMetadata();

    /**
     * Method for receiving data from table by name, limit and offset.
     *
     * @return dto {@link TableRowsDto}
     */
    TableRowsDto selectPortionFromTable(String tableName, int limit, int offset);
}
