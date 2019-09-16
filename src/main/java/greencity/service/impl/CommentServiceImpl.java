package greencity.service.impl;

import greencity.repository.CommentRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * The class provides implementation of the {@code CommentService}.
 *
 * @author Marian Milian
 * @version 1.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class CommentServiceImpl {
    private CommentRepo commentRepo;
}
