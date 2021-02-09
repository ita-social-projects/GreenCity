package greencity.webcontroller;

import com.google.gson.Gson;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.goal.GoalManagementDto;
import greencity.service.GoalService;
import greencity.service.HabitService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagementHabitShopingListItemControllerTest {

    private static final String shopingManagementLink = "/management/habit-shoping-list";

    private MockMvc mockMvc;
    @Mock
    private GoalService goalService;
    @Mock
    private HabitService habitService;

    @InjectMocks
    private ManagementHabitShopingListItemController itemController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(itemController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void getAllShopingListGoalsTest() throws Exception {

        Pageable pageable = PageRequest.of(0, 5);
        GoalManagementDto goalManagementDto = new GoalManagementDto();
        goalManagementDto.setId(1L);

        List<GoalManagementDto> goalList = Collections.singletonList(goalManagementDto);

        PageableAdvancedDto<GoalManagementDto> goalManagementDTO = new PageableAdvancedDto<>();
        goalManagementDTO.setPage(goalList);
        goalManagementDTO.setCurrentPage(0);
        goalManagementDTO.setTotalElements(5);
        goalManagementDTO.setNumber(1);

        when(goalService.findAllGoalForManagementPageNotContained(1L, pageable))
            .thenReturn(goalManagementDTO);
        when(goalService.getGoalByHabitId(1L)).thenReturn(goalList);

        this.mockMvc.perform(get(shopingManagementLink + "?habitId=1")
            .param("page", "0")
            .param("size", "5"))
            .andExpect(view().name("core/management_habit_shoping_list_item"))
            .andExpect(model().attribute("allGoal", goalManagementDTO))
            .andExpect(model().attribute("habitId", 1L))
            .andExpect(model().attribute("currentCoals", goalList))
            .andExpect(status().isOk());
        verify(goalService).findAllGoalForManagementPageNotContained(1L, pageable);
    }

    @Test
    void deleteAllShopingListItem() throws Exception {
        List<Long> listID = Arrays.asList(1L, 2L);
        Gson gson = new Gson();
        String json = gson.toJson(listID);
        when(habitService.deleteAllGoalByListOfId(1L, listID)).thenReturn(listID);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(shopingManagementLink + "/deleteAll/?habitId=1")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitService).deleteAllGoalByListOfId(1L, listID);
    }

    @Test
    void addAllShopingListItemTest() throws Exception {
        List<Long> listID = Arrays.asList(1L, 2L);
        Gson gson = new Gson();
        String json = gson.toJson(listID);
        when(habitService.addAllGoalByListOfId(1L, listID)).thenReturn(listID);

        this.mockMvc.perform(MockMvcRequestBuilders.post(shopingManagementLink + "/addAll/?habitId=1")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitService).addAllGoalByListOfId(1L, listID);
    }

    @Test
    void deleteShopingListItemTest() throws Exception {
        doNothing().when(habitService).deleteGoal(1L, 1L);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(shopingManagementLink + "/delete/?habitId=1&goalId=1"))
            .andExpect(status().isOk());
        verify(habitService).deleteGoal(1L, 1L);

    }

    @Test
    void addShopingListItemToHabit() throws Exception {
        doNothing().when(habitService).addGoalToHabit(1L, 1L);
        this.mockMvc.perform(MockMvcRequestBuilders.post(shopingManagementLink + "/add/?habitId=1&goalId=1"))
            .andExpect(status().isOk());
        verify(habitService).addGoalToHabit(1L, 1L);
    }
}
