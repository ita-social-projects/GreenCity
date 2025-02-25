package greencity.service;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.exportsettings.TableRowsDto;
import greencity.exception.exceptions.FileGenerationException;
import greencity.exception.exceptions.InvalidLimitException;
import greencity.repository.ExportSettingsRepo;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Service
public class ExportToFileServiceImpl implements ExportToFileService {
    private final ExportSettingsRepo exportSettingsRepo;

    @Transactional(readOnly = true)
    @Override
    public InputStream exportTableDataToExcel(String tableName, int limit, int offset) {
        if (limit > AppConstant.SQL_ROW_LIMIT) {
            log.warn("Table data exceeds limit: {}", limit);
            throw new InvalidLimitException(String.format(ErrorMessage.EXCEED_LIMIT, limit));
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(tableName);
        TableRowsDto data = exportSettingsRepo.selectPortionFromTable(tableName, limit, offset);
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

        Set<String> raw = data.getTableData().getFirst().keySet();
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
        List<Map<String, String>> tableData = data.getTableData();

        int rowIndex = 1; // Start after the header row
        for (Map<String, String> r : tableData) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0; // Reset the cell index for each new row

            for (Map.Entry<String, String> entry : r.entrySet()) {
                // Check if the current header matches the entry's key
                Cell headerCell = headerRow.getCell(cellIndex);
                if (headerCell != null && headerCell.getStringCellValue().equals(entry.getKey())) {
                    // Create a new cell in the current row
                    Cell cell = row.createCell(cellIndex);
                    cell.setCellValue(entry.getValue());
                    cell.setCellStyle(style);
                }
                cellIndex++;
            }
        }
    }

    private InputStream convertWorkbookToInputStream(Workbook workbook) {
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
