package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.exportsettings.EnvironmentDto;
import greencity.dto.exportsettings.TableParamsRequestDto;
import greencity.dto.exportsettings.TablesMetadataDto;
import org.springframework.data.domain.Pageable;
import java.io.InputStream;
import java.util.Map;

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
     * @param tableName {@link String} DB table name.
     * @param pageable  {@link Pageable} pageable object with params such as page,
     *                  size, sort etc.
     *
     * @return {@link PageableAdvancedDto} pageable object with data.
     */
    PageableAdvancedDto<Map<String, String>> selectFromTable(String tableName, Pageable pageable);

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
