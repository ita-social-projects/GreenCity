package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.user.CreateGreenCityUserDto;
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

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        when(userService.createUser(userDto)).thenThrow(new UserAlreadyExistsException(HttpStatus.CONFLICT, "User already registered with this email"));
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
}
