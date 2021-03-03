package greencity.webcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.RestClient;
import greencity.constant.RestTemplateLinks;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserManagementViewDto;
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
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManagementUserControllerTest {
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private RestClient restClient;
    @InjectMocks
    private ManagementUserController managementUserController;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private static final String managementUserLink = "/management/users";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(managementUserController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(restClient, modelMapper))
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllUsers() throws Exception {
        String test = "test";
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id").descending());
        List<UserManagementDto> userManagementDto = Collections.singletonList(new UserManagementDto());
        PageableAdvancedDto<UserManagementDto> userAdvancedDto =
            new PageableAdvancedDto<>(userManagementDto, 20, 0, 0, 0,
                true, true, true, true);
        when(restClient.findUserForManagementByPage(pageable)).thenReturn(userAdvancedDto);
        when(restClient.searchBy(pageable, test)).thenReturn(userAdvancedDto);
        mockMvc.perform(get(managementUserLink + "?query=" +
            "&page=" + 0 + "&size=" + 20 + "&sort=" + "id,DESC"))
            .andExpect(model().attribute("users", userAdvancedDto));
        mockMvc.perform(get(managementUserLink + "?query=" + test +
            "&page=" + 0 + "&size=" + 20 + "&sort=" + "id,DESC"));
        verify(restClient).findUserForManagementByPage(pageable);
        verify(restClient).searchBy(pageable, test);
    }

    @Test
    void searchTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 20, Sort.unsorted());
        UserManagementViewDto userViewDto =
            UserManagementViewDto.builder()
                .id("1L")
                .name("vivo")
                .email("test@ukr.net")
                .userCredo("Hello")
                .role("1")
                .userStatus("1")
                .build();
        String content = objectMapper.writeValueAsString(userViewDto);
        List<UserManagementVO> userManagementVOS = Collections.singletonList(new UserManagementVO());
        PageableAdvancedDto<UserManagementVO> userAdvancedDto =
            new PageableAdvancedDto<>(userManagementVOS, 20, 0, 0, 0,
                true, true, true, true);
        when(restClient.search(pageable, userViewDto)).thenReturn(userAdvancedDto);
        mockMvc.perform(post(managementUserLink + "/search")
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void getReasonsOfDeactivation() throws Exception {
        List<String> test = List.of("test", "test");
        when(restClient.getDeactivationReason(1L, "en")).thenReturn(test);
        this.mockMvc.perform(get(managementUserLink + "/reasons" + "?id=1" + "&lang=en")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(restClient).getDeactivationReason(1L, "en");
    }

    @Test
    void setActivatedStatus() throws Exception {
        mockMvc.perform(post(managementUserLink + "/activate" + "?id=1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(restClient).setActivatedStatus(1L);
    }

    @Test
    void getUserLang() throws Exception {
        this.mockMvc.perform(get(managementUserLink + "/lang" + "?id=1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(restClient).getUserLang(1L);
    }

    @Test
    void deactivateUser() throws Exception {
        List<String> test = List.of("test", "test");
        String json = objectMapper.writeValueAsString(test);
        mockMvc.perform(post(managementUserLink + "/deactivate" + "?id=1")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(restClient).deactivateUser(1L, test);
    }
}
