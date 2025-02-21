package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.logs.LogFileRequestDto;
import greencity.dto.logs.LogFileMetadataDto;
import greencity.service.DotenvService;
import greencity.service.LogFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/logs")
@Profile({"dev", "test"})
public class LogFileController {
    private final LogFileService logFileService;
    private final DotenvService dotenvService;

    @Operation(summary = "Returns a list of log files metadata from project directory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(example = LogFileMetadataDto.defaultJson))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @ApiPageable
    @PostMapping
    public ResponseEntity<PageableDto<LogFileMetadataDto>> listLogFiles(
        @Schema(
            description = "Filters for logs",
            name = "LogFileFilterDto",
            type = "object",
            example = LogFileRequestDto.defaultJson) @RequestBody @Valid LogFileRequestDto requestDto,
        @Parameter(hidden = true) Pageable page) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(logFileService.listLogFiles(page, requestDto.filterDto(), requestDto.secretKey()));
    }

    @Operation(summary = "Returns content of a file with given filename",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = "text/plain",
                schema = @Schema(type = "string"))))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(example = "string"))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
        @ApiResponse(responseCode = "503", description = HttpStatuses.SERVICE_UNAVAILABLE,
            content = @Content(examples = @ExampleObject(HttpStatuses.SERVICE_UNAVAILABLE)))
    })
    @PostMapping("/view/{filename}")
    public ResponseEntity<String> viewLogFileContent(
        @RequestBody String secretKey,
        @PathVariable String filename) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(logFileService.viewLogFileContent(logFileService.sanitizeFilename(filename), secretKey));
    }

    @Operation(summary = "Returns a url that triggers file download in a browser",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = "text/plain",
                schema = @Schema(type = "string"))))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(example = HttpStatuses.OK))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @PostMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadLogFile(
        @RequestBody String secretKey,
        @PathVariable String filename) {
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + logFileService.sanitizeFilename(filename) + "\"")
            .body(logFileService.generateDownloadLogFileUrl(logFileService.sanitizeFilename(filename), secretKey));
    }

    @Operation(summary = "deletes '.env' file to make functionality that is dependent on it unavailable",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = "text/plain",
                schema = @Schema(type = "string"))))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(example = HttpStatuses.OK))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @PostMapping("/delete-dotenv")
    public ResponseEntity<Object> deleteDotenvFile(
        @RequestBody String secretKey) {
        dotenvService.deleteDotenvFile(secretKey);
        return ResponseEntity.ok().build();
    }
}