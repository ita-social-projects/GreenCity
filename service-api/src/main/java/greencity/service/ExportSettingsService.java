package greencity.service;

import greencity.dto.exportsettings.EnvironmentDto;
import greencity.dto.exportsettings.TableParamsRequestDto;
import greencity.dto.exportsettings.TableRowsDto;
import greencity.dto.exportsettings.TablesMetadataDto;
import java.io.InputStream;

public interface ExportSettingsService {
    /**
     * Method for receiving all DB tables short metadata.
     *
     * @return {@link TablesMetadataDto} instance.
     */
    TablesMetadataDto getTablesMetadata();

    /**
     * Method for receiving rows from table by table name, limit and offset.
     *
     * @param tableParams {@link TableParamsRequestDto} dto with params such as
     *                    tableName, limit and offset.
     *
     * @return {@link TableRowsDto} object with metadata.
     */
    TableRowsDto selectFromTable(TableParamsRequestDto tableParams);

    /**
     * Method for receiving an excel file as InputStream with rows from DB by table
     * name, limit, offset.
     *
     * @param tableParams {@link TableParamsRequestDto} dto with params such as
     *                    tableName, limit and offset.
     *
     * @return {@link InputStream} InputStream with file.
     */
    InputStream getExcelFileAsResource(TableParamsRequestDto tableParams);

    /**
     * Method for receiving all environment variables.
     *
     * @return {@link EnvironmentDto} instance.
     */
    EnvironmentDto getEnvironmentVariables();
}
