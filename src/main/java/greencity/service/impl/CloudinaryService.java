package greencity.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import greencity.exception.exceptions.NotSavedException;
import greencity.service.FileService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryService implements FileService {
    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;

    /**
     * Constructor.
     */
    public CloudinaryService(@Value("${cloud.name}")
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
        String fileName = UUID.randomUUID().toString();
        try {
            File fileToSave = Files.createTempFile("temp", fileName).toFile();
            imageToSave.transferTo(fileToSave);
            Map response = cloudinary.uploader().upload(fileToSave, ObjectUtils.asMap(
                "folder", imageFolderName));
            return response.get("secure_url").toString();
        } catch (IOException e) {
            throw new NotSavedException(e.getMessage());
        }
    }
}


