package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.advice.AdvicePostDto;
import greencity.service.AdviceService;
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
import org.springframework.validation.Validator;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdviceControllerTest {
    private static final String adviceLink = "/advices";

    private MockMvc mockMvc;

    @InjectMocks
    private AdviceController adviceController;

    @Mock
    private AdviceService adviceService;

    @Mock
    private Validator mockValidator;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(adviceController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(mockValidator)
            .build();
    }

    @Test
    void getAllTest() throws Exception {
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(adviceLink))
            .andExpect(status().isOk());

        verify(adviceService).getAllAdvices(pageable);
    }

    @Test
    void getRandomAdviceHabitIdAndLanguageTest() throws Exception {
        mockMvc.perform(get(adviceLink + "/random/1?lang=en"))
            .andExpect(status().isOk());

        verify(adviceService).getRandomAdviceByHabitIdAndLanguage(1L, "en");
    }

    @Test
    void updateTest() throws Exception {
        Long adviceId = 1L;
        ObjectMapper objectMapper = new ObjectMapper();
        AdvicePostDto advicePostDto = ModelUtils.getAdvicePostDto();
        String content = objectMapper.writeValueAsString(advicePostDto);

        mockMvc.perform(put(adviceLink + "/" + adviceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        verify(adviceService, times(1)).update(advicePostDto, adviceId);
    }

    @Test
    void getRandomAdviceHabitWithInvalidIdAndLanguageTest() throws Exception {
        mockMvc.perform(get(adviceLink + "/random/{id}?lang=en", "invalidId"))
            .andExpect(status().isBadRequest());
        verify(adviceService, times(0)).getRandomAdviceByHabitIdAndLanguage(1L, "en");
    }

    @Test
    void saveTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        AdvicePostDto advicePostDto = ModelUtils.getAdvicePostDto();
        String content = objectMapper.writeValueAsString(advicePostDto);

        mockMvc.perform(post(adviceLink)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        verify(adviceService, times(1)).save(advicePostDto);
    }

    @Test
    void deleteTest() throws Exception {
        mockMvc.perform(delete(adviceLink + "/{adviceId}", 1)).andExpect(status().isOk());

        verify(adviceService, times(1))
            .delete(1L);
    }

    @Test
    void deleteFailedTest() throws Exception {
        mockMvc.perform(delete(adviceLink + "/{adviceId}", "invalidId")).andExpect(status().isBadRequest());

        verify(adviceService, times(0))
            .delete(anyLong());
    }

    @Test
    void getByIdTest() throws Exception {
        Long adviceId = 1L;
        mockMvc.perform(get(adviceLink + "/{adviceId}", adviceId))
            .andExpect(status().isOk());

        verify(adviceService, times(1))
            .getAdviceById(adviceId);
    }
}