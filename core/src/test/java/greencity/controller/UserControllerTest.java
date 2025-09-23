package greencity.controller;

import static greencity.ModelUtils.getUserVO;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.TestConst;
import greencity.constant.ErrorMessage;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.location.UserLocationDto;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.dto.user.UpdateUserCredoDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserAddRatingExternalDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserVO;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyExistsException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.exception.helper.EndpointValidationHelper;
import greencity.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    static final String userLink = "/users";
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    final ObjectMapper objectMapper = new ObjectMapper();
    final ModelMapper modelMapper = new ModelMapper();

    MockMvc mockMvc;

    @Mock
    EndpointValidationHelper endpointValidationHelper;

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper),
                new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper, endpointValidationHelper))
            .build();
    }

    @Test
    void createUserTest() throws Exception {
        CreateGreenCityUserDto userDto = ModelUtils.getCreateGreenCityDto();

        when(userService.createUser(userDto)).thenReturn(true);

        mockMvc.perform(post(userLink + "/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(userDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().string("true"));

        verify(userService).createUser(userDto);
    }

    @Test
    void createUserConflictTest() throws Exception {
        CreateGreenCityUserDto userDto = ModelUtils.getCreateGreenCityDto();

        when(userService.createUser(userDto))
            .thenThrow(new UserAlreadyExistsException(HttpStatus.CONFLICT, "User already registered with this email"));
        MvcResult result = mockMvc.perform(post(userLink + "/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(userDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("User already registered with this email"));

        verify(userService).createUser(userDto);
    }

    @Test
    void updatePicturePathTest() throws Exception {
        String email = "test@email";
        String profilePicturePath = "http://somepicture.com.ua";

        doNothing().when(userService).updateUserProfilePicture(email, profilePicturePath);

        mockMvc.perform(put(userLink + "/user/picturePath")
            .param("email", email)
            .param("profilePicturePath", profilePicturePath))
            .andExpect(status().isOk());

        verify(userService).updateUserProfilePicture(email, profilePicturePath);
    }

    @Test
    void updatePicturePathUserNotFoundTest() throws Exception {
        String email = "test@email";
        String profilePicturePath = "http://somepicture.com.ua";

        doThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email))
            .when(userService).updateUserProfilePicture(email, profilePicturePath);

        mockMvc.perform(put(userLink + "/user/picturePath")
            .param("email", email)
            .param("profilePicturePath", profilePicturePath))
            .andExpect(status().isNotFound());

        verify(userService).updateUserProfilePicture(email, profilePicturePath);
    }

    @Test
    void updateUserNameTest() throws Exception {
        Long userId = 1L;
        String userName = "username";
        String url = UriComponentsBuilder.fromPath(userLink + "/{userId}/name")
            .buildAndExpand(userId)
            .toUriString();

        doNothing().when(userService).updateUserName(userId, userName);

        mockMvc.perform(patch(url).queryParam("userName", userName))
            .andExpect(status().isOk())
            .andReturn();

        verify(userService).updateUserName(userId, userName);
    }

    @Test
    void updateUserNameWhenUserIsNotFoundTest() throws Exception {
        Long userId = 1L;
        String userName = "username";
        String url = UriComponentsBuilder.fromPath(userLink + "/{userId}/name")
            .buildAndExpand(userId)
            .toUriString();

        doThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId))
            .when(userService).updateUserName(userId, userName);

        mockMvc.perform(patch(url).queryParam("userName", userName))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(userService).updateUserName(userId, userName);
    }

    @Test
    void updateUserNameByEmailTest() throws Exception {
        String email = "test@email";
        String userName = "username";
        String url = UriComponentsBuilder.fromPath(userLink + "/user/name")
            .queryParam("email", email)
            .buildAndExpand()
            .toUriString();

        doNothing().when(userService).updateUserName(email, userName);

        mockMvc.perform(patch(url).queryParam("userName", userName))
            .andExpect(status().isOk())
            .andReturn();

        verify(userService).updateUserName(email, userName);
    }

    @Test
    void updateUserNameByEmailWhenUserIsNotFoundTest() throws Exception {
        String email = "test@email";
        String userName = "username";
        String url = UriComponentsBuilder.fromPath(userLink + "/user/name")
            .queryParam("email", email)
            .buildAndExpand()
            .toUriString();

        doThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email))
            .when(userService).updateUserName(email, userName);

        mockMvc.perform(patch(url).queryParam("userName", userName))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(userService).updateUserName(email, userName);
    }

    @Test
    @SneakyThrows
    void findGreenCityUserProfilesByUserIdsTest() {
        List<Long> userIds = List.of(1L, 2L, 3L);
        String userIdsStr = userIds.stream().map(String::valueOf).collect(Collectors.joining(","));

        mockMvc.perform(get(userLink + "/profiles")
            .queryParam("userIds", userIdsStr))
            .andExpect(status().isOk());

        verify(userService).findGreenCityUserProfilesByUserIds(userIds);
    }

    @Test
    @SneakyThrows
    void findGreenCityUserProfilesByUserIdsWhenUsersNotFoundTest() {
        List<Long> userIds = List.of(1L, 2L, 3L);
        String userIdsStr = userIds.stream().map(String::valueOf).collect(Collectors.joining(","));

        when(userService.findGreenCityUserProfilesByUserIds(userIds))
            .thenThrow(new NotFoundException());

        mockMvc.perform(get(userLink + "/profiles")
            .queryParam("userIds", userIdsStr))
            .andExpect(status().isNotFound());

        verify(userService).findGreenCityUserProfilesByUserIds(userIds);
    }

    @Test
    @SneakyThrows
    void findGreenCityUserProfilesByEmailsTest() {
        List<String> emails = List.of("email1", "email2", "email3");
        String userEmailsStr = String.join(",", emails);

        mockMvc.perform(get(userLink + "/profiles/external")
            .queryParam("emails", userEmailsStr))
            .andExpect(status().isOk());

        verify(userService).findGreenCityUserProfilesByEmails(emails);
    }

    @Test
    @SneakyThrows
    void findGreenCityUserProfilesByEmailsWhenUsersNotFoundTest() {
        List<String> emails = List.of("email1", "email2", "email3");
        String userEmailsStr = String.join(",", emails);

        when(userService.findGreenCityUserProfilesByEmails(emails))
            .thenThrow(new NotFoundException());

        mockMvc.perform(get(userLink + "/profiles/external")
            .queryParam("emails", userEmailsStr))
            .andExpect(status().isNotFound());

        verify(userService).findGreenCityUserProfilesByEmails(emails);
    }

    @Test
    void findAllUsersCitiesTest() throws Exception {
        Long userId = 1L;
        UserCityDto userCityDto = new UserCityDto();

        when(userService.findAllUsersCities(userId)).thenReturn(userCityDto);

        mockMvc.perform(get(userLink + "/{id}/cities", userId))
            .andExpect(status().isOk());

        verify(userService).findAllUsersCities(userId);
    }

    @Test
    void findAllUsersCitiesNotFoundTest() throws Exception {
        Long userId = 999L;

        when(userService.findAllUsersCities(userId))
            .thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        mockMvc.perform(get(userLink + "/{id}/cities", userId))
            .andExpect(status().isNotFound());

        verify(userService).findAllUsersCities(userId);
    }

    @Test
    void findAllUsersCitiesByEmailTest() throws Exception {
        String email = "test@email";
        UserCityDto userCityDto = new UserCityDto();

        when(userService.findAllUsersCities(email)).thenReturn(userCityDto);

        mockMvc.perform(get(userLink + "/user/cities")
            .queryParam("email", email))
            .andExpect(status().isOk());

        verify(userService).findAllUsersCities(email);
    }

    @Test
    void findAllUsersCitiesByEmailNotFoundTest() throws Exception {
        String email = "test@email";

        when(userService.findAllUsersCities(email))
            .thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        mockMvc.perform(get(userLink + "/user/cities")
            .queryParam("email", email))
            .andExpect(status().isNotFound());

        verify(userService).findAllUsersCities(email);
    }

    @Test
    void findUserLocationByUserIdTest() throws Exception {
        Long userId = 1L;
        UserLocationDto userLocationDto = new UserLocationDto();

        when(userService.findUserLocationDtoByUserId(userId)).thenReturn(userLocationDto);

        mockMvc.perform(get(userLink + "/{id}/location", userId))
            .andExpect(status().isOk());

        verify(userService).findUserLocationDtoByUserId(userId);
    }

    @Test
    void findUserLocationByUserIdNotFoundTest() throws Exception {
        Long userId = 999L;

        when(userService.findUserLocationDtoByUserId(userId))
            .thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        mockMvc.perform(get(userLink + "/{id}/location", userId))
            .andExpect(status().isNotFound());

        verify(userService).findUserLocationDtoByUserId(userId);
    }

    @Test
    void setLocationForUserTest() throws Exception {
        Long userId = 1L;
        UserProfileDtoRequest userProfileDtoRequest = new UserProfileDtoRequest();

        doNothing().when(userService).setLocationForUser(userId, userProfileDtoRequest);

        mockMvc.perform(patch(userLink + "/{id}/location", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userProfileDtoRequest)))
            .andExpect(status().isOk());

        verify(userService).setLocationForUser(userId, userProfileDtoRequest);
    }

    @Test
    void setLocationForUserNotFoundTest() throws Exception {
        Long userId = 999L;
        UserProfileDtoRequest userProfileDtoRequest = new UserProfileDtoRequest();

        doThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId))
            .when(userService).setLocationForUser(userId, userProfileDtoRequest);

        mockMvc.perform(patch(userLink + "/{id}/location", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userProfileDtoRequest)))
            .andExpect(status().isNotFound());

        verify(userService).setLocationForUser(userId, userProfileDtoRequest);
    }

    @Test
    void setLocationForUserByEmailTest() throws Exception {
        String email = "test@email";
        UserProfileDtoRequest userProfileDtoRequest = new UserProfileDtoRequest();

        doNothing().when(userService).setLocationForUser(email, userProfileDtoRequest);

        mockMvc.perform(patch(userLink + "/user/location")
            .queryParam("email", email)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userProfileDtoRequest)))
            .andExpect(status().isOk());

        verify(userService).setLocationForUser(email, userProfileDtoRequest);
    }

    @Test
    void setLocationForUserByEmailNotFoundTest() throws Exception {
        String email = "test@email";
        UserProfileDtoRequest userProfileDtoRequest = new UserProfileDtoRequest();

        doThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email))
            .when(userService).setLocationForUser(email, userProfileDtoRequest);

        mockMvc.perform(patch(userLink + "/user/location")
            .queryParam("email", email)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userProfileDtoRequest)))
            .andExpect(status().isNotFound());

        verify(userService).setLocationForUser(email, userProfileDtoRequest);
    }

    @Test
    void getAllUserFriendsIdsTest() throws Exception {
        Long userId = 1L;
        List<Long> friendsIds = List.of(2L, 3L, 4L);

        when(userService.getAllUserFriendsIds(userId)).thenReturn(friendsIds);

        mockMvc.perform(get(userLink + "/{id}/all-friends", userId))
            .andExpect(status().isOk());

        verify(userService).getAllUserFriendsIds(userId);
    }

    @Test
    void getAllUserFriendsIdsNotFoundTest() throws Exception {
        Long userId = 999L;

        when(userService.getAllUserFriendsIds(userId))
            .thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        mockMvc.perform(get(userLink + "/{id}/all-friends", userId))
            .andExpect(status().isNotFound());

        verify(userService).getAllUserFriendsIds(userId);
    }

    @Test
    void getAllUserFriendsIdsByEmailTest() throws Exception {
        String email = "test@email";
        List<Long> friendsIds = List.of(2L, 3L, 4L);

        when(userService.getAllUserFriendsIds(email)).thenReturn(friendsIds);

        mockMvc.perform(get(userLink + "/user/all-friends")
            .queryParam("email", email))
            .andExpect(status().isOk());

        verify(userService).getAllUserFriendsIds(email);
    }

    @Test
    void getAllUserFriendsIdsByEmailNotFoundTest() throws Exception {
        String email = "test@email";

        when(userService.getAllUserFriendsIds(email))
            .thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        mockMvc.perform(get(userLink + "/user/all-friends")
            .queryParam("email", email))
            .andExpect(status().isNotFound());

        verify(userService).getAllUserFriendsIds(email);
    }

    @Test
    void getAllUserFriendsIdsPageTest() throws Exception {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        PageableAdvancedDto<Long> friendsPage = new PageableAdvancedDto<>();

        when(userService.getAllUserFriendsIds(userId, pageable)).thenReturn(friendsPage);

        mockMvc.perform(get(userLink + "/{id}/friends", userId)
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk());

        verify(userService).getAllUserFriendsIds(userId, pageable);
    }

    @Test
    void getAllUserFriendsIdsPageNotFoundTest() throws Exception {
        Long userId = 999L;
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.getAllUserFriendsIds(userId, pageable))
            .thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        mockMvc.perform(get(userLink + "/{id}/friends", userId)
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isNotFound());

        verify(userService).getAllUserFriendsIds(userId, pageable);
    }

    @Test
    void getAllUserFriendsIdsPageByEmailTest() throws Exception {
        String email = "test@email";
        Pageable pageable = PageRequest.of(0, 10);
        PageableAdvancedDto<Long> friendsPage = new PageableAdvancedDto<>();

        when(userService.getAllUserFriendsIds(email, pageable)).thenReturn(friendsPage);

        mockMvc.perform(get(userLink + "/user/friends")
            .queryParam("email", email)
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk());

        verify(userService).getAllUserFriendsIds(email, pageable);
    }

    @Test
    void getAllUserFriendsIdsPageByEmailNotFoundTest() throws Exception {
        String email = "test@email";
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.getAllUserFriendsIds(email, pageable))
            .thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        mockMvc.perform(get(userLink + "/user/friends")
            .queryParam("email", email)
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isNotFound());

        verify(userService).getAllUserFriendsIds(email, pageable);
    }

    @Test
    void getSixFriendsIdsWithTheHighestRatingTest() throws Exception {
        Long userId = 1L;
        List<Long> topFriendsIds = List.of(2L, 3L, 4L, 5L, 6L, 7L);

        when(userService.getSixFriendsIdsWithTheHighestRating(userId)).thenReturn(topFriendsIds);

        mockMvc.perform(get(userLink + "/{id}/top-friends", userId))
            .andExpect(status().isOk());

        verify(userService).getSixFriendsIdsWithTheHighestRating(userId);
    }

    @Test
    void getSixFriendsIdsWithTheHighestRatingNotFoundTest() throws Exception {
        Long userId = 999L;

        when(userService.getSixFriendsIdsWithTheHighestRating(userId))
            .thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        mockMvc.perform(get(userLink + "/{id}/top-friends", userId))
            .andExpect(status().isNotFound());

        verify(userService).getSixFriendsIdsWithTheHighestRating(userId);
    }

    @Test
    void getSixFriendsIdsWithTheHighestRatingByEmailTest() throws Exception {
        String email = "test@email";
        List<Long> topFriendsIds = List.of(2L, 3L, 4L, 5L, 6L, 7L);

        when(userService.getSixFriendsIdsWithTheHighestRating(email)).thenReturn(topFriendsIds);

        mockMvc.perform(get(userLink + "/user/top-friends")
            .queryParam("email", email))
            .andExpect(status().isOk());

        verify(userService).getSixFriendsIdsWithTheHighestRating(email);
    }

    @Test
    void getSixFriendsIdsWithTheHighestRatingByEmailNotFoundTest() throws Exception {
        String email = "test@email";

        when(userService.getSixFriendsIdsWithTheHighestRating(email))
            .thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        mockMvc.perform(get(userLink + "/user/top-friends")
            .queryParam("email", email))
            .andExpect(status().isNotFound());

        verify(userService).getSixFriendsIdsWithTheHighestRating(email);
    }

    @Test
    void increaseUserRatingTest() throws Exception {
        UserAddRatingDto userAddRatingDto = new UserAddRatingDto();

        doNothing().when(userService).increaseUserRating(userAddRatingDto);

        mockMvc.perform(patch(userLink + "/rating")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userAddRatingDto)))
            .andExpect(status().isOk());

        verify(userService).increaseUserRating(userAddRatingDto);
    }

    @Test
    void increaseUserRatingNotFoundTest() throws Exception {
        UserAddRatingDto userAddRatingDto = new UserAddRatingDto();

        doThrow(new NotFoundException("User not found"))
            .when(userService).increaseUserRating(userAddRatingDto);

        mockMvc.perform(patch(userLink + "/rating")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userAddRatingDto)))
            .andExpect(status().isNotFound());

        verify(userService).increaseUserRating(userAddRatingDto);
    }

    @Test
    void increaseUserRatingExternalTest() throws Exception {
        UserAddRatingExternalDto userAddRatingDto = new UserAddRatingExternalDto();

        doNothing().when(userService).increaseUserRating(userAddRatingDto);

        mockMvc.perform(patch(userLink + "/user-rating")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userAddRatingDto)))
            .andExpect(status().isOk());

        verify(userService).increaseUserRating(userAddRatingDto);
    }

    @Test
    void increaseUserRatingExternalNotFoundTest() throws Exception {
        UserAddRatingExternalDto userAddRatingDto = new UserAddRatingExternalDto();

        doThrow(new NotFoundException("User not found"))
            .when(userService).increaseUserRating(userAddRatingDto);

        mockMvc.perform(patch(userLink + "/user-rating")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userAddRatingDto)))
            .andExpect(status().isNotFound());

        verify(userService).increaseUserRating(userAddRatingDto);
    }

    @Test
    void updateUserCredoByUserIdTest() throws Exception {
        UpdateUserCredoDto updateUserCredoDto = new UpdateUserCredoDto(TestConst.USER_ID, "credo");

        doNothing().when(userService).updateUserCredo(updateUserCredoDto);

        mockMvc.perform(patch(userLink + "/credo")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateUserCredoDto)))
            .andExpect(status().isOk());

        verify(userService).updateUserCredo(updateUserCredoDto);
    }

    @Test
    void updateUserCredoByUserIdNotFoundTest() throws Exception {
        UpdateUserCredoDto updateUserCredoDto = new UpdateUserCredoDto(TestConst.USER_ID, "credo");

        doThrow(new NotFoundException("User not found"))
            .when(userService).updateUserCredo(updateUserCredoDto);

        mockMvc.perform(patch(userLink + "/credo")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateUserCredoDto)))
            .andExpect(status().isNotFound());

        verify(userService).updateUserCredo(updateUserCredoDto);
    }

    @Test
    void changeUserStatusTest() throws Exception {
        UserStatus status = UserStatus.ACTIVATED;
        UserVO currentUser = getUserVO();
        long userId = 1L;

        when(userService.findNotDeactivatedByEmail(currentUser.getEmail()))
            .thenReturn(currentUser);

        mockMvc.perform(put(userLink + "/status/" + userId)
                .param("status", status.name())
                .principal(currentUser::getEmail))
            .andExpect(status().isOk());

        verify(userService).updateUserStatusById(currentUser, userId, status);
    }

    @Test
    void getReasonsOfDeactivationTest() throws Exception {
        List<String> reasons = List.of("reason1", "reason2", "reason3");
        UserVO currentUser = getUserVO();
        long userId = 1L;

        when(userService.findNotDeactivatedByEmail(currentUser.getEmail()))
            .thenReturn(currentUser);
        when(userService.getDeactivationReasons(userId, currentUser))
            .thenReturn(reasons);

        mockMvc.perform(get(userLink + "/reasons")
                .param("id", String.valueOf(userId))
                .principal(currentUser::getEmail))
            .andExpect(status().isOk());

        verify(userService).getDeactivationReasons(userId, currentUser);
    }

    @Test
    void getUserStatusTest() throws Exception {
        UserStatus status = UserStatus.ACTIVATED;
        String email = "test@email";

        when(userService.getUserStatusByEmail(email))
            .thenReturn(status);

        mockMvc.perform(get(userLink + "/status")
                .param("email", email))
            .andExpect(status().isOk());

        verify(userService).getUserStatusByEmail(email);
    }

    @Test
    void deleteUserTest() throws Exception {
        UserVO currentUser = getUserVO();

        when(userService.findNotDeactivatedByEmail(currentUser.getEmail()))
            .thenReturn(currentUser);

        mockMvc.perform(delete(userLink + "/delete")
                .principal(currentUser::getEmail))
            .andExpect(status().isOk());

        verify(userService).deleteUserByEmail(currentUser.getEmail());
    }

    @Test
    void getActivatedUsersAmountTest() throws Exception {
        long amount = 100L;

        when(userService.getActivatedUsersAmount())
            .thenReturn(amount);

        mockMvc.perform(get(userLink + "/activatedUsersAmount"))
            .andExpect(status().isOk());

        verify(userService).getActivatedUsersAmount();
    }
}