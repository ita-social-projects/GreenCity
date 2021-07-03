package greencity.webcontroller;

import greencity.annotations.CurrentUser;
import greencity.annotations.ValidLanguage;
import greencity.client.RestClient;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.place.PlaceVO;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.dto.user.UserVO;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.service.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Controller
@AllArgsConstructor
@RequestMapping("/management/users/{id}")
public class ManagementUserPersonalPageController {
    private final RestClient restClient;
    private final HabitAssignService habitAssignService;
    private final EcoNewsService ecoNewsService;
    private final TipsAndTricksService tipsAndTricksService;
    private final PlaceService placeService;
    private final UserService userService;

    @GetMapping
    public String getUser(@PathVariable Long id,
                          @RequestParam(required = false, name = "query") String query, Model model,
                          @ApiIgnore @ValidLanguage Locale locale) {
        UserVO user = userService.findById(id);

        List<HabitAssignDto> acquiredHabits = habitAssignService
                .getAllHabitAssignsByUserIdAndStatusAcquired(id, locale.getLanguage());
        List<HabitAssignDto> inProgressHabits = habitAssignService
                .findInprogressHabitAssignsOnDate(id, LocalDate.now(), locale.getLanguage());
        List<HabitAssignDto> cancelledHabits = habitAssignService
                .getAllHabitAssignsByUserIdAndCancelledStatus(id, locale.getLanguage());
        List<HabitAssignDto> customHabits = habitAssignService
                .getAllCustomHabitAssignsByUserId(id, locale.getLanguage());
        List<EcoNewsDto> publishedEcoNews = ecoNewsService.getAllPublishedNewsByUserId(user.getId());
        List<TipsAndTricksDtoResponse> publishedTipsAndTricks = tipsAndTricksService
                .getAllTipsAndTricksByUserId(user.getId());
        List<PlaceVO> createdEcoPlaces = placeService.getAllCreatedPlacesByUserId(user.getId());

        HabitAssignDto habit1 = inProgressHabits.get(0);
        model.addAttribute("habit1", habit1.getHabit());

        model.addAttribute("user", user);
        model.addAttribute("acquiredHabits", acquiredHabits);
        model.addAttribute("inProgressHabits", inProgressHabits);
        model.addAttribute("cancelledHabits", cancelledHabits);
        model.addAttribute("customHabits", customHabits);
        model.addAttribute("publishedEcoNews", publishedEcoNews);
        model.addAttribute("publishedTipsAndTricks", publishedTipsAndTricks);
        model.addAttribute("createdEcoPlaces", createdEcoPlaces);

        return "core/management_user_personal_page";
    }

    @PostMapping(value = "/updateUserStatus")
    public String updateUserStatus(@PathVariable Long id, @RequestParam(name = "userStatus") String userStatus,
                                   @CurrentUser UserVO currentUser) {
        UserStatus status = UserStatus.valueOf(userStatus.toUpperCase());
        userService.updateStatus(id, status, currentUser.getEmail());
        return "redirect:core/management_user_personal_page";
    }

    @PostMapping(value = "/updateUserRole")
    public String updateUserRole(@PathVariable Long id, @RequestParam(name = "userRole") String userRole,
                                 @CurrentUser UserVO currentUser) {
        Role role = Role.valueOf("ROLE_" + userRole.toUpperCase());
        userService.updateRole(id, role, currentUser.getEmail());
        return "redirect:core/management_user_personal_page";
    }

    @PostMapping(value = "/updateHabit/{habitAssignId}")
    public String updateHabit(@PathVariable Long id, @RequestParam(name = "userRole") String userRole,
                              @PathVariable Long habitAssignId, @CurrentUser UserVO currentUser) {

        return "redirect:core/management_user_personal_page";
    }
}