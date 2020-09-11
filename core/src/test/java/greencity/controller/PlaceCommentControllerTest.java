package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.config.SecurityConfig;
import greencity.dto.comment.AddCommentDto;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.entity.enums.UserStatus;
import greencity.repository.PlaceCommentRepo;
import greencity.service.PlaceCommentService;
import greencity.service.PlaceService;
import greencity.service.UserService;
import java.security.Principal;
import org.dom4j.rule.Mode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration
@Import(SecurityConfig.class)
class PlaceCommentControllerTest {

    private static final String placeCommentLinkFirstPart = "/place";
    private static final String placeCommentLinkSecondPart = "/comments";

    private MockMvc mockMvc;

    @InjectMocks
    private PlaceCommentController placeCommentController;
    @Mock
    private PlaceCommentService placeCommentService;
    @Mock
    private UserService userService;
    @Mock
    private PlaceService placeService;
    @Mock
    private PlaceCommentRepo placeCommentRepo;

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
    void setup(){
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
    public void saveBadRequestTest() throws Exception {
        mockMvc.perform(post(placeCommentLinkFirstPart + "/{placeId}" +
            placeCommentLinkSecondPart, 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Disabled
    public void saveRequestByBlockedUserTest() throws Exception {

    }





}