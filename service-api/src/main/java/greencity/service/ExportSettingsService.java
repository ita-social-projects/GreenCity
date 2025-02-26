package greencity.service;

import greencity.dto.exportsettings.TableRowsDto;
import greencity.dto.exportsettings.TablesMetadataDto;
import java.io.InputStream;

public interface ExportSettingsService {
    /**
     * Method for receiving all DB tables short metadata.
     *
     * @return {@link TablesMetadataDto} instance.
     */
    TablesMetadataDto getTablesMetadata(String secretKey);

    /**
     * Method for receiving rows from table by table name, limit and offset.
     *
     * @param tableName {@link String}
     * @param limit     is limit of rows to return.
     * @param offset    is offset for rows to return.
     *
     * @return {@link TableRowsDto} object with metadata.
     */
    TableRowsDto selectFromTable(String tableName, int limit, int offset, String secretKey);

    /**
     * Method for receiving an excel file as InputStream with rows from DB by table
     * name, limit, offset.
     *
     * @param tableName {@link String}
     * @param limit     is limit of rows to return.
     * @param offset    is offset for rows to return.
     *
     * @return {@link InputStream} InputStream with file.
     */
    InputStream getExcelFileAsResource(String tableName, int limit, int offset, String secretKey);
}
