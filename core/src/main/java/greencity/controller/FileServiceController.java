package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.service.FileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileServiceController {
    private final FileService fileService;

    /**
     * Constructor.
     */
    @Autowired
    public FileServiceController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Method for uploading an image.
     *
     * @param image image to save.
     * @return url of the saved image.
     */
    @ApiOperation(value = "Upload an image.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = String.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 500, message = HttpStatuses.INTERNAL_SERVER_ERROR),
    })
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam @NotEmpty MultipartFile image) {
        return ResponseEntity.status(HttpStatus.OK).body(fileService.upload(image).toString());
    }
}
