package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.exportsettings.EnvironmentDto;
import greencity.dto.exportsettings.TableParamsRequestDto;
import greencity.dto.exportsettings.TableRowsDto;
import greencity.dto.exportsettings.TablesMetadataDto;
import greencity.repository.ExportSettingsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Lazy
public class ExportSettingsServiceImpl implements ExportSettingsService {
    private final ExportSettingsRepo exportSettingsRepo;
    private final ExportToFileService exportToFileService;

    @Override
    public TablesMetadataDto getTablesMetadata() {
        return exportSettingsRepo.getTablesMetadata();
    }

    @Override
    @Transactional(readOnly = true)
    public PageableAdvancedDto<Map<String, String>> selectFromTable(String tableName, Pageable pageable) {
        int totalElements = exportSettingsRepo.countRowsInTable(tableName);
        TableRowsDto data = exportSettingsRepo.selectPortionFromTable(
            tableName, pageable.getPageSize(), (int) pageable.getOffset());

        return populatePageableDto(totalElements, pageable, data.tableData());
    }

    @Transactional(readOnly = true)
    @Override
    public InputStream getExcelFileAsResource(TableParamsRequestDto tableParams) {
        TableRowsDto data = exportSettingsRepo.selectPortionFromTable(tableParams.tableName(), tableParams.limit(),
            tableParams.offset());

        return exportToFileService.exportTableDataToExcel(data);
    }

    @Override
    public EnvironmentDto getEnvironmentVariables() {
        return new EnvironmentDto(System.getenv());
    }

    private PageableAdvancedDto<Map<String, String>> populatePageableDto(int totalElements, Pageable pageable,
        List<Map<String, String>> data) {
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
        boolean isFirst = pageable.getPageNumber() == 0;
        boolean isLast = pageable.getPageNumber() + 1 >= totalPages;

        return new PageableAdvancedDto<>(
            data,
            totalElements,
            pageable.getPageNumber(),
            totalPages,
            pageable.getPageNumber(),
            pageable.getPageNumber() > 0,
            !isLast,
            isFirst,
            isLast);
    }
}
