package greencity.service.impl;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.entity.SocialNetworkImage;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.SocialNetworkImageRepo;
import greencity.service.FileService;
import greencity.service.SocialNetworkImageService;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Optional;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Service
@Slf4j
public class SocialNetworkImageServiceImpl implements SocialNetworkImageService {
    @Autowired
    SocialNetworkImageRepo socialNetworkImageRepo;
    @Autowired
    FileService fileService;

    @Override
    public SocialNetworkImage getSocialNetworkImageByUrl(String url) {
        try {
            URL checkUrl = new URL(url);
            Optional<SocialNetworkImage> optionalSocialNetworkImage =
                socialNetworkImageRepo.findByHostPath(checkUrl.getHost());
            if (optionalSocialNetworkImage.isPresent()) {
                return optionalSocialNetworkImage.get();
            } else {
                String imagePath = saveImageToCloud(checkUrl);
                SocialNetworkImage socialNetworkImage = SocialNetworkImage.builder()
                    .hostPath(checkUrl.getHost())
                    .imagePath(imagePath)
                    .build();
                socialNetworkImageRepo.save(socialNetworkImage);
                return socialNetworkImage;
            }
        } catch (MalformedURLException e) {
            log.info(e.getMessage());
            SocialNetworkImage socialNetworkImage =
                socialNetworkImageRepo.findByHostPath(AppConstant.DEFAULT_SOCIAL_NETWORK_IMAGE_URL)
                    .orElseThrow(() -> new RuntimeException(ErrorMessage.BAD_DEFAULT_SOCIAL_NETWORK_IMAGE_PATH));
            return socialNetworkImage;
        } catch (IOException e) {
            throw new NotSavedException(ErrorMessage.IMAGE_NOT_SAVED);
        }
    }

    private String saveImageToCloud(URL preparedUrl) throws IOException {
        String preparedUrlHost = preparedUrl.getHost();
        String preparedFaviconUrl = String.format("http://www.google.com/s2/favicons?sz=64&domain_url=%s", URLEncoder
            .encode(preparedUrlHost, "UTF-8"));
        URL faviconUrl = new URL(preparedFaviconUrl);
        BufferedImage bufferedImage = ImageIO.read(faviconUrl);
        File tempFile = new File("tempImage.png");
        ImageIO.write(bufferedImage, "png", tempFile);
        FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(tempFile.toPath()),
            false, tempFile.getName(), (int) tempFile.length(), tempFile.getParentFile());
        InputStream input = new FileInputStream(tempFile);
        OutputStream outputStream = fileItem.getOutputStream();
        int ret = input.read();
        while (ret != -1) {
            outputStream.write(ret);
            ret = input.read();
        }
        outputStream.flush();
        CommonsMultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        URL uploadCloud = fileService.upload(multipartFile);
        String imagePath = uploadCloud.toString();
        return imagePath;
    }
}
