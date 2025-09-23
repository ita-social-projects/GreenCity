package greencity.webcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.TestConst;
import greencity.client.RestClient;
import greencity.converters.UserArgumentResolver;
import greencity.converters.UserIdArgumentResolver;
import greencity.dto.PageableDetailedDto;
import greencity.dto.user.UserFilterDto;
import greencity.dto.user.UserFilterDtoRequest;
import greencity.dto.user.UserFilterDtoResponse;
import greencity.dto.user.UserManagementCreateDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.security.jwt.JwtTool;
import greencity.service.FilterService;
import greencity.service.HabitAssignService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import java.util.Map;
import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUser;
import static greencity.ModelUtils.getUserFilterDtoResponse;
import static greencity.ModelUtils.getUserPageableDetailedDto;
import static greencity.ModelUtils.getUserRoleBody;
import static greencity.ModelUtils.getUserStatusBody;
import static greencity.ModelUtils.getUserVO;
import static greencity.TestConst.MANAGEMENT_USER_LINK;
import static greencity.TestConst.ROLE_ADMIN;
import static greencity.TestConst.STATUS_ACTIVATED;
import static greencity.TestConst.TEST_QUERY;
import static greencity.TestConst.USER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManagementUserControllerTest {
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RestClient restClient;

    @Mock
    private UserService userService;

    @Mock
    HabitAssignService habitAssignService;

    @Mock
    private FilterService filterService;

    @Mock
    JwtTool jwtTool;

    @InjectMocks
    private ManagementUserController managementUserController;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(managementUserController)
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper),
                new UserIdArgumentResolver(jwtTool))
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void changeRoleTest() {
        Map<String, String> body = getUserRoleBody();
        managementUserController.changeRole(USER_ID, body);
        verify(restClient).updateRole(USER_ID, Role.ROLE_ADMIN);
    }

    @Test
    void changeStatusTest() {
        UserVO currentUser = getUserVO();
        Map<String, String> body = getUserStatusBody();
        managementUserController.changeStatus(USER_ID, body, currentUser);
        verify(userService).updateUserStatusById(currentUser, USER_ID, UserStatus.ACTIVATED);
    }

    @Test
    void getAllUsersTest() throws Exception {
        var userVO = getUserVO();
        var principal = getPrincipal();
        List<UserFilterDtoResponse> response = List.of(getUserFilterDtoResponse());

        PageableDetailedDto<UserManagementVO> userPageableDetailedDto = getUserPageableDetailedDto();

        when(userService.findNotDeactivatedByEmail(principal.getName()))
            .thenReturn(userVO);
        when(userService.getAllUsersByCriteria(any(UserFilterDto.class), any(Pageable.class)))
            .thenReturn(userPageableDetailedDto);
        when(filterService.getAllFilters(USER_ID)).thenReturn(response);

        mockMvc.perform(get(MANAGEMENT_USER_LINK + "?page=" + 0 + "&size=" + 20 + "&sort=id,DESC")
            .principal(principal)
            .param("status", STATUS_ACTIVATED)
            .param("role", ROLE_ADMIN)
            .param("query", TEST_QUERY))
            .andExpect(model().attribute("users", userPageableDetailedDto));

        verify(userService).getAllUsersByCriteria(any(UserFilterDto.class), any(Pageable.class));
        verify(filterService).getAllFilters(USER_ID);
    }

    @Test
    void getReasonsOfDeactivation() throws Exception {
        List<String> test = List.of("test", "test");
        UserVO currentUser = getUserVO();

        when(userService.findNotDeactivatedByEmail(currentUser.getEmail()))
            .thenReturn(currentUser);
        when(userService.getDeactivationReasons(1L, currentUser)).thenReturn(test);

        this.mockMvc.perform(get(MANAGEMENT_USER_LINK + "/reasons" + "?id=1")
            .principal(currentUser::getEmail)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(userService).getDeactivationReasons(1L, currentUser);
    }

    @Test
    void setActivatedStatus() throws Exception {
        UserVO currentUser = getUserVO();

        when(userService.findNotDeactivatedByEmail(currentUser.getEmail()))
            .thenReturn(currentUser);

        mockMvc.perform(post(MANAGEMENT_USER_LINK + "/activate" + "?id=1")
            .principal(currentUser::getEmail)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(userService).updateUserStatusById(currentUser, 1L, UserStatus.ACTIVATED);
    }

    @Test
    void getUserLang() throws Exception {
        this.mockMvc.perform(get(MANAGEMENT_USER_LINK + "/lang" + "?id=1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(restClient).getUserLang(1L);
    }

    @Test
    void deactivateUser() throws Exception {
        List<String> reasons = List.of("test", "test");
        String json = objectMapper.writeValueAsString(reasons);
        UserVO currentUser = getUserVO();

        when(userService.findNotDeactivatedByEmail(currentUser.getEmail()))
            .thenReturn(currentUser);

        mockMvc.perform(
            post(MANAGEMENT_USER_LINK + "/deactivate" + "?id=1")
                .principal(currentUser::getEmail)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(userService).deactivateUserByIdWithReasons(currentUser, 1L, reasons);
    }

    @Test
    void saveUserTest() throws Exception {
        UserManagementCreateDto dto = ModelUtils.getUserManagementCreateDto();

        mockMvc.perform(post(MANAGEMENT_USER_LINK + "/register")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("id", dto.getId().toString())
            .param("name", dto.getName())
            .param("email", dto.getEmail())
            .param("role", dto.getRole().toString()))
            .andExpect(status().is3xxRedirection());

        verify(restClient).managementRegisterUser(dto);
    }

    @Test
    void updateUserTest() throws Exception {
        UserManagementDto userManagementDto = ModelUtils.getUserManagementDto();
        String context = objectMapper.writeValueAsString(userManagementDto);

        mockMvc.perform(put(MANAGEMENT_USER_LINK + "/").content(context).accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        verify(restClient).updateUser(userManagementDto);
    }

    @Test
    void findFriendsByIdTest() throws Exception {
        mockMvc.perform(get(MANAGEMENT_USER_LINK + "/" + 1L + "/friends").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(restClient).findUserFriendsByUserId(1L);
    }

    @Test
    void deactivateAllTest() throws Exception {
        List<Long> list = List.of(1L, 2L);
        String context = objectMapper.writeValueAsString(list);
        UserVO currentUser = getUserVO();

        when(userService.findNotDeactivatedByEmail(currentUser.getEmail()))
            .thenReturn(currentUser);

        mockMvc.perform(post(MANAGEMENT_USER_LINK + "/deactivateAll")
            .principal(currentUser::getEmail)
            .content(context)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        context = objectMapper.writeValueAsString(null);
        mockMvc.perform(post(MANAGEMENT_USER_LINK + "/deactivateAll")
            .content(context)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(userService).deactivateAllUsers(list, currentUser);
    }

    @Test
    void updateUserRole() throws Exception {
        mockMvc.perform(
            put(MANAGEMENT_USER_LINK + "/updateToDoItem/" + 1L + "/" + 1L).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitAssignService).updateToDoItem(1L, 1L);
    }

    @Test
    void saveUserFilterTest() throws Exception {
        String jwt = "jwt";
        var principal = getPrincipal();
        UserFilterDtoRequest dto = UserFilterDtoRequest.builder().name("Test").userRole("ADMIN").userStatus("ACTIVATED")
            .searchCriteria("Test").build();
        UserVO userVO = getUserVO();
        User user = getUser();

        String content = objectMapper.writeValueAsString(dto);
        when(jwtTool.extractJwtFromNativeWebRequest(any()))
            .thenReturn(jwt);
        when(jwtTool.extractUserId(jwt))
            .thenReturn(TestConst.USER_ID);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);

        mockMvc.perform(post(MANAGEMENT_USER_LINK + "/filter-save").content(content).principal(principal)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isFound());

        verify(filterService).save(userVO.getId(), dto);
    }

    @Test
    void selectFilterTest() throws Exception {
        Long id = 1L;
        UserFilterDtoResponse dto = UserFilterDtoResponse.builder().id(1L).name("Test").userRole("ROLE_ADMIN")
            .userStatus("ACTIVATED").searchCriteria("Test").build();
        when(filterService.getFilterById(id)).thenReturn(dto);
        mockMvc.perform(get(MANAGEMENT_USER_LINK + "/select-filter/" + id).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isFound());

        verify(filterService).getFilterById(id);
    }

    @Test
    void deleteUserFilterTest() throws Exception {
        Long id = 1L;
        mockMvc.perform(get(MANAGEMENT_USER_LINK + "/" + id + "/delete-filter").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isFound());

        verify(filterService).deleteFilterById(id);
    }
}
