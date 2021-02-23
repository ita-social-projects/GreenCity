package greencity.service.impl;

import greencity.service.ChatFileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.security.SecureRandom;

@Service
public class ChatImageServiceImpl implements ChatFileService {
    @Value("${fileFolder}")
    private String fileFolder;

    @Override
    public String save(String encodedString) throws IOException {
        String fileName = this.getUniqueName();
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        File file = new File(fileFolder, fileName);
        FileUtils.writeByteArrayToFile(file, decodedBytes);
        return fileName;
    }

    @Override
    public byte[] getByteArrayFromFile(String fileName) throws IOException {
        File file = new File(fileFolder + fileName);
        FileInputStream inputStream = new FileInputStream(file);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int read;
        byte[] data = new byte[300000];
        while ((read = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, read);
        }
        return buffer.toByteArray();
    }

    @Override
    public String getUniqueName() {
        String ext = "png";
        SecureRandom secureRandom = new SecureRandom();
        return String.format("%s%s.%s", secureRandom.nextInt(), RandomStringUtils.randomAlphanumeric(8), ext);
    }
}
