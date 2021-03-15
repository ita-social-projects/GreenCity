package greencity.service;

import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public interface FileService {
    /**
     * Upload file to Google Cloud Storage.
     *
     * @param multipartFile image file to save.
     * @return public image url.
     **/
    URL upload(MultipartFile multipartFile);

    /**
     * Convert string to MultipartFile.
     *
     * @return MultipartFile.
     **/
    MultipartFile convertToMultipartImage(String image);
}
