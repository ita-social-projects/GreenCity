package greencity.webcontroller;

import greencity.annotations.CurrentUser;
import greencity.client.RestClient;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserManagementViewDto;
import greencity.dto.user.UserVO;
import greencity.enums.Role;
import greencity.service.HabitAssignService;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;

@Validated
@Controller
@AllArgsConstructor
@RequestMapping("/management/users")
public class ManagementUserController {
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final UserService userService;
    private final HabitAssignService habitAssignService;

    /**
     * Method that returns management page with all {@link UserVO}.
     *
     * @param query    Query for searching related data
     * @param model    Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     * @author Vasyl Zhovnir
     */
    @GetMapping
    public String getAllUsers(@RequestParam(required = false, name = "query") String query, Model model,
        UserManagementViewDto userViewDto,
        @ApiIgnore Pageable pageable) {
        PageableAdvancedDto<UserManagementVO> found = restClient.search(pageable, userViewDto);
        model.addAttribute("paging", pageable);
        model.addAttribute("users", found);

        return "core/management_user";
    }

    /**
     * Register new user from admin panel.
     *
     * @param userDto dto with info for registering user.
     * @return {@link GenericResponseDto}
     * @author Vasyl Zhovnir
     */
    @PostMapping("/register")
    @ResponseBody
    public GenericResponseDto saveUser(@Valid @RequestBody UserManagementDto userDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            restClient.managementRegisterUser(userDto);
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method that updates user data.
     *
     * @param userDto dto with updated fields.
     * @return {@link GenericResponseDto}
     * @author Vasyl Zhovnir
     */
    @PutMapping
    @ResponseBody
    public GenericResponseDto updateUser(@Valid @RequestBody UserManagementDto userDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            restClient.updateUser(userDto);
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method for finding {@link UserVO} by id.
     *
     * @param id of the searched {@link UserVO}.
     * @return dto {@link UserManagementDto} of the {@link UserVO}.
     * @author Vasyl Zhovnir
     */
    @GetMapping("/findById")
    @ResponseBody
    public UserManagementDto findById(@RequestParam("id") Long id) {
        UserVO byId = restClient.findById(id);
        return modelMapper.map(byId, UserManagementDto.class);
    }

    /**
     * Method that finds user's friends {@link UserManagementDto} by given id.
     *
     * @param id {@link Long} - user's id.
     * @return {@link List} of {@link UserManagementDto} instances.
     * @author Markiyan Derevetskyi
     */
    @GetMapping("/{id}/friends")
    @ResponseBody
    public List<UserManagementDto> findFriendsById(@PathVariable Long id) {
        return restClient.findUserFriendsByUserId(id);
    }

    /**
     * Method that change user's Role {@link Role} by given id.
     *
     * @param id          {@link Long} - user's id.
     * @param userRole    {@link String} - new user's Role.
     * @param currentUser {@link UserVO} - admin profile.
     * @author Stepan Tehlivets.
     */
    @GetMapping("/{id}/{role}")
    public String changeRole(@PathVariable Long id,
        @PathVariable(name = "role") String userRole,
        @CurrentUser UserVO currentUser) {
        Role role = Role.valueOf(userRole);
        userService.updateRole(id, role, currentUser.getEmail());
        return "redirect:/management/users";
    }

    /**
     * Method for setting {@link UserVO}'s status to DEACTIVATED, so the user will
     * not be able to log in into the system.
     *
     * @param id          of the searched {@link UserVO}.
     * @param userReasons {@link List} of {@link String}.
     * @author Vasyl Zhovnir
     */
    @PostMapping("/deactivate")
    public ResponseEntity<ResponseEntity.BodyBuilder> deactivateUser(
        @RequestParam("id") Long id,
        @RequestBody @NotEmpty List<@Size(min = 9) String> userReasons) {
        restClient.deactivateUser(id, userReasons);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for getting {@link String} user language.
     *
     * @param id of the searched {@link UserVO}.
     * @return current user language {@link String}.
     * @author Vlad Pikhotskyi
     */
    @GetMapping("/lang")
    public ResponseEntity<String> getUserLang(@RequestParam("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(restClient.getUserLang(id));
    }

    /**
     * Method for setting {@link UserVO}'s status to ACTIVATED.
     *
     * @param id of the searched {@link UserVO}.
     * @author Vasyl Zhovnir
     */
    @PostMapping("/activate")
    public ResponseEntity<ResponseEntity.BodyBuilder> setActivatedStatus(
        @RequestParam("id") Long id) {
        restClient.setActivatedStatus(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for getting list of {@link String}.
     *
     * @param id        {@link Long} - user's id.
     * @param adminLang {@link String} - current administrator language.
     * @return {@link List} of {@link String} - reasons for deactivation of the
     *         current user.
     * @author Vlad Pikhotskyi
     */
    @GetMapping("/reasons")
    public ResponseEntity<List<String>> getReasonsOfDeactivation(
        @RequestParam("id") Long id,
        @RequestParam("admin") String adminLang) {
        return ResponseEntity.status(HttpStatus.OK).body(restClient.getDeactivationReason(id, adminLang));
    }

    /**
     * Method for setting to a list of {@link UserVO} status DEACTIVATED, so the
     * users will not be able to log in into the system.
     *
     * @param listId {@link List} populated with ids of {@link UserVO} to be
     *               deleted.
     * @author Vasyl Zhovnir
     */
    @PostMapping("/deactivateAll")
    public ResponseEntity<List<Long>> deactivateAll(@RequestBody List<Long> listId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(restClient.deactivateAllUsers(listId));
    }

    /**
     * Method accepts request to search users by several values.
     *
     * @param model       {@link Model}
     * @param pageable    {@link Pageable}
     * @param userViewDto {@link UserManagementViewDto} - stores values.
     * @return path to html view.
     */
    @PostMapping("/search")
    public String search(Model model, @ApiIgnore Pageable pageable, UserManagementViewDto userViewDto) {
        PageableAdvancedDto<UserManagementVO> found = restClient.search(pageable, userViewDto);
        model.addAttribute("users", found);
        model.addAttribute("fields", userViewDto);
        model.addAttribute("paging", pageable);
        return "core/management_user";
    }

    /**
     * Method update shopping item by habitAssign id and shoppingListItem id.
     *
     * @param habitId {@link Long} habitAssignId.
     * @param itemId  {@link Long} shoppingListItemId.
     */
    @PutMapping(value = "/updateShoppingItem/{habitId}/{itemId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void updateShoppingItem(@PathVariable("itemId") Long itemId,
        @PathVariable("habitId") Long habitId) {
        habitAssignService.updateShoppingItem(habitId, itemId);
    }
}
