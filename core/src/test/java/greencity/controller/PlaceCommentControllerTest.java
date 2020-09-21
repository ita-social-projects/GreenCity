package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.comment.AddCommentDto;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.entity.enums.UserStatus;
import greencity.service.PlaceCommentService;
import greencity.service.PlaceService;
import greencity.service.UserService;
import java.security.Principal;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;


@ExtendWith(MockitoExtension.class)
class PlaceCommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PlaceCommentService placeCommentService;
    @Mock
    private UserService userService;
    @Mock
    private PlaceService placeService;
    @InjectMocks
    private PlaceCommentController placeCommentController;

    private static final String placeCommentLinkFirstPart = "/place";
    private static final String placeCommentLinkSecondPart = "/comments";

    private static final String content = "{\n" +
        "  \"estimate\": {\n" +
        "    \"rate\": 1\n" +
        "  },\n" +
        "  \"photos\": [\n" +
        "    {\n" +
        "      \"name\": \"string\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"text\": \"string\"\n" +
        "}";

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(placeCommentController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void saveTest() throws Exception {
        Principal principal = ModelUtils.getPrincipal();
        User user = ModelUtils.getUser();
        Place place = ModelUtils.getPlace();

        user.setUserStatus(UserStatus.ACTIVATED);
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(placeService.findById(anyLong())).thenReturn(place);

        mockMvc.perform(post(placeCommentLinkFirstPart + "/{placeId}" +
            placeCommentLinkSecondPart, 1)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        AddCommentDto addCommentDto = mapper.readValue(content, AddCommentDto.class);

        verify(userService).findByEmail(eq("test@gmail.com"));
        verify(placeCommentService).save(eq(1L), eq(addCommentDto), eq(user.getEmail()));
    }

    @Test
    void saveBadRequestTest() throws Exception {
        mockMvc.perform(post(placeCommentLinkFirstPart + "/{placeId}" +
            placeCommentLinkSecondPart, 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest());

        verify(placeCommentService, times(0)).save(eq(1L), any(), anyString());
    }

    @Test
    void saveRequestByBlockedUserTest() {
        Principal principal = ModelUtils.getPrincipal();
        User user = ModelUtils.getUser();

        user.setUserStatus(UserStatus.BLOCKED);
        when(userService.findByEmail(anyString())).thenReturn(user);

        Exception exception = assertThrows(
            NestedServletException.class,
            () -> mockMvc.perform(post(placeCommentLinkFirstPart + "/{placeId}" +
                placeCommentLinkSecondPart, 1)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isCreated()));

        String expectedMessage = ErrorMessage.USER_HAS_BLOCKED_STATUS;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(placeCommentService, times(0)).save(eq(1L), any(), anyString());
    }

    @Test
    void findAllTest() throws Exception {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(placeCommentLinkSecondPart + "?page=5"))
            .andExpect(status().isOk());

        verify(placeCommentService, times(1)).getAllComments(eq(pageable));
    }

    @Test
    void findByIdTest() throws Exception {
        mockMvc.perform(get(placeCommentLinkSecondPart + "/{id}", 1))
            .andExpect(status().isOk());

        verify(placeCommentService, times(1))
            .findById(1L);
    }

    @Test
    void findByIdFailedTest() throws Exception {
        mockMvc.perform(get(placeCommentLinkSecondPart + "/{id}", "invalidID"))
            .andExpect(status().isBadRequest());

        verify(placeCommentService, times(0)).findById(1L);
    }

    @Test
    void deleteByIdTest() throws Exception {
        this.mockMvc.perform(delete(placeCommentLinkSecondPart + "?id={id}", 1))
            .andExpect(status().isOk());

        verify(placeCommentService, times(1))
            .deleteById(eq(1L));
    }

    @Test
    void deleteByIdFailedTest() throws Exception {
        mockMvc.perform(delete(placeCommentLinkSecondPart + "?id={id}", "invalidID"))
            .andExpect(status().isBadRequest());

        verify(placeCommentService, times(0)).findById(1L);
    }
}