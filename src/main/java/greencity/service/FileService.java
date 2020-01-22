package greencity.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public interface FileService {
    /**
     * Method for saving a photo.
     *
     * @param multipartFile   file for saving.
     * @param imageFolderName name of folder in which the photo will be saved.
     * @return url of the saved photo.
     */
    String uploadImage(MultipartFile multipartFile, String imageFolderName);
}
