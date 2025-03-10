package greencity.service;

import greencity.dto.exportsettings.TableParamsRequestDto;
import greencity.dto.exportsettings.TableRowsDto;
import greencity.dto.exportsettings.TablesMetadataDto;
import greencity.repository.ExportSettingsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;

@RequiredArgsConstructor
@Service
@Lazy
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
    public TableRowsDto selectFromTable(TableParamsRequestDto tableParams, String secretKey) {
        dotenvService.validateSecretKey(secretKey);

        return exportSettingsRepo.selectPortionFromTable(tableParams.tableName(), tableParams.limit(),
            tableParams.offset());
    }

    @Transactional(readOnly = true)
    @Override
    public InputStream getExcelFileAsResource(TableParamsRequestDto tableParams, String secretKey) {
        dotenvService.validateSecretKey(secretKey);
        TableRowsDto data = exportSettingsRepo.selectPortionFromTable(tableParams.tableName(), tableParams.limit(),
            tableParams.offset());

        return exportToFileService.exportTableDataToExcel(data);
    }
}
