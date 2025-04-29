package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.config.CustomPageableHandlerMethodArgumentResolver;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.exportsettings.EnvironmentDto;
import greencity.dto.exportsettings.TableParamsRequestDto;
import greencity.dto.exportsettings.TablesMetadataDto;
import greencity.exception.exceptions.DatabaseMetadataException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.ExportSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ExportSettingsControllerTest {
    private MockMvc mockMvc;
    private static final TableParamsRequestDto tableParams = ModelUtils.tableParamsRequestDto();
    private static final String SETTINGS_CONTROLLER_LINK = "/export/settings";
    private static final String TABLE_NAME = tableParams.tableName();
    private static final String INVALID_TABLE_NAME = "users1";
    private static final String NOT_EXISTS_TABLE_NAME = "usersssssss";
    private static final int LIMIT = tableParams.limit();
    private static final int OFFSET = tableParams.offset();
    private static final int PAGE = 0;
    private static final int SIZE = 20;
    Pageable pageable = PageRequest.of(PAGE, SIZE);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    private final TableParamsRequestDto tableParamsWithNotValidTableName =
        new TableParamsRequestDto(NOT_EXISTS_TABLE_NAME, LIMIT, OFFSET);
    @InjectMocks
    private ExportSettingsController exportSettingsController;
    @Mock
    private ExportSettingsService exportSettingsService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(exportSettingsController)
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper, null))
            .setCustomArgumentResolvers(new CustomPageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void getTablesInfoTest() throws Exception {
        TablesMetadataDto tablesMetadataDto = ModelUtils.getTablesMetadataDto();
        when(exportSettingsService.getTablesMetadata()).thenReturn(tablesMetadataDto);
        String expectedJson = objectMapper.writeValueAsString(tablesMetadataDto);

        mockMvc.perform(get(SETTINGS_CONTROLLER_LINK + "/tables")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void getSelectedWithValidParamsTest() throws Exception {
        PageableAdvancedDto<Map<String, String>> pageableResult = ModelUtils.getPageableAdvancedDtoForTableRows();
        when(exportSettingsService.selectFromTable(tableParams.tableName(), pageable)).thenReturn(pageableResult);
        String expectedJson = objectMapper.writeValueAsString(pageableResult);

        mockMvc.perform(get(SETTINGS_CONTROLLER_LINK + "/select")
            .param("tableName", TABLE_NAME)
            .param("page", String.valueOf(PAGE))
            .param("size", String.valueOf(SIZE))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void getSelectedWithInvalidTableNameTest() throws Exception {
        doThrow(new DatabaseMetadataException(ErrorMessage.SQL_METADATA_EXCEPTION_MESSAGE + NOT_EXISTS_TABLE_NAME))
            .when(exportSettingsService)
            .selectFromTable(INVALID_TABLE_NAME, PageRequest.of(PAGE, SIZE));

        mockMvc.perform(get(SETTINGS_CONTROLLER_LINK + "/select")
            .param("tableName", INVALID_TABLE_NAME)
            .param("page", String.valueOf(PAGE))
            .param("size", String.valueOf(SIZE))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void getSelectedWithNonExistentTableNameTest() throws Exception {
        doThrow(new DatabaseMetadataException(ErrorMessage.SQL_METADATA_EXCEPTION_MESSAGE + NOT_EXISTS_TABLE_NAME))
            .when(exportSettingsService)
            .selectFromTable(NOT_EXISTS_TABLE_NAME, pageable);

        mockMvc.perform(get(SETTINGS_CONTROLLER_LINK + "/select")
            .param("tableName", NOT_EXISTS_TABLE_NAME)
            .param("page", String.valueOf(PAGE))
            .param("size", String.valueOf(SIZE))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void getSelectedWithNegativePageSizeTest() throws Exception {
        int negativePageSize = -1;

        mockMvc.perform(get(SETTINGS_CONTROLLER_LINK + "/select")
            .param("tableName", TABLE_NAME)
            .param("page", String.valueOf(PAGE))
            .param("size", String.valueOf(negativePageSize))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void getSelectedWithNegativePageNumberTest() throws Exception {
        int negativePageNumber = -1;

        mockMvc.perform(get(SETTINGS_CONTROLLER_LINK + "/select")
            .param("tableName", TABLE_NAME)
            .param("page", String.valueOf(negativePageNumber))
            .param("size", String.valueOf(SIZE))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void getSelectedWithOutOfLimitValueTest() throws Exception {
        int invalidLimit = 100_000;

        mockMvc.perform(get(SETTINGS_CONTROLLER_LINK + "/select")
            .param("tableName", TABLE_NAME)
            .param("page", String.valueOf(PAGE))
            .param("size", String.valueOf(invalidLimit))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void downloadExcelWithValidParamsTest() throws Exception {
        InputStream excelResource = new ByteArrayInputStream(new byte[] {1, 2, 3, 4, 5});
        when(exportSettingsService.getExcelFileAsResource(tableParams))
            .thenReturn(excelResource);

        mockMvc.perform(get(SETTINGS_CONTROLLER_LINK + "/download-table-data")
            .param("tableName", TABLE_NAME)
            .param("limit", String.valueOf(LIMIT))
            .param("offset", String.valueOf(OFFSET))
            .accept(MediaType.APPLICATION_OCTET_STREAM))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= users(1 - 10).xlsx"));
    }

    @Test
    void downloadExcelWithInvalidTableNameTest() throws Exception {
        mockMvc.perform(get(SETTINGS_CONTROLLER_LINK + "/download-table-data")
            .param("tableName", INVALID_TABLE_NAME)
            .param("limit", String.valueOf(LIMIT))
            .param("offset", String.valueOf(OFFSET))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void downloadExcelWithNonExistentTableNameTest() throws Exception {
        doThrow(new DatabaseMetadataException(ErrorMessage.SQL_METADATA_EXCEPTION_MESSAGE + NOT_EXISTS_TABLE_NAME))
            .when(exportSettingsService)
            .getExcelFileAsResource(tableParamsWithNotValidTableName);

        mockMvc.perform(get(SETTINGS_CONTROLLER_LINK + "/download-table-data")
            .param("tableName", NOT_EXISTS_TABLE_NAME)
            .param("limit", String.valueOf(LIMIT))
            .param("offset", String.valueOf(OFFSET))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void downloadExcelWithNegativeOffsetTest() throws Exception {
        int negativeOffset = -1;

        mockMvc.perform(get(SETTINGS_CONTROLLER_LINK + "/download-table-data")
            .param("tableName", TABLE_NAME)
            .param("limit", String.valueOf(LIMIT))
            .param("offset", String.valueOf(negativeOffset))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void downloadExcelWithNegativeLimitTest() throws Exception {
        int negativeLimit = -1;

        mockMvc.perform(get(SETTINGS_CONTROLLER_LINK + "/download-table-data")
            .param("tableName", TABLE_NAME)
            .param("limit", String.valueOf(negativeLimit))
            .param("offset", String.valueOf(OFFSET))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void downloadExcelWithOutOfLimitValueTest() throws Exception {
        int invalidLimit = 100_000;

        mockMvc.perform(get(SETTINGS_CONTROLLER_LINK + "/download-table-data")
            .param("tableName", TABLE_NAME)
            .param("limit", String.valueOf(invalidLimit))
            .param("offset", String.valueOf(OFFSET))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void getEnvVariablesTest() throws Exception {
        EnvironmentDto environmentDto = ModelUtils.getEnvironmentDto();
        when(exportSettingsService.getEnvironmentVariables()).thenReturn(environmentDto);
        String expectedJson = objectMapper.writeValueAsString(environmentDto);

        mockMvc.perform(get(SETTINGS_CONTROLLER_LINK + "/env")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }
}
