package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.exportsettings.TableRowsDto;
import greencity.exception.exceptions.FileGenerationException;
import greencity.exception.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class ExportToFileServiceImpl implements ExportToFileService {
    @Transactional(readOnly = true)
    @Override
    public InputStream exportTableDataToExcel(TableRowsDto data) {
        if (data.tableData().isEmpty()) {
            throw new ResourceNotFoundException(String.format(ErrorMessage.EMPTY_TABLE, data.tableData()));
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(data.tableName());
        createHeaderRow(workbook, sheet, data);
        populateTableCells(workbook, sheet, data);
        return convertWorkbookToInputStream(workbook);
    }

    private void createHeaderRow(Workbook workbook, Sheet sheet, TableRowsDto data) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(0);
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);

        Set<String> raw = data.tableData().getFirst().keySet();
        int cellIndex = 0;
        for (String key : raw) {
            Cell headerCell = header.createCell(cellIndex++);
            headerCell.setCellValue(key);
            headerCell.setCellStyle(headerStyle);
        }
    }

    private void populateTableCells(Workbook workbook, Sheet sheet, TableRowsDto data) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        Row headerRow = sheet.getRow(0);
        List<Map<String, String>> tableData = data.tableData();

        int rowIndex = 1;
        for (Map<String, String> r : tableData) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0;

            for (Map.Entry<String, String> entry : r.entrySet()) {
                Cell headerCell = headerRow.getCell(cellIndex);
                if (headerCell != null && headerCell.getStringCellValue().equals(entry.getKey())) {
                    Cell cell = row.createCell(cellIndex);
                    cell.setCellValue(entry.getValue());
                    cell.setCellStyle(style);
                }
                cellIndex++;
            }
        }
    }

    InputStream convertWorkbookToInputStream(Workbook workbook) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);
            workbook.close();

            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileGenerationException(ErrorMessage.GENERATION_EXCEL_FILE_ERROR, e);
        }
    }
}
