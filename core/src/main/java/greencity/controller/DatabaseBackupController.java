package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.service.DatabaseBackupService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller for handling database backup operations.
 */
@Controller
@RequestMapping("/database")
@AllArgsConstructor
public class DatabaseBackupController {
    private DatabaseBackupService databaseBackupService;

    /**
     * Endpoint to initiate the database backup process.
     *
     * @return ResponseEntity with a success message if the backup is completed
     *         successfully.
     */
    @ApiOperation(value = "Backup Database")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/backup")
    public ResponseEntity<String> backupDatabase() {
        databaseBackupService.backupDatabase();
        return ResponseEntity.ok("Database backup completed successfully!");
    }
}
