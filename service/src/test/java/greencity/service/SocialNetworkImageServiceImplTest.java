package greencity.service;

import greencity.client.UserRemoteClient;
import greencity.dto.PageableDto;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SocialNetworkImageServiceImplTest {
    @Mock
    UserRemoteClient userRemoteClient;

    @InjectMocks
    SocialNetworkImageServiceImpl socialNetworkImageService;

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<SocialNetworkImageResponseDTO> expectedDTOs =
            List.of(new SocialNetworkImageResponseDTO(1L, "http://somepath.ua", "http://somehostpath.ua"));

        when(userRemoteClient.getAllSocialNetworkImagesRemote(pageable)).thenReturn(new PageableDto<>(
            expectedDTOs,
            expectedDTOs.size(),
            0,
            1));

        PageableDto<SocialNetworkImageResponseDTO> result = socialNetworkImageService.findAll(pageable);

        assertEquals(expectedDTOs.get(0), result.getPage().get(0));
    }

    @Test
    void deleteAllTest() {
        List<Long> toDelete = List.of(1L, 2L);
        when(userRemoteClient.deleteAllImages(toDelete)).thenReturn(toDelete);

        socialNetworkImageService.deleteAll(toDelete);
        verify(userRemoteClient).deleteAllImages(toDelete);
    }

    @Test
    void deleteTest() {
        Long toDelete = 2L;
        when(userRemoteClient.deleteSocialImage(toDelete)).thenReturn(toDelete);

        socialNetworkImageService.delete(toDelete);
        verify(userRemoteClient).deleteSocialImage(toDelete);
    }

    @Test
    void findDtoByIdTest() {
        Long toSearch = 2L;
        SocialNetworkImageResponseDTO expected =
            new SocialNetworkImageResponseDTO(1L, "http://somepath.ua", "http://somehostpath.ua");
        when(userRemoteClient.getEcoNewsById(toSearch)).thenReturn(expected);

        SocialNetworkImageResponseDTO result = socialNetworkImageService.findDtoById(toSearch);
        verify(userRemoteClient).getEcoNewsById(toSearch);
        assertEquals(expected, result);
    }
}
