package greencity.service.impl;

import greencity.entity.SocialNetworkImage;
import greencity.repository.SocialNetworkImageRepo;
import greencity.service.FileService;
import greencity.service.SocialNetworkImageService;
import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.awt.image.ToolkitImage;
import sun.awt.image.URLImageSource;

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
        Optional<SocialNetworkImage> optionalSocialNetworkImage = socialNetworkImageRepo.findByBasePath(preparedUrlHost);
        if (optionalSocialNetworkImage.isPresent()) {
            return optionalSocialNetworkImage.get();
        }
        try {
            String preparedFaviconUrl = String.format("http://www.google.com/s2/favicons?domain_url=%s", URLEncoder
                .encode(preparedUrlHost, "UTF-8"));
            URL faviconUrl = new URL(preparedFaviconUrl);
            URLImageSource content = (URLImageSource) faviconUrl.getContent();
            ToolkitImage image = (ToolkitImage) Toolkit.getDefaultToolkit().createImage(content);
            System.out.println(image);

        } catch (UnsupportedEncodingException | MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        fileService.upload()
        return null;
    }
}
