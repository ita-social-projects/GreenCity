package greencity.service;

import greencity.ModelUtils;
import greencity.client.UserRemoteClient;
import greencity.dto.PageableDto;
import greencity.dto.socialnetwork.SocialNetworkImageRequestDTO;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

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

    @Test
    void saveTest() {
        SocialNetworkImageRequestDTO imageToSave = ModelUtils.getSocialNetworkImageRequestDTO();
        SocialNetworkImageResponseDTO expected = ModelUtils.getSocialNetworkImageResponseDTO();

        when(userRemoteClient.saveSocialImageRemote(imageToSave, null)).thenReturn(expected);

        SocialNetworkImageResponseDTO result = socialNetworkImageService.save(imageToSave, null);

        verify(userRemoteClient).saveSocialImageRemote(imageToSave, null);
        assertEquals(expected, result);
    }

    @Test
    void updateTest() {
        SocialNetworkImageResponseDTO toUpdate = ModelUtils.getSocialNetworkImageResponseDTO();

        doNothing().when(userRemoteClient).updateSocialImage(toUpdate, null);

        socialNetworkImageService.update(toUpdate, null);

        verify(userRemoteClient).updateSocialImage(toUpdate, null);
    }

    @Test
    void updateWithFileTest() {
        SocialNetworkImageResponseDTO toUpdate = ModelUtils.getSocialNetworkImageResponseDTO();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        doNothing().when(userRemoteClient).updateSocialImage(toUpdate, file);

        socialNetworkImageService.update(toUpdate, file);

        verify(userRemoteClient).updateSocialImage(toUpdate, file);
    }
}
