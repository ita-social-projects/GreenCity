package greencity.webcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.goal.GoalManagementDto;
import greencity.dto.goal.GoalPostDto;
import greencity.dto.goal.GoalViewDto;
import greencity.dto.language.LanguageDTO;
import greencity.service.GoalService;
import greencity.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ManagementGoalsControllerTest {
    private static final String managementGoalLink = "/management/goals";

    private MockMvc mockMvc;

    @InjectMocks
    private ManagementGoalsController managementGoalsController;

    @Mock
    private GoalService goalService;

    @Mock
    private LanguageService languageService;

    @Mock
    private Validator mockValidator;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(managementGoalsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(mockValidator)
            .build();
    }

    @Test
    void getAllGoalsTest() throws Exception {
        int page = 0;
        int size = 10;
        Pageable paging = PageRequest.of(page, size, Sort.by("id").ascending());
        List<GoalManagementDto> goalManagementDtos = Collections.singletonList(new GoalManagementDto());
        PageableAdvancedDto<GoalManagementDto> goalManagementDtoPageableDto =
            new PageableAdvancedDto<>(goalManagementDtos, 1, 0, 1, 1,
                true, true, true, true);
        when(goalService.findGoalForManagementByPage(paging)).thenReturn(goalManagementDtoPageableDto);
        this.mockMvc.perform(get(managementGoalLink)
            .param("page", "0")
            .param("size", "10"))
            .andExpect(view().name("core/management_goals"))
            .andExpect(status().isOk());

        verify(goalService).findGoalForManagementByPage(paging);
    }

    @Test
    void saveTest() throws Exception {
        GoalPostDto goalPostDto = ModelUtils.getGoalPostDto();
        String goalGtoJson = objectMapper.writeValueAsString(goalPostDto);

        mockMvc.perform(post(managementGoalLink)
            .content(goalGtoJson)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(goalService).saveGoal(goalPostDto);
    }

    @Test
    void getGoalByIdTest() throws Exception {
        Long goalId = 1L;
        mockMvc.perform(get(managementGoalLink + "/" + goalId))
            .andExpect(status().isOk());

        verify(goalService).findGoalById(goalId);
    }

    @Test
    void deleteTest() throws Exception {
        Long habitFactId = 1L;
        mockMvc.perform(delete(managementGoalLink + "/" + habitFactId))
            .andExpect(status().isOk());

        verify(goalService).delete(habitFactId);
    }

    @Test
    void deleteAllTest() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        mockMvc.perform(delete(managementGoalLink + "/deleteAll")
            .content("[1,2]")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(goalService).deleteAllGoalByListOfId(ids);
    }

    @Test
    void updateHabitFactsTest() throws Exception {
        GoalPostDto goalPostDto = ModelUtils.getGoalPostDto();
        Long id = 1L;
        String goalGtoJson = objectMapper.writeValueAsString(goalPostDto);

        mockMvc.perform(put(managementGoalLink + "/" + id)
            .content(goalGtoJson)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(goalService).update(goalPostDto);
    }

    @Test
    void filterDataTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 3);
        GoalViewDto goalViewDto = new GoalViewDto();
        List<GoalManagementDto> list = Collections.singletonList(new GoalManagementDto());
        PageableAdvancedDto<GoalManagementDto> pageableDto = new PageableAdvancedDto<>(list, 3, 0, 3,
            0, false, true, true, false);
        when(goalService.getFilteredDataForManagementByPage(pageable, goalViewDto)).thenReturn(pageableDto);
        when(languageService.getAllLanguages()).thenReturn(Collections.singletonList(ModelUtils.getLanguageDTO()));
        this.mockMvc.perform(post(managementGoalLink + "/filter")
            .param("page", "0")
            .param("size", "3"))
            .andExpect(model().attribute("goals", pageableDto))
            .andExpect(model().attribute("languages", languageService.getAllLanguages()))
            .andExpect(model().attribute("fields", goalViewDto))
            .andExpect(view().name("core/management_goals"))
            .andExpect(status().isOk());
        verify(goalService).getFilteredDataForManagementByPage(pageable, goalViewDto);
        verify(languageService, times(2)).getAllLanguages();
    }

    @Test
    void getAllGoalsSearchByQueryTest() throws Exception {
        Pageable paging = PageRequest.of(0, 3, Sort.by("id").ascending());
        List<GoalManagementDto> goalManagementDtos = Collections.singletonList(new GoalManagementDto());
        PageableAdvancedDto<GoalManagementDto> goalManagementDtoPageableDto =
            new PageableAdvancedDto<>(goalManagementDtos, 1, 0, 1, 1,
                true, true, true, true);
        when(goalService.searchBy(paging, "query")).thenReturn(goalManagementDtoPageableDto);
        when(languageService.getAllLanguages()).thenReturn(Collections.singletonList(new LanguageDTO()));
        this.mockMvc.perform(get(managementGoalLink + "?query=query")
            .param("page", "0")
            .param("size", "3"))
            .andExpect(model().attribute("goals", goalManagementDtoPageableDto))
            .andExpect(model().attribute("languages", languageService.getAllLanguages()))
            .andExpect(view().name("core/management_goals"))
            .andExpect(status().isOk());
        verify(goalService).searchBy(paging, "query");
        verify(languageService, times(2)).getAllLanguages();
    }

}
