package greencity.service;

import greencity.ModelUtils;
import greencity.dto.exportsettings.TableRowsDto;
import greencity.dto.exportsettings.TablesMetadataDto;
import greencity.exception.exceptions.InvalidLimitException;
import greencity.repository.ExportSettingsRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExportSettingsServiceImplTest {
    private final String TABLE_NAME = "users";
    private final int LIMIT = 10;
    private final int OFFSET = 1;

    @InjectMocks
    private ExportSettingsServiceImpl settingsService;

    @Mock
    private ExportSettingsRepo exportSettingsRepo;

    @Mock
    private ExportToFileService exportToFileService;

    @Test
    public void getTablesMetadataTest() {
        TablesMetadataDto tablesMetadataDto = ModelUtils.getTablesMetadataDto();
        when(exportSettingsRepo.getTablesMetadata()).thenReturn(tablesMetadataDto);

        TablesMetadataDto result = settingsService.getTablesMetadata();

        assertNotNull(result);
        verify(exportSettingsRepo, times(1)).getTablesMetadata();
    }

    @Test
    public void selectFromTableWithWalidParamsTest() {
        TableRowsDto tableRowsDto = ModelUtils.getTableRowsDto();
        when(exportSettingsRepo.selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET)).thenReturn(tableRowsDto);

        TableRowsDto result = settingsService.selectFromTable(TABLE_NAME, LIMIT, OFFSET);

        assertNotNull(result);
        verify(exportSettingsRepo, times(1)).selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET);
    }

    @Test
    public void selectFromTableWithNegativeLimitTest() {
        int negativeLimit = -1;

        assertThrows(IllegalArgumentException.class,
            () -> settingsService.selectFromTable(TABLE_NAME, negativeLimit, OFFSET));

        verify(exportSettingsRepo, times(0)).selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET);
    }

    @Test
    public void selectFromTableWithNegativeOffsetTest() {
        int negativeOffset = -1;

        assertThrows(IllegalArgumentException.class,
            () -> settingsService.selectFromTable(TABLE_NAME, LIMIT, negativeOffset));

        verify(exportSettingsRepo, times(0)).selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET);
    }

    @Test
    public void selectFromTableWithOutOfLimitValueTest() {
        int invalidLimit = 100_000;

        assertThrows(InvalidLimitException.class,
            () -> settingsService.selectFromTable(TABLE_NAME, invalidLimit, OFFSET));

        verify(exportSettingsRepo, times(0)).selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET);
    }

    @Test
    public void getExcelFileAsResourceWithValidParamsTest() {
        InputStream excelResource = new ByteArrayInputStream(new byte[] {1, 2, 3, 4, 5});
        when(exportToFileService.exportTableDataToExcel(TABLE_NAME, LIMIT, OFFSET)).thenReturn(excelResource);

        InputStream result = settingsService.getExcelFileAsResource(TABLE_NAME, LIMIT, OFFSET);

        assertNotNull(result);
        verify(exportToFileService, times(1)).exportTableDataToExcel(TABLE_NAME, LIMIT, OFFSET);
    }
}
