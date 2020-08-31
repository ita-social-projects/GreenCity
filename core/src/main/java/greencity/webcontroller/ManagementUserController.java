package greencity.webcontroller;

import greencity.dto.genericresponse.FieldErrorDto;
import greencity.dto.genericresponse.GenericResponseDto;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/management/users")
public class ManagementUserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    /**
     * Method that returns management page with all {@link User}.
     *
     * @param model Model that will be configured and returned to user.
     * @param page  Page index you want to retrieve.
     * @param size  Number of records per page.
     * @return View template path {@link String}.
     * @author Vasyl Zhovnir
     */
    @GetMapping("")
    public String getAllUsers(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size) {
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        model.addAttribute("users", userService.findUserForManagementByPage(paging));
        return "core/management_user";
    }



    /**
     * Method that updates user data.
     *
     * @param userDto dto with updated fields.
     * @return View template path {@link String}.
     * @author Vasyl Zhovnir
     */
    @PutMapping("")
    @ResponseBody
    public GenericResponseDto updateUser(@Valid @RequestBody UserManagementDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            GenericResponseDto genericResponseDto = new GenericResponseDto();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                genericResponseDto.getErrors().add(
                    new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()));
            }
            return genericResponseDto;
        }
        userService.updateUser(userDto);
        return GenericResponseDto.builder().errors(new ArrayList<>()).build();
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
    public UserManagementDto findById(@RequestParam("id") Long id) {
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
    @PostMapping("/deactivate")
    public ResponseEntity deactivateUser(@RequestParam("id") Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for setting to a list of {@link User} status DEACTIVATED,
     * so the users will not be able to log in into the system.
     *
     * @param listId {@link List} populated with ids of {@link User} to be deleted.
     * @author Vasyl Zhovnir
     */
    @PostMapping("/deactivateAll")
    public ResponseEntity<List<Long>> deactivateAll(@RequestBody List<Long> listId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userService.deactivateAllUsers(listId));
    }
}
