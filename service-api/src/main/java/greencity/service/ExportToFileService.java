package greencity.service;

import greencity.dto.exportsettings.TableRowsDto;
import java.io.InputStream;

public interface ExportToFileService {
    /**
     * Method for creating .xlsx file with data from DB and return InputStream with
     * file.
     *
     * @param data {@link TableRowsDto}
     *
     * @return {@link InputStream} stream with an excel file in it.
     */
    InputStream exportTableDataToExcel(TableRowsDto data);
}
