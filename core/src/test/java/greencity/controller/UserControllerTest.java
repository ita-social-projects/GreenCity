package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.TestConst;
import greencity.constant.ErrorMessage;
import greencity.dto.location.UserLocationDto;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.dto.user.UpdateUserCredoDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyExistsException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.exception.helper.EndpointValidationHelper;
import greencity.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    static final String userLink = "/users";
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    final ObjectMapper objectMapper = new ObjectMapper();

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
            .setCustomArgumentResolvers(
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
        Long userId = 1L;
        String profilePicturePath = "http://somepicture.com.ua";

        doNothing().when(userService).updateUserProfilePicture(userId, profilePicturePath);

        mockMvc.perform(put(userLink + "/picturePath")
            .param("profilePicturePath", profilePicturePath)
            .param("userId", String.valueOf(userId)))
            .andExpect(status().isOk());

        verify(userService).updateUserProfilePicture(userId, profilePicturePath);
    }

    @Test
    void updatePicturePathUserNotFoundTest() throws Exception {
        Long userId = 999L;
        String profilePicturePath = "http://somepicture.com.ua";

        doThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId))
            .when(userService).updateUserProfilePicture(userId, profilePicturePath);

        mockMvc.perform(put(userLink + "/picturePath")
            .param("profilePicturePath", profilePicturePath)
            .param("userId", String.valueOf(userId)))
            .andExpect(status().isNotFound());

        verify(userService).updateUserProfilePicture(userId, profilePicturePath);
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
    void getAllUserFriendsIdsPageTest() throws Exception {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Long> friendsIds = List.of(2L, 3L, 4L);
        Page<Long> friendsPage = new PageImpl<>(friendsIds, pageable, friendsIds.size());

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
}