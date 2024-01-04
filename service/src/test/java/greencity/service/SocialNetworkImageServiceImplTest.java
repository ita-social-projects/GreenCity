package greencity.service;

import greencity.dto.socialnetwork.SocialNetworkImageVO;
import greencity.entity.SocialNetworkImage;
import greencity.repository.SocialNetworkImageRepo;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;

@ExtendWith(MockitoExtension.class)
class SocialNetworkImageServiceImplTest {

    @Mock
    SocialNetworkImageRepo socialNetworkImageRepo;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    SocialNetworkImageServiceImpl socialNetworkImageService;

    @Test
    void getSocialNetworkImageByUrl() throws Exception {
        URL checkUrl = new URL("HTTP://");
        SocialNetworkImageVO socialNetworkImageVO = new SocialNetworkImageVO();
        socialNetworkImageVO.setId(1L);
        socialNetworkImageVO.setHostPath(checkUrl.getHost());
        socialNetworkImageVO.setImagePath("HTTP://");

        Optional<SocialNetworkImage> socialNetworkImage = Optional.of(new SocialNetworkImage());
        when(socialNetworkImageRepo.findByHostPath(null))
            .thenReturn(socialNetworkImage);
        when(modelMapper.map(any(SocialNetworkImage.class), eq(SocialNetworkImageVO.class)))
            .thenReturn(socialNetworkImageVO);

        when(modelMapper.map(socialNetworkImageVO, SocialNetworkImageVO.class)).thenReturn(socialNetworkImageVO);

        assertEquals(socialNetworkImageVO, socialNetworkImageService.getSocialNetworkImageByUrl("HTTP://"));
    }

    @Test
    void getSocialNetworkImageByUrlBadRequest() throws Exception {
        URI checkUrl = new URI("http://example.com");
        SocialNetworkImageVO socialNetworkImageVO = new SocialNetworkImageVO();
        socialNetworkImageVO.setId(1L);
        socialNetworkImageVO.setHostPath(checkUrl.getHost());
        socialNetworkImageVO.setImagePath(checkUrl.getPath());

        when(socialNetworkImageRepo.findByHostPath(checkUrl.getHost())).thenReturn(null);

        assertThrows(RuntimeException.class,
            () -> socialNetworkImageService.getSocialNetworkImageByUrl(checkUrl.toString()));
    }

    @Test
    void findByHostPath() throws Exception {
        URL checkUrl = new URL("HTTP://example.com/");
        SocialNetworkImageVO socialNetworkImageVO = new SocialNetworkImageVO();
        socialNetworkImageVO.setId(1L);
        socialNetworkImageVO.setHostPath(checkUrl.getHost());
        socialNetworkImageVO.setImagePath("HTTP://example.com/");

        Optional<SocialNetworkImage> socialNetworkImage = Optional.of(new SocialNetworkImage());
        when(socialNetworkImageRepo.findByHostPath(checkUrl.getHost()))
            .thenReturn(socialNetworkImage);

        when(modelMapper.map(any(SocialNetworkImage.class), eq(SocialNetworkImageVO.class)))
            .thenReturn(socialNetworkImageVO);

        assertEquals(Optional.of(socialNetworkImageVO), socialNetworkImageService.findByHostPath(checkUrl.getHost()));
    }

    @Test
    void getDefaultSocialNetworkImage() {
        SocialNetworkImageVO socialNetworkImageVO = new SocialNetworkImageVO();
        socialNetworkImageVO.setId(1L);
        socialNetworkImageVO.setHostPath("img/default_social_network_icon.png");
        socialNetworkImageVO.setImagePath("HTTP://img/default_social_network_icon.png/");

        Optional<SocialNetworkImage> socialNetworkImage = Optional.of(new SocialNetworkImage());
        when(socialNetworkImageRepo.findByHostPath(socialNetworkImageVO.getHostPath()))
            .thenReturn(socialNetworkImage);

        when(modelMapper.map(any(SocialNetworkImage.class), eq(SocialNetworkImageVO.class)))
            .thenReturn(socialNetworkImageVO);
        assertEquals(socialNetworkImageVO, socialNetworkImageService.getDefaultSocialNetworkImage());
    }
}
