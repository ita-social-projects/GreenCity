package greencity.service;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.exportsettings.TableRowsDto;
import greencity.dto.exportsettings.TablesMetadataDto;
import greencity.exception.exceptions.InvalidLimitException;
import greencity.repository.ExportSettingsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class ExportSettingsServiceImpl implements ExportSettingsService {
    private final ExportSettingsRepo exportSettingsRepo;
    private final ExportToFileService exportToFileService;
    private final DotenvService dotenvService;

    @Override
    public TablesMetadataDto getTablesMetadata(String secretKey) {
        dotenvService.validateSecretKey(secretKey);
        return exportSettingsRepo.getTablesMetadata();
    }

    @Transactional(readOnly = true)
    @Override
    public TableRowsDto selectFromTable(String tableName, int limit, int offset, String secretKey) {
        dotenvService.validateSecretKey(secretKey);
        checkLimitAndOffset(limit, offset);

        return exportSettingsRepo.selectPortionFromTable(tableName, limit, offset);
    }

    @Override
    public InputStream getExcelFileAsResource(String tableName, int limit, int offset, String secretKey) {
        dotenvService.validateSecretKey(secretKey);
        checkLimitAndOffset(limit, offset);

        return exportToFileService.exportTableDataToExcel(tableName, limit, offset);
    }

    private void checkLimitAndOffset(int limit, int offset) {
        if (limit < 0) {
            throw new IllegalArgumentException(ErrorMessage.NEGATIVE_LIMIT);
        }
        if (limit > AppConstant.SQL_ROW_LIMIT) {
            throw new InvalidLimitException(String.format(ErrorMessage.EXCEED_LIMIT, AppConstant.SQL_ROW_LIMIT));
        }
        if (offset < 0) {
            throw new IllegalArgumentException(ErrorMessage.NEGATIVE_OFFSET);
        }
    }
}
