package greencity.controller;

import greencity.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
@AllArgsConstructor
public class CommentController {
    /**
     * Autowired CommentService instance.
     */
    private CommentService commentService;
}
