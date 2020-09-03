package greencity.service.impl;

import greencity.entity.SocialNetworkImage;
import greencity.repository.SocialNetworkImageRepo;
import greencity.service.FileService;
import greencity.service.SocialNetworkImageService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Service
public class SocialNetworkImageServiceImpl implements SocialNetworkImageService {
    @Autowired
    SocialNetworkImageRepo socialNetworkImageRepo;
    @Autowired
    FileService fileService;

    @Override
    public SocialNetworkImage getSocialNetworkImageByUrl(String url) {
        String preparedUrlHost = "https://greencity.azurewebsites.net/"; //default image url for bad argument
        try {
            preparedUrlHost = new URL(url).getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Optional<SocialNetworkImage> optionalSocialNetworkImage =
            socialNetworkImageRepo.findByBasePath(preparedUrlHost);
        if (optionalSocialNetworkImage.isPresent()) {
            return optionalSocialNetworkImage.get();
        }
        try {
            String preparedFaviconUrl = String.format("http://www.google.com/s2/favicons?domain_url=%s", URLEncoder
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
            SocialNetworkImage socialNetworkImage = SocialNetworkImage.builder()
                .basePath(preparedUrlHost)
                .imagePath(imagePath)
                .build();
            socialNetworkImageRepo.save(socialNetworkImage);
            return socialNetworkImage;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
