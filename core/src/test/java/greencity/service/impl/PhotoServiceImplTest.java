package greencity.service.impl;

import greencity.entity.Photo;
import greencity.repository.PhotoRepo;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PhotoServiceImplTest {

    @Mock
    PhotoRepo photoRepo;
    @InjectMocks
    PhotoServiceImpl photoService;

    @Test
    public void findByNameTest() {
        when(photoRepo.findByName("test")).thenReturn(Optional.of(new Photo()));
        assertEquals(Optional.of(new Photo()), photoService.findByName("test"));
    }

    @Test
    public void findByNameEmptyTest() {
        when(photoRepo.findByName("")).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), photoService.findByName(""));
    }
}