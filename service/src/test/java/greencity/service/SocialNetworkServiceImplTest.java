package greencity.service;

import greencity.ModelUtils;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.repository.SocialNetworkRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SocialNetworkServiceImplTest {

    @Mock
    private SocialNetworkRepo socialNetworkRepo;

    @InjectMocks
    private SocialNetworkServiceImpl socialNetworkService;

    @Test
    void deleteTest() {
        Long expected = 1L;
        Long actual = socialNetworkService.delete(expected);
        verify(socialNetworkRepo).deleteById(expected);
        assertEquals(expected, actual);
    }

    @Test
    void getSocialNetworkUrlByNameTest() {
        String expected = "url";
        List<SocialNetworkVO> socialNetworkVOs = ModelUtils.getListSocialNetworkVO();
        String actual = socialNetworkService.getSocialNetworkUrlByName(socialNetworkVOs, "url");
        assertEquals(expected, actual);
    }
}
