package greencity.service;

import java.io.IOException;
import java.net.URL;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public interface FileService {
    /**
     * Upload file to Google Cloud Storage.
     * @param multipartFile image file to save.
     * @return public image url.
     * @throws IOException exception.
     **/
    URL upload(MultipartFile multipartFile) throws IOException;
}
