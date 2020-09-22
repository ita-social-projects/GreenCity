package greencity.service.impl;

import greencity.entity.Photo;
import greencity.repository.PhotoRepo;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PhotoServiceImplTest {

    @Mock
    PhotoRepo photoRepo;
    @InjectMocks
    PhotoServiceImpl photoService;

    @Test
    void findByNameTest() {
        when(photoRepo.findByName("test")).thenReturn(Optional.of(new Photo()));
        assertEquals(Optional.of(new Photo()), photoService.findByName("test"));
    }

    @Test
    void findByNameEmptyTest() {
        when(photoRepo.findByName("")).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), photoService.findByName(""));
    }
}