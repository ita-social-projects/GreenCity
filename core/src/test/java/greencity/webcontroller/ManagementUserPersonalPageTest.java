package greencity.webcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.RestClient;
import greencity.converters.UserArgumentResolver;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserManagementViewDto;
import greencity.dto.user.UserVO;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ManagementUserPersonalPageTest {
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private RestClient restClient;
    @InjectMocks
    private ManagementUserPersonalPageController managementUserPersonalPageController;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private static final String managementUserLink = "/management/users";
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(managementUserPersonalPageController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getUserByIdTest() throws Exception {
        UserVO userVO =
            UserVO.builder()
                .id(1L)
                .name("vivo")
                .email("test@ukr.net")
                .userCredo("Hello")
                .role(Role.ROLE_USER)
                .userStatus(UserStatus.ACTIVATED)
                .build();
        String content = objectMapper.writeValueAsString(userVO);
        // List<UserManagementVO> userManagementVOS = Collections.singletonList(new
        // UserManagementVO());
        when(restClient.findById(1L)).thenReturn(userVO);
        mockMvc.perform(get(managementUserLink + "/" + userVO.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(restClient).findById(1L);
    }
}
