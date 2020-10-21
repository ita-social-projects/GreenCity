
package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.advice.AdviceDto;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceTranslationVO;
import greencity.entity.User;
import greencity.entity.localization.AdviceTranslation;
import greencity.service.AdviceService;
import greencity.service.AdviceTranslationService;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
class AdviceControllerTest {
    private static final String adviceLink = "/advices";

    private MockMvc mockMvc;

    @Mock
    AdviceService adviceService;

    @Mock
    ModelMapper modelMapper;

    @Mock
    AdviceTranslationService adviceTranslationService;

    @Mock
    Validator mockValidator;

    @InjectMocks
    AdviceController adviceController;

    private List<AdviceTranslationVO> adviceTranslationsVO =
        Collections.singletonList(AdviceTranslationVO.builder().build());

    private AdvicePostDto advicePostDto = new AdvicePostDto();



    private User user = User.builder().id(16L).email("dima.honko@gmail.com").build();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(adviceController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(mockValidator)
            .build();
    }

    @Test
    void findAllTest() throws Exception {
        mockMvc.perform(get(adviceLink)).andExpect(status().isOk());
        verify(adviceService, times(1)).getAllAdvices();
    }

    @Test
    void getRandomAdviceHabitIdAndLanguageTest() throws Exception {
        mockMvc.perform(get(adviceLink + "/random/1?lang=en"))
            .andExpect(status().isOk());

        verify(adviceService).getRandomAdviceByHabitIdAndLanguage(1L, "en");
    }

    @Test
    void getRandomAdviceHabitWithInvalidIdAndLanguageTest() throws Exception {
        mockMvc.perform(get(adviceLink + "/random/{id}?lang=en", "invalidId"))
            .andExpect(status().isBadRequest());

        verify(adviceService, times(0)).getRandomAdviceByHabitIdAndLanguage(1L, "en");
    }

    @Test
    void saveTest() throws Exception {
        when(adviceTranslationService.saveAdviceAndAdviceTranslation(advicePostDto)).thenReturn(adviceTranslationsVO);
        List<AdviceTranslation> response = modelMapper.map(adviceTranslationsVO,
            new TypeToken<List<AdviceTranslation>>() {
            }.getType());
        ResponseEntity<List<AdviceTranslation>> ans = ResponseEntity.status(HttpStatus.CREATED).body(response);
        assertEquals(ans, adviceController.save(advicePostDto));
    }

    @Test
    void deleteTest() throws Exception {
        mockMvc.perform(delete(adviceLink + "/{adviceId}", 1)
        ).andExpect(status().isOk());

        verify(adviceService, times(1))
            .delete(1L);
    }

    @Test
    void deleteFailedTest() throws Exception {
        mockMvc.perform(delete(adviceLink + "/{adviceId}", "invalidId")
        ).andExpect(status().isBadRequest());

        verify(adviceService, times(0))
            .delete(anyLong());
    }
}
//    public static final String content =
//        "{\n"
//////        + "  \"habitDictionary\": {\n"
//////        + "    \"id\": 1\n"
//////        + "  },\n"
//            + "    {\n"
//            + "      \"habit\": {\n"
//            + "        \"id\": 1\n"
//            + "      }\n"
//        + "  \"translations\": [\n"
//        + "{\n"
//            +" \"content\": \"string\", "
//        + "      \"language\": {\n"
//        + "        \"code\": \"en\",\n"
//        + "        \"id\": 1\n"
//        + "      }\n"
//        + "    },\n"
//        + "    },\n";
//////        + "    {\n"
//////        + "      \"content\": \"Эко\",\n"
//////        + "      \"language\": {\n"
//////        + "        \"code\": \"ru\",\n"
//////        + "        \"id\": 3\n"
//////        + "       }\n"
//////        + "     } \n"
////        + "  ]\n"
////        + "}";
////        "{\n"
////        + "      \"habit\": {\n"
////        + "        \"id\": 1\n"
////        + "      }\n"
////        "translations": [
////    {
////        "content": "Eco",
////        "language": {
////        "code": "en",
////            "id": 1
////    }
////    }
////  ]
////}";
//    @Test
//    void updateTest() throws Exception {
//        mockMvc.perform(put(adviceLink + "/{adviceId}", 1)
//            .with(SecurityMockMvcRequestPostProcessors.httpBasic("dima.honko@gmail.com","Fabial_2014"))
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(content))
//            .andExpect(status().isOk());
//
//        ObjectMapper mapper = new ObjectMapper();
//        AdvicePostDto advicePostDTO = mapper.readValue(content, AdvicePostDto.class);
//
//        verify(adviceService, times(1)).update(advicePostDTO, 1L);
//    }
