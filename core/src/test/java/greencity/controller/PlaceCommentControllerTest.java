package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.UserStatus;
import greencity.service.PlaceCommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PlaceCommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PlaceCommentService placeCommentService;
    @InjectMocks
    private PlaceCommentController placeCommentController;

    private AddCommentDto addCommentDto;
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

    private static final String getContent = "{\n" +
        "  \"estimate\": {\n" +
        "    \"rate\": 1\n" +
        "  },\n" +
        "  \"photos\": [\n" +
        "    {\n" +
        "      \"name\": \"test\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"text\": \"test\"\n" +
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
        UserVO userVO = getUserVO();

        user.setUserStatus(UserStatus.ACTIVATED);
        userVO.setUserStatus(UserStatus.ACTIVATED);

        mockMvc.perform(post(placeCommentLinkFirstPart + "/{placeId}" +
            placeCommentLinkSecondPart, 1)
            .principal(principal)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        addCommentDto = mapper.readValue(content, AddCommentDto.class);

        verify(placeCommentService).save(1L, addCommentDto, principal.getName());
    }

    @Test
    void saveBadRequestTest() throws Exception {
        mockMvc.perform(post(placeCommentLinkFirstPart + "/{placeId}" +
            placeCommentLinkSecondPart, 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest());

        verify(placeCommentService, times(0)).save(1L, addCommentDto, "fail@ukr.net");
    }

    @Test
    void findAllTest() throws Exception {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(placeCommentLinkSecondPart + "?page=5"))
            .andExpect(status().isOk());

        verify(placeCommentService, times(1)).getAllComments(pageable);
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
            .deleteById(1L);
    }

    @Test
    void deleteByIdFailedTest() throws Exception {
        mockMvc.perform(delete(placeCommentLinkSecondPart + "?id={id}", "invalidID"))
            .andExpect(status().isBadRequest());

        verify(placeCommentService, times(0)).findById(1L);
    }
}
