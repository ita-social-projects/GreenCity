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
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        TableRowsDto emptyRow = new TableRowsDto("users", new LinkedList<>());

        assertThrows(ResourceNotFoundException.class,
            () -> exportToFileService.exportTableDataToExcel(emptyRow));
    }

    @Test
    void testExceptionCatchingDuringCreatingFileTest() throws Exception {
        Workbook spyWorkbook = spy(new XSSFWorkbook());
        doThrow(new IOException("Some exception message")).when(spyWorkbook).write(any(ByteArrayOutputStream.class));

        assertThrows(FileGenerationException.class,
            () -> exportToFileService.convertWorkbookToInputStream(spyWorkbook));
    }
}
