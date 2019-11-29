package greencity.controller;

import static greencity.constant.ErrorMessage.INVALID_HABIT_ID;

import greencity.constant.HttpStatuses;
import greencity.dto.fact.HabitFactDTO;
import greencity.entity.HabitFact;
import greencity.service.impl.HabitFactServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fact")
@AllArgsConstructor
public class HabitFactController {
    private HabitFactServiceImpl habitFactService;

    /**
     * The controller which returns random {@link HabitFact} by HabitDictionary adviceId.
     *
     * @param habitId HabitDictionary
     * @return {@link HabitFactDTO}
     * @author Vitaliy Dzen
     */
    @ApiOperation("Get random habit fact by habit adviceId")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = INVALID_HABIT_ID),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/random/{habitId}")
    public HabitFactDTO getRandomAdviceByHabitId(@PathVariable Long habitId) {
        return habitFactService.getRandomHabitFactByHabitId(habitId);
    }
}
