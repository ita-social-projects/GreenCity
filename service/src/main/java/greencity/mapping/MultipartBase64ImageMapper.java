package greencity.mapping;

import greencity.exception.exceptions.NotSavedException;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;

/**
 * Class that is used by {@link ModelMapper} to map Base64 encoded image into
 * MultipartFile.
 */
@Component
public class MultipartBase64ImageMapper extends AbstractConverter<String, MultipartFile> {
    /**
     * Method for converting Base64 encoded image into MultipartFile.
     *
     * @param image encoded in Base64 format to convert.
     * @return image converted to MultipartFile.
     */
    @Override
    public MultipartFile convert(String image) {
        String imageToConvert = image.substring(image.indexOf(',') + 1);
        File tempFile = new File("tempImage.jpg");
        byte[] imageByte = decodeBase64(imageToConvert);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        try {
            BufferedImage bufferedImage = ImageIO.read(bis);
            ImageIO.write(bufferedImage, "png", tempFile);
            FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(tempFile.toPath()),
                false, tempFile.getName(), (int) tempFile.length(), tempFile.getParentFile());
            try (InputStream input = new FileInputStream(tempFile);
                OutputStream outputStream = fileItem.getOutputStream()) {
                IOUtils.copy(input, outputStream);
                outputStream.flush();
                return new CommonsMultipartFile(fileItem);
            }
        } catch (IOException e) {
            throw new NotSavedException("Cannot convert to BASE64 image");
        }
    }
}