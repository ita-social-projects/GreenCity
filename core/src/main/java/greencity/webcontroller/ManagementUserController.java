package greencity.webcontroller;

import greencity.dto.user.UserManagementDto;
import greencity.entity.User;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@AllArgsConstructor
@RequestMapping("/management")
@ApiIgnore
public class ManagementUserController {
    private UserService userService;
    private ModelMapper modelMapper;

    /**
     * Method that returns management page with all {@link User}.
     *
     * @param model Model that will be configured and returned to user.
     * @param page  Page index you want to retrieve.
     * @param size  Number of records per page.
     * @return View template path {@link String}.
     * @author Vasyl Zhovnir
     */
    @GetMapping("/users")
    public String getAllUsers(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size) {
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        model.addAttribute("users", userService.findUserForManagementByPage(paging));
        model.addAttribute("currentPage", page);
        return "core/management_users_list";
    }

    /**
     * Method that shows form for updating {@link User}.
     *
     * @param id    {@link User}'s id.
     * @param model Model that will be configured and returned to user.
     * @return View template path {@link String}.
     * @author Vasyl Zhovnir
     */
    @GetMapping("/updateUserForm")
    public String showForm(@RequestParam Long id, Model model) {
        model.addAttribute("user", modelMapper.map(userService.findById(id), UserManagementDto.class));
        return "core/management_user_update_form";
    }

    /**
     * Method that updates user data.
     *
     * @param userDto dto with updated fields.
     * @return View template path {@link String}.
     * @author Vasyl Zhovnir
     */
    @PostMapping("/update")
    public String updateUser(@Valid @ModelAttribute("user") UserManagementDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "core/management_user_update_form";
        }
        userService.updateUser(userDto);
        return "redirect:/management/users";
    }

    /**
     * Method for finding {@link User} by id.
     *
     * @param id of the searched {@link User}.
     * @return dto {@link UserManagementDto} of the {@link User}.
     * @author Vasyl Zhovnir
     */
    @GetMapping("/findById")
    @ResponseBody
    public UserManagementDto findById(Long id) {
        User byId = userService.findById(id);
        return modelMapper.map(byId, UserManagementDto.class);
    }

    /**
     * Method for setting {@link User}'s status to DEACTIVATED,
     * so the user will not be able to log in into the system.
     *
     * @param id of the searched {@link User}.
     * @author Vasyl Zhovnir
     */
    @PostMapping
    public ResponseEntity deactivateUser(@RequestParam Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
