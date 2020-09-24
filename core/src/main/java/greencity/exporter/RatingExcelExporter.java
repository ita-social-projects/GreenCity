package greencity.exporter;

import greencity.dto.ratingstatistics.RatingStatisticsDto;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * This class is used for export {@link greencity.entity.RatingStatistics} data to Excel file.
 *
 * @author Dovganyuk Taras
 */
public class RatingExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<RatingStatisticsDto> ratingStatisticsDtos;

    /**
     * Constructor.
     *
     * @author Dovganyuk Taras
     */
    public RatingExcelExporter(List<RatingStatisticsDto> ratingStatisticsDtos) {
        this.ratingStatisticsDtos = ratingStatisticsDtos;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Rating");
    }

    private void writeHeaderRow() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);

        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue("ID");

        cell = row.createCell(1);
        cell.setCellStyle(style);
        cell.setCellValue("Event");

        cell = row.createCell(2);
        cell.setCellStyle(style);
        cell.setCellValue("Date");

        cell = row.createCell(3);
        cell.setCellStyle(style);
        cell.setCellValue("UserID");

        cell = row.createCell(4);
        cell.setCellStyle(style);
        cell.setCellValue("User email");

        cell = row.createCell(5);
        cell.setCellStyle(style);
        cell.setCellValue("PointsChanged");

        cell = row.createCell(6);
        cell.setCellStyle(style);
        cell.setCellValue("Current rating");
    }

    private void writeDataRows() {
        int rowCount = 1;

        for (RatingStatisticsDto dto : ratingStatisticsDtos) {
            Row row = sheet.createRow(rowCount++);

            Cell cell = row.createCell(0);
            cell.setCellValue(dto.getId());
            sheet.autoSizeColumn(0);

            cell = row.createCell(1);
            cell.setCellValue(dto.getRatingCalculationEnum().toString());
            sheet.autoSizeColumn(1);

            cell = row.createCell(2);
            cell.setCellValue(dto.getCreateDate().toString());
            sheet.autoSizeColumn(2);

            cell = row.createCell(3);
            cell.setCellValue(dto.getUser().getId());
            sheet.autoSizeColumn(3);

            cell = row.createCell(4);
            cell.setCellValue(dto.getUser().getEmail());
            sheet.autoSizeColumn(4);


            cell = row.createCell(5);
            cell.setCellValue(dto.getPointsChanged());
            sheet.autoSizeColumn(5);

            cell = row.createCell(6);
            cell.setCellValue(dto.getRating());
            sheet.autoSizeColumn(6);
        }
    }

    /**
     * Export {@link greencity.entity.RatingStatistics} data to Excel file.
     *
     * @author Dovganyuk Taras
     */
    public void export(HttpServletResponse response) throws IOException {
        writeHeaderRow();
        writeDataRows();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
