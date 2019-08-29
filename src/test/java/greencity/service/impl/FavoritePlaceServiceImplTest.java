package greencity.service.impl;

import greencity.GreenCityApplication;
import greencity.dto.favoritePlace.FavoritePlaceDto;
import greencity.exception.NotFoundException;
import greencity.repository.FavoritePlaceRepo;
import greencity.service.PlaceService;
import greencity.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class FavoritePlaceServiceImplTest {
    @MockBean private FavoritePlaceRepo repo;
    @MockBean private UserService userService;
    @MockBean private PlaceService placeService;
    @MockBean private ModelMapper modelMapper;

//    @Test
//    public void deleteByPlaceIdAndUserEmail() {
//        FavoritePlaceDto favoritePlaceDto= new FavoritePlaceDto();
//        when(repo.existsByPlaceIdAndUserEmail(favoritePlaceDto.getPlace().getId(), favoritePlaceDto.getUser().getEmail())).thenThrow(NotFoundException.class);
//        Mockito.when(ку.findById(1L)).thenReturn(Optional.of(placeToDelete));
//        Assert.assertEquals(true, placeService.deleteById(1L));
//    }

}
