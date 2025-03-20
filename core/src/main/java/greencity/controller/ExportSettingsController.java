package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.exportsettings.EnvironmentDto;
import greencity.dto.exportsettings.TableParamsRequestDto;
import greencity.dto.exportsettings.TableRowsDto;
import greencity.dto.exportsettings.TablesMetadataDto;
import greencity.service.ExportSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

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
    public ResponseEntity<TablesMetadataDto> getTablesInfo(@RequestHeader String secretKey) {
        return ResponseEntity.ok(exportSettingsService.getTablesMetadata(secretKey));
    }

    /**
     * Method for receiving rows from DB by table name, limit and offset.
     *
     * @return dto {@link TableRowsDto}
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
    public ResponseEntity<TableRowsDto> selectFromTable(@Valid TableParamsRequestDto tableParams,
        @RequestHeader String secretKey) {
        return ResponseEntity.ok(exportSettingsService.selectFromTable(tableParams, secretKey));
    }

    /**
     * Method for receiving an .xlsx file with rows from DB by table name, limit and
     * offset.
     *
     * @return dto {@link TableRowsDto}
     */
    @Operation(summary = "Get excel file with table rows by params.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = TableRowsDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
    })
    @GetMapping("/download-table-data")
    public ResponseEntity<InputStreamResource> exportTableRowsAsExcel(@Valid TableParamsRequestDto tableParams,
        @RequestHeader String secretKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
            String.format("attachment; filename= %s(%d - %d).xlsx", tableParams.tableName(), tableParams.offset(),
                tableParams.limit()));
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
            .headers(headers)
            .body(new InputStreamResource(
                exportSettingsService.getExcelFileAsResource(tableParams, secretKey)));
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
    public ResponseEntity<EnvironmentDto> getEnvVariables(@RequestHeader String secretKey) {
        return ResponseEntity.ok(exportSettingsService.getEnvironmentVariables(secretKey));
    }
}
