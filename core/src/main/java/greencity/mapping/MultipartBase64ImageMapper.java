package greencity.mapping;

import greencity.exception.exceptions.NotSavedException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static org.apache.commons.codec.binary.Base64.decodeBase64;

/**
 * Class that is used by {@link ModelMapper} to map Base64 encoded image into MultipartFile.
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
    protected MultipartFile convert(String image) {
        String imageToConvert = image.substring(image.indexOf(',') + 1);
        File tempFile = new File("tempImage.jpg");
        byte[] imageByte = decodeBase64(imageToConvert);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        try {
            BufferedImage bufferedImage = ImageIO.read(bis);
            ImageIO.write(bufferedImage, "png", tempFile);
            return new MockMultipartFile(tempFile.getPath(), new FileInputStream(tempFile));
        } catch (IOException e) {
            throw new NotSavedException("Cannot convert to BASE64 image");
        }
    }
}
