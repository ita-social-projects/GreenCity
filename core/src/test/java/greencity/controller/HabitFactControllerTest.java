package greencity.controller;

import com.google.gson.Gson;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitIdRequestDto;
import greencity.service.HabitFactService;
import greencity.service.HabitFactTranslationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HabitFactControllerTest {

    private MockMvc mockMvc;

    @Mock
    HabitFactService habitFactService;
    @Mock
    HabitFactTranslationService habitFactTranslationService;
    @Mock
    ModelMapper mapper;

    @InjectMocks
    HabitFactController habitFactController;

    private static final String factLink = "/facts";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitFactController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void getRandomFactByHabitId() throws Exception {
        Locale locale = new Locale("en");
        Gson gson = new Gson();
        String json = gson.toJson(locale);
        mockMvc.perform(get(factLink + "/random/{habitId}", 1)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(habitFactService).getRandomHabitFactByHabitIdAndLanguage(1L, locale.getLanguage());
    }

    @Test
    void getHabitFactOfTheDay() throws Exception {
        mockMvc.perform(get(factLink + "/dayFact/{languageId}", 1))
                .andExpect(status().isOk());
        verify(habitFactTranslationService).getHabitFactOfTheDay(1L);
    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(get(factLink))
                .andExpect(status().isOk());
        verify(habitFactService).getAllHabitFacts();
    }


    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(factLink + "/{factId}", 1))
                .andExpect(status().isOk());
        verify(habitFactService).delete(1L);
    }
}
