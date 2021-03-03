package greencity.service.impl;

import greencity.service.ChatFileService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.security.SecureRandom;

@Service
public class ChatFileServiceImpl implements ChatFileService {
    @Value("${fileFolder}")
    private String fileFolder;
    private static final String IMAGE_TYPE = "image";
    private static final String VIDEO_TYPE = "video";
    private static final String OTHER_TYPE = "doc";

    @Override
    public String save(String encodedString) throws IOException {
        String fileName = this.getUniqueName(encodedString);
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        File file = new File(fileFolder, fileName);
        FileUtils.writeByteArrayToFile(file, decodedBytes);
        return fileName;
    }

    @Override
    public String saveFileAndGetFileName(byte[] fileBytes, String originalName) throws IOException {
        String fileName = this.getUniqueName(originalName);
        File file = new File(fileFolder, fileName);
        FileUtils.writeByteArrayToFile(file, fileBytes);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            int read;
            byte[] data = new byte[300000];
            while ((read = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, read);
            }
        }
        return fileName;
    }

    @Override
    public byte[] getByteArrayFromFile(String fileName) throws IOException {
        File file = new File(fileFolder, fileName);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            int read;
            byte[] data = new byte[300000];
            while ((read = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, read);
            }
        }
        return buffer.toByteArray();
    }

    @Override
    public String getUniqueName(String originalName) {
        SecureRandom secureRandom = new SecureRandom();
        return String.format("%s%s", secureRandom.nextInt(), originalName);
    }

    @Override
    public String getFilteredFileType(String mediaType) {
        if (mediaType.contains(IMAGE_TYPE)) {
            return IMAGE_TYPE;
        }
        if (mediaType.contains(VIDEO_TYPE)) {
            return VIDEO_TYPE;
        }
        return OTHER_TYPE;
    }

    @Override
    public Resource getFileResource(String fileName) throws IOException {
        Path path = Paths.get(fileFolder + fileName);
        return new ByteArrayResource(Files.readAllBytes(path));
    }
}
