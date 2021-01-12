package greencity.client;

import greencity.ModelUtils;
import greencity.constant.RestTemplateLinks;
import greencity.dto.user.UserVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestClientTest {
    @Mock
    private RestTemplate restTemplate;
    @Value("${greencityuser.server.address}")
    private String greenCityUserServerAddress;
    @InjectMocks
    private RestClient restClient;

    @Test
    void findByEmail() {
        UserVO userVO = ModelUtils.getUserVO();
        when(restTemplate.getForObject(greenCityUserServerAddress
            + RestTemplateLinks.USER_FIND_BY_EMAIL + RestTemplateLinks.EMAIL + "taras@gmail.com", UserVO.class))
                .thenReturn(userVO);

        assertEquals(userVO, restClient.findByEmail("taras@gmail.com"));
    }
}
