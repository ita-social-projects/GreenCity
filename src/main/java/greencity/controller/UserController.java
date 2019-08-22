package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.user.UserForListDto;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping("update/role")
    public ResponseEntity<?> updateRole(
            @RequestParam("id") Long id, @RequestParam("role") ROLE role) {
        userService.updateRole(id, role);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("update/status")
    public ResponseEntity<?> updateUserStatus(
            @RequestParam("id") Long id, @RequestParam UserStatus status) {
        userService.updateUserStatus(id, status);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(Pageable pageable) {
        return new ResponseEntity<PageableDto<UserForListDto>>(userService.findByPage(pageable)
                , HttpStatus.OK);
    }
}
