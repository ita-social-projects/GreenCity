package greencity.webcontroller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.google.gson.Gson;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitManagementDto;
import greencity.dto.language.LanguageDTO;
import greencity.service.LanguageService;
import greencity.service.ManagementHabitService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class HabitManagementControllerTest {

    private static final String habitManagementLink = "/management/habits";

    private MockMvc mockMvc;

    @Mock
    private ManagementHabitService managementHabitService;

    @Mock
    private LanguageService languageService;

    @InjectMocks
    HabitManagementController habitManagementController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitManagementController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void findAllHabits() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        List<HabitManagementDto> habitManagementDtos = Collections.singletonList(new HabitManagementDto());
        PageableDto<HabitManagementDto> habitManagementDtoPageableDto = new PageableDto<>(habitManagementDtos, 4, 0, 3);

        when(managementHabitService.getAllHabitsDto(pageable)).thenReturn(habitManagementDtoPageableDto);

        List<LanguageDTO> languageDtos = Collections.singletonList(new LanguageDTO());

        when(languageService.getAllLanguages()).thenReturn(languageDtos);

        this.mockMvc.perform(get(habitManagementLink)
            .param("page", "0")
            .param("size", "5"))
            .andExpect(view().name("core/management_user_habits"))
            .andExpect(model().attribute("pageable", habitManagementDtoPageableDto))
            .andExpect(model().attribute("languages", languageDtos))
            .andExpect(status().isOk());

        verify(managementHabitService).getAllHabitsDto(pageable);
    }

    @Test
    void getHabitById() throws Exception {
        this.mockMvc.perform(get(habitManagementLink + "/find?id=1"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void save() throws Exception {
        HabitManagementDto habitManagementDto = new HabitManagementDto();
        Gson gson = new Gson();
        String json = gson.toJson(habitManagementDto);

        MockMultipartFile jsonFile =
            new MockMultipartFile("habitManagementDto", "", "application/json", json.getBytes());
        this.mockMvc.perform(multipart(habitManagementLink + "/save")
            .file(jsonFile)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(managementHabitService, never()).saveHabitAndTranslations(habitManagementDto, jsonFile);
    }

    @Test
    void update() throws Exception {
        HabitManagementDto habitManagementDto = new HabitManagementDto();
        Gson gson = new Gson();
        String json = gson.toJson(habitManagementDto);

        MockMultipartFile jsonFile =
            new MockMultipartFile("habitManagementDto", "", "application/json", json.getBytes());

        this.mockMvc.perform(multipart(habitManagementLink + "/update")
            .file(jsonFile)
            .with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest mockHttpServletRequest) {
                    mockHttpServletRequest.setMethod("PUT");
                    return mockHttpServletRequest;
                }
            })).andExpect(status().isOk());

        verify(managementHabitService, never()).update(habitManagementDto, jsonFile);
    }

    @Test
    void delete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(habitManagementLink + "/delete?id=1"))
            .andExpect(status().isOk());

        verify(managementHabitService).delete(1L);
    }

    @Test
    void deleteAll() throws Exception {
        List<Long> idsToDelete = Arrays.asList(1L, 2L);
        Gson gson = new Gson();
        String json = gson.toJson(idsToDelete);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(habitManagementLink + "/deleteAll")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(managementHabitService).deleteAll(idsToDelete);
    }
}
