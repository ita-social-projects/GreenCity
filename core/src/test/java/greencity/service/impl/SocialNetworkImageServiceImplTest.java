package greencity.service.impl;

import greencity.entity.SocialNetworkImage;
import greencity.repository.SocialNetworkImageRepo;
import greencity.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SocialNetworkImageServiceImplTest {

    SocialNetworkImageServiceImpl socialNetworkImageService;
    @Mock
    SocialNetworkImageRepo socialNetworkImageRepo;
    @Mock
    FileService fileService;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        socialNetworkImageService= new SocialNetworkImageServiceImpl(socialNetworkImageRepo, fileService);
    }

    @Test
    void getSocialNetworkImageByUrl() throws Exception {
        URL checkUrl = new URL("http:");
        SocialNetworkImage socialNetworkImage = new SocialNetworkImage();
        socialNetworkImage.setId(1L);
        socialNetworkImage.setHostPath(checkUrl.getHost());
        socialNetworkImage.setImagePath("http:");

        when(socialNetworkImageRepo.findByHostPath(checkUrl.getHost())).thenReturn(Optional.of(socialNetworkImage));
        assertEquals(socialNetworkImage, socialNetworkImageService.getSocialNetworkImageByUrl("http:"));
    }

    @Test
    void getSocialNetworkImageByUrlBadRequest() throws Exception {
        URL checkUrl = new URL("HTTP:");
        SocialNetworkImage socialNetworkImage = new SocialNetworkImage();
        socialNetworkImage.setId(1L);
        socialNetworkImage.setHostPath(checkUrl.getHost());
        socialNetworkImage.setImagePath("HTTP:");

        when(socialNetworkImageRepo.findByHostPath(checkUrl.getHost())).thenReturn(Optional.ofNullable(null));

        assertThrows(RuntimeException.class, () ->
                socialNetworkImageService.getSocialNetworkImageByUrl("HTTP:")
        );
    }

    @Test
    void findByHostPath() throws Exception{
        URL checkUrl = new URL("HTTP://example.com/");
        SocialNetworkImage socialNetworkImage = new SocialNetworkImage();
        socialNetworkImage.setId(1L);
        socialNetworkImage.setHostPath(checkUrl.getHost());
        socialNetworkImage.setImagePath("HTTP://example.com/");

        when(socialNetworkImageRepo.findByHostPath(checkUrl.getHost())).thenReturn(Optional.of(socialNetworkImage));
        assertEquals(Optional.of(socialNetworkImage),socialNetworkImageService.findByHostPath(checkUrl.getHost()));
    }

    @Test
    void saveSocialNetworkImage() throws  Exception {
        URL checkUrl = new URL("http://example.com/");
        SocialNetworkImage socialNetworkImage = new SocialNetworkImage();
        socialNetworkImage.setId(1L);
        socialNetworkImage.setHostPath(checkUrl.getHost());
        socialNetworkImage.setImagePath("http://example.com/");


        when(socialNetworkImageRepo.findByHostPath(checkUrl.getHost())).thenReturn(Optional.of(socialNetworkImage));
        when(fileService.upload(any())).thenReturn(checkUrl);
        when(socialNetworkImageRepo.save(any())).thenReturn(socialNetworkImage);
        assertEquals(socialNetworkImage, socialNetworkImageService.saveSocialNetworkImage(checkUrl));
    }

    @Test
    void getDefaultSocialNetworkImage() throws Exception {
        SocialNetworkImage socialNetworkImage = new SocialNetworkImage();
        socialNetworkImage.setId(1L);
        socialNetworkImage.setHostPath("img/default_social_network_icon.png");
        socialNetworkImage.setImagePath("HTTP://img/default_social_network_icon.png/");

        when(socialNetworkImageRepo.findByHostPath(socialNetworkImage.getHostPath())).thenReturn(Optional.of(socialNetworkImage));
        assertEquals(socialNetworkImage, socialNetworkImageService.getDefaultSocialNetworkImage());

    }
}
