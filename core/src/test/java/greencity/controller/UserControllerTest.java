package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.goal.BulkCustomGoalDto;
import greencity.dto.goal.BulkSaveCustomGoalDto;
import greencity.dto.user.BulkSaveUserGoalDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserUpdateDto;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.service.CustomGoalService;
import greencity.service.HabitStatisticService;
import greencity.service.UserService;
import java.security.Principal;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
    private HabitStatisticService habitStatisticService;
    @Mock
    private CustomGoalService customGoalService;

    @BeforeEach
    public void setup() {
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

        verify(userService).updateRole(eq(1L), eq(ROLE.ROLE_USER), eq("testmail@gmail.com"));
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

    /*@Test
    void getUserHabitsTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/habits?language=en", 1))
            .andExpect(status().isCreated());

        verify(habitStatisticService).findAllHabitsAndTheirStatistics(
            eq(1L), eq(true), eq("en"));
    }*/

    @Test
    void getUserHabitsWithoutLanguageParamTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/habits", 1))
            .andExpect(status().isCreated());
    }

    /*@Test
    void findInfoAboutUserHabitsTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/habits/statistic", 1))
            .andExpect(status().isOk());

        verify(habitStatisticService).getInfoAboutUserHabits(eq(1L));
    }*/

    @Test
    void getUserGoalsWithLanguageParamTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/goals?language=en", 1))
            .andExpect(status().isOk());

        verify(userService).getUserGoals(eq(1L), eq("en"));
    }

    @Test
    void getUserGoalsWithoutLanguageParamTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/goals", 1))
            .andExpect(status().isOk());

        verify(userService).getUserGoals(eq(1L), eq("en"));
    }

    @Test
    void findAllByUserTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/customGoals", 1))
            .andExpect(status().isOk());

        verify(customGoalService).findAllByUser(eq(1L));
    }

    @Test
    void saveUserCustomGoalsTest() throws Exception {
        User user = ModelUtils.getUser();
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

        verify(customGoalService).save(eq(dto), eq(user));
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
    void getAvailableGoalsWithoutLanguageParamTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/goals/available", 1))
            .andExpect(status().isOk());

        verify(userService).getAvailableGoals(eq(1L), eq("en"));
    }

    @Test
    void getAvailableGoalsWithLanguageParamTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/goals/available", 1)
            .locale(new Locale("ru")))
            .andExpect(status().isOk());

        verify(userService).getAvailableGoals(eq(1L), eq("ru"));
    }

    @Test
    void getAvailableCustomGoalsTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/customGoals/available", 1))
            .andExpect(status().isOk());

        verify(userService).getAvailableCustomGoals(eq(1L));
    }

    @Test
    void updateUserGoalStatusWithLanguageParamTest() throws Exception {
        mockMvc.perform(patch(userLink + "/{userId}/goals/{goalId}", 1, 1)
            .locale(new Locale("ru")))
            .andExpect(status().isCreated());

        verify(userService).updateUserGoalStatus(eq(1L), eq(1L), eq("ru"));
    }

    @Test
    void updateUserGoalStatusWithoutLanguageParamTest() throws Exception {
        mockMvc.perform(patch(userLink + "/{userId}/goals/{goalId}", 1, 1))
            .andExpect(status().isCreated());

        verify(userService).updateUserGoalStatus(eq(1L), eq(1L), eq("en"));
    }

    @Test
    void saveUserGoalsWithoutLanguageParamTest() throws Exception {
        String content = "{\n"
            + "  \"userCustomGoal\": [\n"
            + "    {\n"
            + "      \"customGoal\": {\n"
            + "        \"id\": 1\n"
            + "      }\n"
            + "    }\n"
            + "  ],\n"
            + "  \"userGoals\": [\n"
            + "    {\n"
            + "      \"goal\": {\n"
            + "        \"id\": 1\n"
            + "      }\n"
            + "    }\n"
            + "  ]\n"
            + "}\n";

        mockMvc.perform(post(userLink + "/{userId}/goals", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        BulkSaveUserGoalDto dto =
            mapper.readValue(content, BulkSaveUserGoalDto.class);

        verify(userService).saveUserGoals(eq(1L), eq(dto), eq("en"));
    }

    /*@Test
    void getAvailableHabitDictionaryTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/habit-dictionary/available?language=en", 1))
            .andExpect(status().isOk());

        verify(userService).getAvailableHabitDictionary(eq(1L), eq("en"));
    }*/

    /*@Test
    void saveUserHabitsTest() throws Exception {
        String content = "[\n"
            + "  {\n"
            + "    \"habitDictionaryId\": 0\n"
            + "  }\n"
            + "]";

        mockMvc.perform(post(userLink + "/{userId}/habit?language=en", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        List<HabitIdDto> dto = mapper.readValue(content, new TypeReference<List<HabitIdDto>>() {
        });

        verify(userService).createUserHabit(eq(1L), eq(dto), eq("en"));
    }*/

   /* @Test
    void deleteHabitTest() throws Exception {
        mockMvc.perform(delete(userLink + "/{userId}/habit/{habitId}", 1, 1))
            .andExpect(status().isOk());

        verify(userService).deleteHabitByUserIdAndHabitDictionary(eq(1L), eq(1L));
    }
*/
    @Test
    void bulkDeleteUserGoalsTest() throws Exception {
        mockMvc.perform(delete(userLink + "/{userId}/userGoals?ids=1,2", 1))
            .andExpect(status().isOk());

        verify(userService).deleteUserGoals(eq("1,2"));
    }

    @Test
    void getActivatedUsersAmountTest() throws Exception {
        mockMvc.perform(get(userLink + "/activatedUsersAmount"))
            .andExpect(status().isOk());

        verify(userService).getActivatedUsersAmount();
    }

    @Test
    void updateUserProfilePictureTest() throws Exception {
        User user = ModelUtils.getUser();
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
    void getSixFriendsWithTheHighestRatingTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/sixUserFriends/", 1))
            .andExpect(status().isOk());

        verify(userService).getSixFriendsWithTheHighestRating(eq(1L));
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