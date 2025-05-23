package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyExistsException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.exception.helper.EndpointValidationHelper;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

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
import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
