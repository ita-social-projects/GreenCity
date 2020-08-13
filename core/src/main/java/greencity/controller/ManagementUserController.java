package greencity.controller;

import greencity.entity.User;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
@RequestMapping("/management/users")
public class ManagementUserController {
    private UserService userService;

    /**
     * Returns management page with all {@link User}.
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
                              @RequestParam(defaultValue = "5") int size) {
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        model.addAttribute("users", userService.findByPage(paging).getPage());
        return "core/management_users_list";
    }
}
