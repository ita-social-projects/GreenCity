package greencity.service.impl;

import greencity.repository.PhotoRepo;
import greencity.service.PhotoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * The class provides implementation of the {@code PhotoService}.
 *
 * @author Marian Milian
 * @version 1.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class  PhotoServiceImpl implements PhotoService {
    private PhotoRepo photoRepo;
}
