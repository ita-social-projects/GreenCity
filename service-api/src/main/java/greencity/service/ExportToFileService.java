package greencity.service;

import java.io.InputStream;

public interface ExportToFileService {
    /**
     * Method for creating .xlsx file with data from DB and return InputStream with
     * file.
     *
     * @param tableName {@link String}
     * @param limit     {@link int}
     * @param offset    {@link int}
     *
     * @return {@link InputStream} stream with an excel file in it.
     */
    InputStream exportTableDataToExcel(String tableName, int limit, int offset);
}
