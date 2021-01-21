package greencity.webcontroller;

import greencity.client.RestClient;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.genericresponse.GenericResponseDto;

import static greencity.constant.AppConstant.AUTHORIZATION;
import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;

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
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/management/users")
public class ManagementUserController {
    private final ModelMapper modelMapper;
    private final RestClient restClient;

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
    public String getAllUsers(@RequestParam(required = false, name = "query") String query, Pageable pageable,
        Model model, @RequestHeader(AUTHORIZATION) String accessToken) {
        Pageable paging = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").descending());
        PageableAdvancedDto<UserManagementDto> pageableDto = query == null || query.isEmpty()
            ? restClient.findUserForManagementByPage(paging, accessToken)
            : restClient.searchBy(paging, query, accessToken);
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
    public GenericResponseDto saveUser(@Valid @RequestBody UserManagementDto userDto, BindingResult bindingResult,
        @RequestHeader(AUTHORIZATION) String accessToken) {
        if (!bindingResult.hasErrors()) {
            restClient.managementRegisterUser(userDto, accessToken);
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
    public GenericResponseDto updateUser(@Valid @RequestBody UserManagementDto userDto, BindingResult bindingResult,
        @RequestHeader(AUTHORIZATION) String accessToken) {
        if (!bindingResult.hasErrors()) {
            restClient.updateUser(userDto, accessToken);
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
    public UserManagementDto findById(@RequestParam("id") Long id, @RequestHeader(AUTHORIZATION) String accessToken) {
        UserVO byId = restClient.findById(id, accessToken);
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
    public List<UserManagementDto> findFriendsById(@PathVariable Long id,
        @RequestHeader(AUTHORIZATION) String accessToken) {
        return restClient.findUserFriendsByUserId(id, accessToken);
    }

    /**
     * Method for setting {@link UserVO}'s status to DEACTIVATED, so the user will
     * not be able to log in into the system.
     *
     * @param id of the searched {@link UserVO}.
     * @author Vasyl Zhovnir
     */
    @PostMapping("/deactivate")
    public ResponseEntity<ResponseEntity.BodyBuilder> deactivateUser(@RequestParam("id") Long id,
        @RequestHeader(AUTHORIZATION) String accessToken) {
        restClient.deactivateUser(id, accessToken);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for setting {@link UserVO}'s status to ACTIVATED.
     *
     * @param id of the searched {@link UserVO}.
     * @author Vasyl Zhovnir
     */
    @PostMapping("/activate")
    public ResponseEntity<ResponseEntity.BodyBuilder> setActivatedStatus(@RequestParam("id") Long id,
        @RequestHeader(AUTHORIZATION) String accessToken) {
        restClient.setActivatedStatus(id, accessToken);
        return ResponseEntity.status(HttpStatus.OK).build();
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
    public ResponseEntity<List<Long>> deactivateAll(@RequestBody List<Long> listId,
        @RequestHeader(AUTHORIZATION) String accessToken) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(restClient.deactivateAllUsers(listId, accessToken));
    }
}
