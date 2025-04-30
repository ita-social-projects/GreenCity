package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.exportsettings.EnvironmentDto;
import greencity.dto.exportsettings.TableParamsRequestDto;
import greencity.dto.exportsettings.TableRowsDto;
import greencity.dto.exportsettings.TablesMetadataDto;
import greencity.repository.ExportSettingsRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportSettingsServiceImplTest {
    private static final String TABLE_NAME = "users";
    private static final Pageable pageable = PageRequest.of(0, 10);
    private final TableParamsRequestDto tableParams = ModelUtils.tableParamsRequestDto();

    @InjectMocks
    private ExportSettingsServiceImpl settingsService;

    @Mock
    private ExportSettingsRepo exportSettingsRepo;

    @Mock
    private ExportToFileService exportToFileService;

    @Test
    void getTablesMetadataTest() {
        TablesMetadataDto tablesMetadataDto = ModelUtils.getTablesMetadataDto();
        when(exportSettingsRepo.getTablesMetadata()).thenReturn(tablesMetadataDto);

        TablesMetadataDto result = settingsService.getTablesMetadata();

        assertNotNull(result);
        verify(exportSettingsRepo, times(1)).getTablesMetadata();
    }

    @Test
    void selectFromTableWithValidParamsTest() {
        TableRowsDto tableRowsDto = ModelUtils.getTableRowsDto();
        when(exportSettingsRepo.selectPortionFromTable(TABLE_NAME, pageable.getPageSize(), (int) pageable.getOffset()))
            .thenReturn(tableRowsDto);

        PageableAdvancedDto<Map<String, String>> result = settingsService.selectFromTable(TABLE_NAME, pageable);

        assertFalse(result.getPage().isEmpty());
        verify(exportSettingsRepo, times(1)).selectPortionFromTable(TABLE_NAME, pageable.getPageSize(),
            (int) pageable.getOffset());
    }

    @Test
    void selectFromTableWithValidParamsAndResultMoreThenOnePageTest() {
        TableRowsDto tableRowsDto = populateTableRowDto();
        Pageable pageableForSecondPage = PageRequest.of(2, 2);
        when(exportSettingsRepo.selectPortionFromTable(TABLE_NAME, pageableForSecondPage.getPageSize(),
            (int) pageableForSecondPage.getOffset()))
            .thenReturn(tableRowsDto);
        when(exportSettingsRepo.countRowsInTable(TABLE_NAME)).thenReturn(tableRowsDto.tableData().size());

        PageableAdvancedDto<Map<String, String>> result =
            settingsService.selectFromTable(TABLE_NAME, pageableForSecondPage);

        assertEquals(result.getTotalElements(), tableRowsDto.tableData().size());
        verify(exportSettingsRepo, times(1)).selectPortionFromTable(TABLE_NAME, pageableForSecondPage.getPageSize(),
            (int) pageableForSecondPage.getOffset());
    }

    @Test
    void getExcelFileAsResourceWithValidParamsTest() {
        InputStream excelResource = new ByteArrayInputStream(new byte[] {1, 2, 3, 4, 5});
        TableRowsDto tableRowsDto = ModelUtils.getTableRowsDto();
        when(exportSettingsRepo.selectPortionFromTable(TABLE_NAME, tableParams.limit(), tableParams.offset()))
            .thenReturn(tableRowsDto);
        when(exportToFileService.exportTableDataToExcel(tableRowsDto)).thenReturn(excelResource);

        InputStream result = settingsService.getExcelFileAsResource(tableParams);

        assertNotNull(result);
        verify(exportToFileService, times(1)).exportTableDataToExcel(tableRowsDto);
    }

    @Test
    void getEnvironmentVariablesTest() {
        EnvironmentDto result = settingsService.getEnvironmentVariables();

        assertFalse(result.variables().isEmpty());
    }

    private TableRowsDto populateTableRowDto() {
        List<Map<String, String>> tableData = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("id", String.valueOf(i));
            row.put("date_of_registration", "1970-01-01 00:00:00");
            row.put("email", "someemail" + i + "@some.com");
            row.put("name", "Name" + i);
            row.put("role", "ROLE_USER");
            tableData.add(row);
        }
        return new TableRowsDto("users", tableData);
    }
}
