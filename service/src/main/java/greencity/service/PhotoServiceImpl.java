package greencity.service;

import greencity.dto.photo.PhotoVO;
import greencity.repository.PhotoRepo;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final PhotoRepo photoRepo;
    private final ModelMapper modelMapper;

    @Override
    public Optional<PhotoVO> findByName(String name) {
        return photoRepo.findByName(name).map(photo -> modelMapper.map(photo, PhotoVO.class));
    }
}
