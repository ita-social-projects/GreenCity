package greencity.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * Upload file to Azure Cloud Storage.
     *
     * @param multipartFile image file to save.
     * @return public image url.
     **/
    String upload(MultipartFile multipartFile);

    /**
     * Convert string to MultipartFile.
     *
     * @return MultipartFile.
     **/
    MultipartFile convertToMultipartImage(String image);

    /**
     * {@inheritDoc}
     */
    void delete(String path);
}
