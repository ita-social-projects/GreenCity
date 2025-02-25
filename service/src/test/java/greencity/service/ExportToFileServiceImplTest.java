package greencity.service;

import greencity.ModelUtils;
import greencity.dto.exportsettings.TableRowsDto;
import greencity.exception.exceptions.FileGenerationException;
import greencity.exception.exceptions.InvalidLimitException;
import greencity.repository.ExportSettingsRepo;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ExportToFileServiceImplTest {
    private final String TABLE_NAME = "users";
    private final int LIMIT = 10;
    private final int OFFSET = 1;
    @InjectMocks
    private ExportToFileServiceImpl exportToFileService;;

    @Mock
    private ExportSettingsRepo exportSettingsRepo;

    @Test
    public void exportTableDataToExcelWithValidParamsTest() {
        TableRowsDto tableRowsDto = ModelUtils.getTableRowsDto();
        when(exportSettingsRepo.selectPortionFromTable(TABLE_NAME, OFFSET, LIMIT)).thenReturn(tableRowsDto);

        InputStream result = exportToFileService.exportTableDataToExcel(TABLE_NAME, OFFSET, LIMIT);

        assertNotNull(result);
        verify(exportSettingsRepo, times(1)).selectPortionFromTable(TABLE_NAME, OFFSET, LIMIT);
    }

    @Test
    public void exportTableDataToExcelWithOutOfLimitValueTest() throws Exception {
        int outOfLimit = 100_000;

        assertThrows(InvalidLimitException.class,
            () -> exportToFileService.exportTableDataToExcel(TABLE_NAME, outOfLimit, LIMIT));

        verify(exportSettingsRepo, times(0)).selectPortionFromTable(TABLE_NAME, OFFSET, outOfLimit);
    }

    @Test
    public void testExceptionCatchingDuringCreatingFileTest() throws Exception {
        Method method = ExportToFileServiceImpl.class.getDeclaredMethod("convertWorkbookToInputStream", Workbook.class);
        method.setAccessible(true);
        Workbook spyWorkbook = spy(new XSSFWorkbook());
        doThrow(new IOException("Some exception message")).when(spyWorkbook).write(any(ByteArrayOutputStream.class));

        try {
            method.invoke(exportToFileService, spyWorkbook);
            fail("Expected FileGenerationException to be thrown");
        } catch (InvocationTargetException e) {
            // Unwrap the InvocationTargetException to get the actual cause
            Throwable cause = e.getCause();
            assertInstanceOf(FileGenerationException.class, cause);
        }
    }
}
