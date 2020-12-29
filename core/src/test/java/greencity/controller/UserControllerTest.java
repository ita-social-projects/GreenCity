package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.goal.BulkCustomGoalDto;
import greencity.dto.goal.BulkSaveCustomGoalDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserUpdateDto;
import greencity.dto.user.UserVO;
import greencity.enums.Role;
import greencity.service.CustomGoalService;
import greencity.service.HabitAssignService;
import greencity.service.UserService;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserControllerTest {
    private static final String userLink = "/user";
    private MockMvc mockMvc;
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    @Mock
    private HabitAssignService habitAssignService;
    @Mock
    private CustomGoalService customGoalService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void updateStatusTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        String content = "{\n"
            + "  \"id\": 0,\n"
            + "  \"userStatus\": \"BLOCKED\"\n"
            + "}";

        mockMvc.perform(patch(userLink + "/status")
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        UserStatusDto userStatusDto =
            mapper.readValue(content, UserStatusDto.class);

        verify(userService).updateStatus(eq(userStatusDto.getId()),
            eq(userStatusDto.getUserStatus()), eq("testmail@gmail.com"));
    }

    @Test
    void updateStatusBadRequestTest() throws Exception {
        mockMvc.perform(patch(userLink + "/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateRoleTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        String content = "{\n"
            + "  \"id\": 1,\n"
            + "  \"role\": \"ROLE_USER\"\n"
            + "}";

        mockMvc.perform(patch(userLink + "/role")
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();

        verify(userService).updateRole(eq(1L), eq(Role.ROLE_USER), eq("testmail@gmail.com"));
    }

    @Test
    void updateRoleBadRequestTest() throws Exception {
        mockMvc.perform(patch(userLink + "/role")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUsersTest() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(userLink + "/all?page=1"))
            .andExpect(status().isOk());

        verify(userService).findByPage(eq(pageable));
    }

    @Test
    void findUsersRecommendedFriendsTest() throws Exception {
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(userLink + "/{userId}/recommendedFriends/", 1))
            .andExpect(status().isOk());

        verify(userService).findUsersRecommendedFriends(eq(pageable), eq(1L));
    }

    @Test
    void findAllUsersFriendsTest() throws Exception {
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(userLink + "/{userId}/friends/", 1))
            .andExpect(status().isOk());

        verify(userService).findAllUsersFriends(eq(pageable), eq(1L));
    }

    @Test
    void findAllUsersFriendRequestTest() throws Exception {
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(userLink + "/{userId}/friendRequests/", 1))
            .andExpect(status().isOk());

        verify(userService).getAllUserFriendRequests(eq(1L), eq(pageable));
    }

    @Test
    void getRolesTest() throws Exception {
        mockMvc.perform(get(userLink + "/roles"))
            .andExpect(status().isOk());

        verify(userService).getRoles();
    }

    @Test
    void getEmailNotificationsTest() throws Exception {
        mockMvc.perform(get(userLink + "/emailNotifications"))
            .andExpect(status().isOk());

        verify(userService).getEmailNotificationsStatuses();
    }

    @Test
    void getUsersByFilterTest() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        String content = "{\n"
            + "  \"searchReg\": \"string\"\n"
            + "}";

        mockMvc.perform(post(userLink + "/filter?page=1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        FilterUserDto filterUserDto =
            mapper.readValue(content, FilterUserDto.class);

        verify(userService).getUsersByFilter(eq(filterUserDto), eq(pageable));
    }

    @Test
    void getUserByPrincipalTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        mockMvc.perform(get(userLink)
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).getUserUpdateDtoByEmail(eq("testmail@gmail.com"));
    }

    @Test
    void updateUserTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        String content = "{\n"
            + "  \"emailNotification\": \"DISABLED\",\n"
            + "  \"name\": \"string\"\n"
            + "}";

        ObjectMapper mapper = new ObjectMapper();
        UserUpdateDto userUpdateDto =
            mapper.readValue(content, UserUpdateDto.class);

        mockMvc.perform(patch(userLink)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        verify(userService).update(eq(userUpdateDto), eq("testmail@gmail.com"));
    }

    @Test
    void findAllByUserTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/customGoals", 1))
            .andExpect(status().isOk());

        verify(customGoalService).findAllByUser(eq(1L));
    }

    @Test
    void saveUserCustomGoalsTest() throws Exception {
        UserVO user = ModelUtils.getUserVO();
        when(userService.findById(1L)).thenReturn(user);

        String content = "{\n"
            + "  \"customGoalSaveRequestDtoList\": [\n"
            + "    {\n"
            + "      \"text\": \"string\"\n"
            + "    }\n"
            + "  ]\n"
            + "}";

        ObjectMapper mapper = new ObjectMapper();
        BulkSaveCustomGoalDto dto = mapper.readValue(content, BulkSaveCustomGoalDto.class);

        mockMvc.perform(post(userLink + "/{userId}/customGoals", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        verify(customGoalService).save(eq(dto), eq(user.getId()));
    }

    @Test
    void updateBulkTest() throws Exception {
        String content = "{\n"
            + "  \"customGoals\": [\n"
            + "    {\n"
            + "      \"id\": 1,\n"
            + "      \"text\": \"string\"\n"
            + "    }\n"
            + "  ]\n"
            + "}";

        mockMvc.perform(patch(userLink + "/{userId}/customGoals", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        BulkCustomGoalDto dto =
            mapper.readValue(content, BulkCustomGoalDto.class);

        verify(customGoalService).updateBulk(eq(dto));
    }

    @Test
    void bulkDeleteCustomGoalsTest() throws Exception {
        mockMvc.perform(delete(userLink + "/{userId}/customGoals?ids=123", 1))
            .andExpect(status().isOk());

        verify(customGoalService).bulkDelete(eq("123"));
    }

    @Test
    void getAvailableCustomGoalsTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/customGoals/available", 1))
            .andExpect(status().isOk());

        verify(userService).getAvailableCustomGoals(eq(1L));
    }

    /*
     * @Test void getAvailableHabitDictionaryTest() throws Exception {
     * mockMvc.perform(get(userLink +
     * "/{userId}/habit-dictionary/available?language=en", 1))
     * .andExpect(status().isOk());
     *
     * verify(userService).getAvailableHabitDictionary(eq(1L), eq("en")); }
     */

    /*
     * @Test void saveUserHabitsTest() throws Exception { String content = "[\n" +
     * "  {\n" + "    \"habitDictionaryId\": 0\n" + "  }\n" + "]";
     *
     * mockMvc.perform(post(userLink + "/{userId}/habit?language=en", 1)
     * .contentType(MediaType.APPLICATION_JSON) .content(content))
     * .andExpect(status().isCreated());
     *
     * ObjectMapper mapper = new ObjectMapper(); List<HabitIdDto> dto =
     * mapper.readValue(content, new TypeReference<List<HabitIdDto>>() { });
     *
     * verify(userService).createUserHabit(eq(1L), eq(dto), eq("en")); }
     */

    /*
     * @Test void deleteHabitTest() throws Exception {
     * mockMvc.perform(delete(userLink + "/{userId}/habit/{habitId}", 1, 1))
     * .andExpect(status().isOk());
     *
     * verify(userService).deleteHabitByUserIdAndHabitDictionary(eq(1L), eq(1L)); }
     */

    @Test
    void getActivatedUsersAmountTest() throws Exception {
        mockMvc.perform(get(userLink + "/activatedUsersAmount"))
            .andExpect(status().isOk());

        verify(userService).getActivatedUsersAmount();
    }

    @Test
    void updateUserProfilePictureTest() throws Exception {
        UserVO user = ModelUtils.getUserVO();
        Principal principal = mock(Principal.class);

        String json = "{\n"
            + "\t\"id\": 1,\n"
            + "\t\"profilePicturePath\": \"ima\""
            + "}";

        MockMultipartFile jsonFile = new MockMultipartFile("userProfilePictureDto", "",
            "application/json", json.getBytes());

        when(principal.getName()).thenReturn("testmail@gmail.com");
        when(userService.updateUserProfilePicture(null, "testmail@gmail.com",
            ModelUtils.getUserProfilePictureDto())).thenReturn(user);

        MockMultipartHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.multipart(userLink + "/profilePicture");
        builder.with(request -> {
            request.setMethod("PATCH");
            return request;
        });

        this.mockMvc.perform(builder
            .file(jsonFile)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void deleteUserFriendTest() throws Exception {
        mockMvc.perform(delete(userLink + "/{userId}/userFriend/{friendId}", 1, 1))
            .andExpect(status().isOk());

        verify(userService).deleteUserFriendById(eq(1L), eq(1L));
    }

    @Test
    void addNewFriendTest() throws Exception {
        mockMvc.perform(post(userLink + "/{userId}/userFriend/{friendId}", 1, 1))
            .andExpect(status().isOk());

        verify(userService).addNewFriend(eq(1L), eq(1L));
    }

    @Test
    void acceptFriendRequestTest() throws Exception {
        mockMvc.perform(post(userLink + "/{userId}/acceptFriend/{friendId}", 1, 2))
            .andExpect(status().isOk());

        verify(userService).acceptFriendRequest(eq(1L), eq(2L));
    }

    @Test
    void declineFriendRequestTest() throws Exception {
        mockMvc.perform(post(userLink + "/{userId}/declineFriend/{friendId}", 1, 2))
            .andExpect(status().isOk());

        verify(userService).declineFriendRequest(eq(1L), eq(2L));
    }

    @Test
    void getSixFriendsWithTheHighestRatingTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/sixUserFriends/", 1))
            .andExpect(status().isOk());

        verify(userService).getSixFriendsWithTheHighestRatingPaged(eq(1L));
    }

    @Test
    void getUserProfileInformationTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/profile/", 1))
            .andExpect(status().isOk());
        verify(userService).getUserProfileInformation(eq(1L));
    }

    @Test
    void checkIfTheUserIsOnlineTest() throws Exception {
        mockMvc.perform(get(userLink + "/isOnline/{userId}/", 1))
            .andExpect(status().isOk());
        verify(userService).checkIfTheUserIsOnline(eq(1L));
    }

    @Test
    void getUserProfileStatistics() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/profileStatistics/", 1))
            .andExpect(status().isOk());
        verify(userService).getUserProfileStatistics(eq(1L));
    }

    @Test
    void saveTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        String json = "{\n"
            + "\t\"firstName\": \"name\",\n"
            + "\t\"city\": \"city\",\n"
            + "\t\"userCredo\": \"credo\",\n"
            + "\t\"socialNetworks\": [],\n"
            + "\t\"showLocation\": true,\n"
            + "\t\"showEcoPlace\": true,\n"
            + "\t\"showShoppingList\": false\n"
            + "}";

        this.mockMvc.perform(put(userLink + "/profile")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .principal(principal))
            .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        UserProfileDtoRequest dto = mapper.readValue(json, UserProfileDtoRequest.class);

        verify(userService).saveUserProfile(eq(dto), eq("testmail@gmail.com"));
    }
}
