package greencity.service.impl;

import greencity.entity.Photo;
import greencity.repository.PhotoRepo;
import greencity.service.PhotoService;
import java.util.Optional;
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
public class PhotoServiceImpl implements PhotoService {
    private PhotoRepo photoRepo;

    @Override
    public Optional<Photo> findByName(String name) {
        return photoRepo.findByName(name);
    }
}
