package greencity.controller;

import greencity.service.impl.CloudStorageServiceImpl;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileServiceController {
    private final CloudStorageServiceImpl fileService;

    /**
     * Constructor.
     */
    @Autowired
    public FileServiceController(CloudStorageServiceImpl fileService) {
        this.fileService = fileService;
    }

    /**
     * Method for uploading an image.
     *
     * @param image image to save.
     * @return url of the saved image.
     */
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam @NotEmpty MultipartFile image) {
        return ResponseEntity.ok(fileService.upload(image).toString());
    }
}
