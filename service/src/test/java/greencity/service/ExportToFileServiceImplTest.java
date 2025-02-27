package greencity.service;

import greencity.ModelUtils;
import greencity.dto.exportsettings.TableRowsDto;
import greencity.exception.exceptions.FileGenerationException;
import greencity.exception.exceptions.ResourceNotFoundException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class ExportToFileServiceImplTest {
    @InjectMocks
    private ExportToFileServiceImpl exportToFileService;

    @Test
    void exportTableDataToExcelWithValidParamsTest() {
        TableRowsDto tableRowsDto = ModelUtils.getTableRowsDto();

        InputStream result = exportToFileService.exportTableDataToExcel(tableRowsDto);

        assertNotNull(result);
    }

    @Test
    void exportTableDataToExcelIfTableIsEmptyTest() {
        TableRowsDto emptyRow = TableRowsDto.builder().tableData(new LinkedList<>()).build();

        assertThrows(ResourceNotFoundException.class,
            () -> exportToFileService.exportTableDataToExcel(emptyRow));
    }

    @Test
    void testExceptionCatchingDuringCreatingFileTest() throws Exception {
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
