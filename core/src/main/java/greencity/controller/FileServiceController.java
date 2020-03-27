package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.entity.EcoNews;
import greencity.service.FileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = EcoNews.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 500, message = HttpStatuses.INTERNAL_SERVER_ERROR),
    })
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestPart @NotEmpty MultipartFile image) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(fileService.upload(image).toString());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
