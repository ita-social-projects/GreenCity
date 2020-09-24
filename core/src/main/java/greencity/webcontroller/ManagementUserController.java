package greencity.webcontroller;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.genericresponse.FieldErrorDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.user.UserManagementDto;
import greencity.entity.User;
import greencity.security.service.OwnSecurityService;
import greencity.service.UserService;
import java.util.List;
import javax.validation.Valid;
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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/management/users")
public class ManagementUserController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final OwnSecurityService ownSecurityService;

    /**
     * Method that returns management page with all {@link User}.
     *
     * @param query    Query for searching related data
     * @param model    Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     * @author Vasyl Zhovnir
     */
    @GetMapping
    public String getAllUsers(@RequestParam(required = false, name = "query") String query, Pageable pageable,
                              Model model) {
        Pageable paging = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").descending());
        PageableAdvancedDto<UserManagementDto> pageableDto = query == null || query.isEmpty()
            ? userService.findUserForManagementByPage(paging) : userService.searchBy(paging, query);
        model.addAttribute("users", pageableDto);
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
        if (bindingResult.hasErrors()) {
            GenericResponseDto genericResponseDto = new GenericResponseDto();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                genericResponseDto.getErrors().add(
                    new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()));
            }
            return genericResponseDto;
        }
        ownSecurityService.managementRegisterUser(userDto);
        return GenericResponseDto.builder().build();
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
        if (bindingResult.hasErrors()) {
            GenericResponseDto genericResponseDto = new GenericResponseDto();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                genericResponseDto.getErrors().add(
                    new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()));
            }
            return genericResponseDto;
        }
        userService.updateUser(userDto);
        return GenericResponseDto.builder().build();
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
    public ResponseEntity<ResponseEntity.BodyBuilder> deactivateUser(@RequestParam("id") Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for setting {@link User}'s status to ACTIVATED.
     *
     * @param id of the searched {@link User}.
     * @author Vasyl Zhovnir
     */
    @PostMapping("/activate")
    public ResponseEntity<ResponseEntity.BodyBuilder> setActivatedStatus(@RequestParam("id") Long id) {
        userService.setActivatedStatus(id);
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
