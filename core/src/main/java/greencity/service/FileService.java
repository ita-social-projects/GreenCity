package greencity.service;

import java.net.URL;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
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
