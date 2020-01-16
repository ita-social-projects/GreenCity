package greencity.controller;

import static greencity.constant.AppConstant.VALIDATION_FOLDER;
import greencity.constant.HttpStatuses;
import greencity.entity.EcoNews;
import greencity.service.FileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
     * @param multipartFile file to save.
     * @param folderName    specified folder the image will be saved to.
     * @return url of the saved image.
     */
    @ApiOperation(value = "Upload an image.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = EcoNews.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("Image to save")@NotEmpty MultipartFile multipartFile,
                                              @ApiParam(value = "Folder name where the image will be saved('eco_news') "
                                                  + "The maximum size of an image is not more than 2 mb.")
                                              @RequestParam("folder name")
                                              @Pattern(regexp = VALIDATION_FOLDER, message = "invalid folder name")
                                              @NotNull(message = "can not be null") String folderName) {
        return ResponseEntity.status(HttpStatus.OK).body(fileService.uploadImage(multipartFile, folderName));
    }
}
