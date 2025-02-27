package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.exportsettings.TableRowsDto;
import greencity.dto.exportsettings.TablesMetadataDto;
import greencity.exception.exceptions.BadSecretKeyException;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportSettingsServiceImplTest {
    private static final String TABLE_NAME = "users";
    private static final int LIMIT = 10;
    private static final int OFFSET = 1;
    private static final String SECRET_KEY = "SomeSecretKey";
    private static final String NOT_VALID_SECRET_KEY = "SomeNotValidSecretKey";

    @InjectMocks
    private ExportSettingsServiceImpl settingsService;

    @Mock
    private ExportSettingsRepo exportSettingsRepo;

    @Mock
    private ExportToFileService exportToFileService;

    @Mock
    private DotenvService dotenvService;

    @Test
    void getTablesMetadataTest() {
        TablesMetadataDto tablesMetadataDto = ModelUtils.getTablesMetadataDto();
        doNothing().when(dotenvService).validateSecretKey(SECRET_KEY);
        when(exportSettingsRepo.getTablesMetadata()).thenReturn(tablesMetadataDto);

        TablesMetadataDto result = settingsService.getTablesMetadata(SECRET_KEY);

        assertNotNull(result);
        verify(exportSettingsRepo, times(1)).getTablesMetadata();
    }

    @Test
    void getTablesMetadataWithNotWalidSecretKeyTest() {
        doThrow(new BadSecretKeyException(ErrorMessage.BAD_SECRET_KEY)).when(dotenvService)
            .validateSecretKey(NOT_VALID_SECRET_KEY);

        assertThrows(BadSecretKeyException.class,
            () -> settingsService.getTablesMetadata(NOT_VALID_SECRET_KEY));

        verify(exportSettingsRepo, times(0)).getTablesMetadata();
    }

    @Test
    void selectFromTableWithWalidParamsTest() {
        TableRowsDto tableRowsDto = ModelUtils.getTableRowsDto();
        doNothing().when(dotenvService).validateSecretKey(SECRET_KEY);
        when(exportSettingsRepo.selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET)).thenReturn(tableRowsDto);

        TableRowsDto result = settingsService.selectFromTable(TABLE_NAME, LIMIT, OFFSET, SECRET_KEY);

        assertNotNull(result);
        verify(exportSettingsRepo, times(1)).selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET);
    }

    @Test
    void selectFromTableWithNegativeLimitTest() {
        int negativeLimit = -1;
        doNothing().when(dotenvService).validateSecretKey(SECRET_KEY);

        assertThrows(IllegalArgumentException.class,
            () -> settingsService.selectFromTable(TABLE_NAME, negativeLimit, OFFSET, SECRET_KEY));

        verify(exportSettingsRepo, times(0)).selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET);
    }

    @Test
    void selectFromTableWithNegativeOffsetTest() {
        int negativeOffset = -1;
        doNothing().when(dotenvService).validateSecretKey(SECRET_KEY);

        assertThrows(IllegalArgumentException.class,
            () -> settingsService.selectFromTable(TABLE_NAME, LIMIT, negativeOffset, SECRET_KEY));

        verify(exportSettingsRepo, times(0)).selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET);
    }

    @Test
    void selectFromTableWithOutOfLimitValueTest() {
        int invalidLimit = 100_000;
        doNothing().when(dotenvService).validateSecretKey(SECRET_KEY);

        assertThrows(InvalidLimitException.class,
            () -> settingsService.selectFromTable(TABLE_NAME, invalidLimit, OFFSET, SECRET_KEY));

        verify(exportSettingsRepo, times(0)).selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET);
    }

    @Test
    void selectFromTableWithNotValidSecretKeyTest() {
        doThrow(new BadSecretKeyException(ErrorMessage.BAD_SECRET_KEY)).when(dotenvService)
            .validateSecretKey(NOT_VALID_SECRET_KEY);

        assertThrows(BadSecretKeyException.class,
            () -> settingsService.selectFromTable(TABLE_NAME, LIMIT, OFFSET, NOT_VALID_SECRET_KEY));

        verify(exportSettingsRepo, times(0)).selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET);
    }

    @Test
    void getExcelFileAsResourceWithValidParamsTest() {
        InputStream excelResource = new ByteArrayInputStream(new byte[] {1, 2, 3, 4, 5});
        TableRowsDto tableRowsDto = ModelUtils.getTableRowsDto();
        doNothing().when(dotenvService).validateSecretKey(SECRET_KEY);
        when(exportSettingsRepo.selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET)).thenReturn(tableRowsDto);
        when(exportToFileService.exportTableDataToExcel(tableRowsDto)).thenReturn(excelResource);

        InputStream result = settingsService.getExcelFileAsResource(TABLE_NAME, LIMIT, OFFSET, SECRET_KEY);

        assertNotNull(result);
        verify(exportToFileService, times(1)).exportTableDataToExcel(tableRowsDto);
    }

    @Test
    void getExcelFileAsResourceWithNotValidSecretKeyTest() {
        doThrow(new BadSecretKeyException(ErrorMessage.BAD_SECRET_KEY)).when(dotenvService)
            .validateSecretKey(NOT_VALID_SECRET_KEY);

        assertThrows(BadSecretKeyException.class,
            () -> settingsService.getExcelFileAsResource(TABLE_NAME, LIMIT, OFFSET, NOT_VALID_SECRET_KEY));

        verify(exportSettingsRepo, times(0)).selectPortionFromTable(TABLE_NAME, LIMIT, OFFSET);
    }
}
