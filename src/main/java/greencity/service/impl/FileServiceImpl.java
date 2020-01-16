package greencity.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import greencity.service.FileService;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {
    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;

    /**
     * Constructor.
     */
    public FileServiceImpl(@Value("${cloud.name}")
                               String cloudName,
                           @Value("${api.key}")
                               String apiKey,
                           @Value("${api.secret}")
                               String apiSecret) {
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko and Yurii Olkhovskyi.
     */
    @Override
    public String uploadImage(MultipartFile imageToSave, String imageFolderName) {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret
        ));

        try {
            File fileToSave = Files.createTempFile("temp", imageToSave.getOriginalFilename()).toFile();
            imageToSave.transferTo(fileToSave);

            Map response = cloudinary.uploader().upload(fileToSave, ObjectUtils.asMap(
                "folder", imageFolderName));
            return response.get("secure_url").toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}


