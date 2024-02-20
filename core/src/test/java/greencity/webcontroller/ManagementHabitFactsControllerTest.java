package greencity.webcontroller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactUpdateDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.habitfact.HabitFactViewDto;
import greencity.dto.language.LanguageDTO;
import greencity.service.HabitFactService;
import greencity.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManagementHabitFactsControllerTest {

    private static final String managementHabitFactLink = "/management/facts";

    private MockMvc mockMvc;

    @Mock
    private HabitFactService habitFactService;

    @Mock
    private LanguageService languageService;

    @Mock
    private Validator mockValidator;

    @InjectMocks
    private ManagementHabitFactsController habitFactsController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitFactsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(mockValidator)
            .build();
    }

    @Test
    void findAllHabitFactsTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        List<HabitFactVO> habitFactVOS = Collections.singletonList(ModelUtils.getHabitFactVO());
        PageableDto<HabitFactVO> pageableDto = new PageableDto<>(habitFactVOS, 1, 0, 1);
        when(habitFactService.getAllHabitFactVOsWithFilter(null, pageable)).thenReturn(pageableDto);
        List<LanguageDTO> languageDTOS = Collections.emptyList();
        when(languageService.getAllLanguages()).thenReturn(languageDTOS);

        mockMvc.perform(get(managementHabitFactLink))
            .andExpect(view().name("core/management_habit_facts"))
            .andExpect(model().attribute("pageable", pageableDto))
            .andExpect(model().attribute("languages", languageDTOS))
            .andExpect(status().isOk());

        verify(habitFactService).getAllHabitFactVOsWithFilter(null, pageable);
        verify(languageService).getAllLanguages();
    }

    @Test
    void findAllHabitFactsTestWithQuery() throws Exception {
        String query = "eng";
        Pageable pageable = PageRequest.of(0, 20);
        List<HabitFactVO> habitFactVOS = Collections.singletonList(ModelUtils.getHabitFactVO());
        PageableDto<HabitFactVO> pageableDto = new PageableDto<>(habitFactVOS, 1, 0, 1);
        when(habitFactService.getAllHabitFactVOsWithFilter(query, pageable)).thenReturn(pageableDto);
        List<LanguageDTO> languageDTOS = Collections.emptyList();
        when(languageService.getAllLanguages()).thenReturn(languageDTOS);

        mockMvc.perform(get(managementHabitFactLink + "?page=0&size=20&query=" + query))
            .andExpect(view().name("core/management_habit_facts"))
            .andExpect(model().attribute("pageable", pageableDto))
            .andExpect(model().attribute("languages", languageDTOS))
            .andExpect(status().isOk());

        verify(habitFactService).getAllHabitFactVOsWithFilter(query, pageable);
        verify(languageService).getAllLanguages();
    }

    @Test
    void filterDataTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        HabitFactViewDto habitFactViewDto = new HabitFactViewDto("1", "", "Pro");
        String habitFactViewDtoAsJson = objectMapper.writeValueAsString(habitFactViewDto);

        mockMvc.perform(post(managementHabitFactLink + "/filter")
            .contentType(MediaType.APPLICATION_JSON)
            .content(habitFactViewDtoAsJson))
            .andExpect(status().isOk());

        verify(habitFactService).getFilteredDataForManagementByPage(eq(pageable), any(HabitFactViewDto.class));
    }

    @Test
    void getHabitFactsByIdTest() throws Exception {
        Long habitFactId = 1L;
        mockMvc.perform(get(managementHabitFactLink + "/find/" + habitFactId))
            .andExpect(status().isOk());

        verify(habitFactService).getHabitFactById(habitFactId);
    }

    @Test
    void saveHabitFactsTest() throws Exception {
        HabitFactPostDto habitFactPostDto = ModelUtils.getHabitFactPostDto();
        String habitFactPostDtoAsJson = objectMapper.writeValueAsString(habitFactPostDto);

        mockMvc.perform(post(managementHabitFactLink)
            .content(habitFactPostDtoAsJson)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(habitFactService).save(habitFactPostDto);
    }

    @Test
    void deleteTest() throws Exception {
        Long habitFactId = 1L;
        mockMvc.perform(delete(managementHabitFactLink + "/" + habitFactId))
            .andExpect(status().isOk());

        verify(habitFactService).delete(habitFactId);
    }

    @Test
    void deleteAllTest() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        mockMvc.perform(delete(managementHabitFactLink + "/deleteAll")
            .content("[1,2]")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(habitFactService).deleteAllHabitFactsByListOfId(ids);
    }

    @Test
    void updateHabitFactsTest() throws Exception {
        HabitFactUpdateDto habitFactUpdateDto = ModelUtils.getHabitFactUpdateDto();
        Long id = 1L;
        String habitFactUpdateDtoAsJson = objectMapper.writeValueAsString(habitFactUpdateDto);

        mockMvc.perform(put(managementHabitFactLink + "/" + id)
            .content(habitFactUpdateDtoAsJson)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(habitFactService).update(habitFactUpdateDto, id);
    }
}
