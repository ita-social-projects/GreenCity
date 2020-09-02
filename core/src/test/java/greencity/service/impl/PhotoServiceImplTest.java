package greencity.service.impl;

import greencity.GreenCityApplication;
import greencity.entity.Photo;
import greencity.repository.PhotoRepo;
import greencity.service.PhotoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.mockito.Mockito.when;


import static org.junit.Assert.*;

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