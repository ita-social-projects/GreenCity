package greencity.controller;

import greencity.dto.user.UserForListDto;
import greencity.entity.enums.ROLE;
import greencity.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private UserService userService;

    @PostMapping("update/role")
    public ResponseEntity<?> updateRole(
            @RequestParam("id") Long id, @RequestParam("role") ROLE role) {
        userService.updateRole(id, role);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("block")
    public ResponseEntity<?> blockUser(@RequestParam("id") Long id) {
        userService.blockUser(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(Pageable pageable) {
        return new ResponseEntity<List<UserForListDto>>(
                userService.findAll(pageable), HttpStatus.OK);
    }

    @PostMapping("ban")
    public ResponseEntity<?> banUser(@RequestParam("id") Long id) {
        userService.banUser(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
