package greencity.controller;

import greencity.constant.ErrorMessage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.exportsettings.EnvironmentDto;
import greencity.dto.exportsettings.TableParamsRequestDto;
import greencity.dto.exportsettings.TableRowsDto;
import greencity.dto.exportsettings.TablesMetadataDto;
import greencity.service.ExportSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Validated
@Lazy
@RequestMapping("/export/settings")
public class ExportSettingsController {
    private final ExportSettingsService exportSettingsService;

    /**
     * Method for receiving all DB tables names.
     *
     * @return dto {@link TablesMetadataDto}
     */
    @Operation(summary = "Get all tables names and columns.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = TablesMetadataDto.class))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
    })
    @GetMapping(value = "/tables", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TablesMetadataDto> getTablesInfo() {
        return ResponseEntity.ok(exportSettingsService.getTablesMetadata());
    }

    /**
     * Method for receiving rows from DB by table name, limit and offset.
     *
     * @return dto {@link PageableAdvancedDto}
     */
    @Operation(summary = "Get table rows by params.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = TableRowsDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @GetMapping("/select")
    public ResponseEntity<PageableAdvancedDto<Map<String, String>>> selectFromTable(
        @Pattern(regexp = "^(?!_)[a-z]+(?:_[a-z]+){0,10}(?<!_)$",
            message = ErrorMessage.INVALID_TABLE_NAME) String tableName,
        @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(exportSettingsService.selectFromTable(tableName, pageable));
    }

    /**
     * Method for receiving an .xlsx file with rows from DB by table name, limit and
     * offset.
     *
     * @return the excel file as stream {@link InputStreamResource}
     */
    @Operation(summary = "Get excel file with table rows by params.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
    })
    @GetMapping("/download-table-data")
    public ResponseEntity<InputStreamResource> exportTableRowsAsExcel(@Valid TableParamsRequestDto tableParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
            String.format("attachment; filename= %s(%d - %d).xlsx", tableParams.tableName(), tableParams.offset(),
                tableParams.limit()));
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
            .headers(headers)
            .body(new InputStreamResource(
                exportSettingsService.getExcelFileAsResource(tableParams)));
    }

    /**
     * Method for receiving all environment variables use in the app.
     *
     * @return dto {@link EnvironmentDto}
     */
    @Operation(summary = "Get all environment variables")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = EnvironmentDto.class))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
    })
    @GetMapping("/env")
    public ResponseEntity<EnvironmentDto> getEnvVariables() {
        return ResponseEntity.ok(exportSettingsService.getEnvironmentVariables());
    }
}
