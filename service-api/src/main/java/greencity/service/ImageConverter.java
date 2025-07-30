package greencity.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageConverter {
    /**
     * Convert string to MultipartFile.
     *
     * @param image image to convert.
     * @return MultipartFile.
     **/
    MultipartFile convertToMultipartImage(String image);
}
