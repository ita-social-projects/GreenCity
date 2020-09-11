package greencity.service.impl;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.NotSavedException;
import greencity.service.FileService;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.UUID;
import javax.imageio.ImageIO;
import liquibase.pro.packaged.I;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import static org.apache.commons.codec.binary.Base64.decodeBase64;

@Service
public class CloudStorageService implements FileService {
    private final String staticUrl;
    private final String bucketName;
    private final Storage storage;


    /**
     * Constructor with parameters.
     */
    public CloudStorageService(@Value("${bucketName}") final String bucketName,
                               @Value("${staticUrl}") final String staticUrl) {
        this.bucketName = bucketName;
        this.staticUrl = staticUrl;
        this.storage = StorageOptions.newBuilder().build().getService();
    }

    /**
     * {@inheritDoc}
     */
    public URL upload(final MultipartFile multipartFile) {
        try {
            final String contentType = multipartFile.getContentType();
            final String blob = UUID.randomUUID().toString();
            final BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blob).setContentType(contentType).build();

            return write(blobInfo, multipartFile);
        } catch (IOException ex) {
            throw new NotSavedException(ErrorMessage.FILE_NOT_SAVED);
        }
    }

    private URL write(final BlobInfo blobInfo, final MultipartFile multipartFile) throws IOException {
        try (WriteChannel writer = storage.writer(blobInfo)) {
            final byte[] buffer = new byte[1024];
            InputStream input = multipartFile.getInputStream();
            int limit;
            while ((limit = input.read(buffer)) >= 0) {
                writer.write(ByteBuffer.wrap(buffer, 0, limit));
            }
        }
        return getURL(blobInfo);
    }

    private URL getURL(BlobInfo blobInfo) throws MalformedURLException {
        return new URL(staticUrl + blobInfo.getBucket() + "/" + blobInfo.getName());
    }

    /**
     * Convert string to MultipartFile.
     *
     * @return MultipartFile.
     **/
    public MultipartFile convertToMultipartImage(String image) {
        String imageToConvert = image.substring(image.indexOf(',') + 1);
        File tempFile = new File("tempImage.jpg");
        byte[] imageByte = decodeBase64(imageToConvert);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        try (InputStream input = new FileInputStream(tempFile)) {
            BufferedImage bufferedImage = ImageIO.read(bis);
            ImageIO.write(bufferedImage, "png", tempFile);
            FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(tempFile.toPath()),
                false, tempFile.getName(), (int) tempFile.length(), tempFile.getParentFile());
            OutputStream outputStream = fileItem.getOutputStream();
            int ret = input.read();
            while (ret != -1) {
                outputStream.write(ret);
                ret = input.read();
            }
            outputStream.flush();
            return new CommonsMultipartFile(fileItem);
        } catch (IOException e) {
            throw new NotSavedException("Cannot convert BASE64 image");
        }
    }
}