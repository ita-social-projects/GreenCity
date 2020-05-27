package greencity.service;

import java.net.URL;
import org.springframework.web.multipart.MultipartFile;


/**
 * Provides interface to save photo.
 */
public interface FileService {
    /**
     * Method saves photo to Cloud Storage.
     *
     * @param multipartfile  file.
     * @return saved photo's URL.
     */
    URL upload(final MultipartFile multipartfile);
}
