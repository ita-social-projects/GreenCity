package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.goal.GoalPostDto;
import greencity.service.GoalService;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.validation.Validator;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoalControllerTest {
//    private static final String goalLink = "/goals";
//    private MockMvc mockMvc;
//    @InjectMocks
//    private GoalController goalController;
//    @Mock
//    private GoalService goalService;
//    @Mock
//    private Validator mockValidator;
//    @Mock
//    private ModelMapper modelMapper;
//
//    public static final String content =
//        "{\n"
//            + "  \"goal\": {\n"
//            + "    \"id\": 1\n"
//            + "  },\n"
//            + "  \"translations\": [\n"
//            + "    {\n"
//            + "      \"content\": \"Еко\",\n"
//            + "      \"language\": {\n"
//            + "        \"code\": \"ua\",\n"
//            + "        \"id\": 1\n"
//            + "      }\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"content\": \"Eco\",\n"
//            + "      \"language\": {\n"
//            + "        \"code\": \"en\",\n"
//            + "        \"id\": 2\n"
//            + "      }\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"content\": \"Эко\",\n"
//            + "      \"language\": {\n"
//            + "        \"code\": \"ru\",\n"
//            + "        \"id\": 3\n"
//            + "      }\n"
//            + "    }\n"
//            + "  ]\n"
//            + "}";
//
//    @BeforeEach
//    void setUp() {
//        this.mockMvc = MockMvcBuilders
//            .standaloneSetup(goalController)
//            .setValidator(mockValidator)
//            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
//            .build();
//    }
//
//    @Test
//    void getAllTest() throws Exception {
//        mockMvc.perform(get(goalLink).locale(new Locale("ru")))
//            .andExpect(status().isOk());
//        verify(goalService).findAll(eq("ru"));
//    }
//
//    @Test
//    void saveTest() throws Exception {
//        mockMvc.perform(post(goalLink)
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(content))
//            .andExpect(status().isCreated());
//        ObjectMapper mapper = new ObjectMapper();
//        GoalPostDto goalPostDto = mapper.readValue(content, GoalPostDto.class);
//        verify(goalService, times(1)).saveGoal(goalPostDto);
//    }
//
//    @Test
//    void updateTest() throws Exception {
//        mockMvc.perform(put(goalLink + "/1")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(content))
//            .andExpect(status().isOk());
//        ObjectMapper mapper = new ObjectMapper();
//        GoalPostDto goalPostDto = mapper.readValue(content, GoalPostDto.class);
//        verify(goalService, times(1)).update(goalPostDto);
//    }
//
//    @Test
//    void deleteTest() throws Exception {
//        mockMvc.perform(delete(goalLink + "/1")
//            .param("id", "1"))
//            .andExpect(status().isOk());
//        verify(goalService, times(1)).delete(1L);
//    }
}
