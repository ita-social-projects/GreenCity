package greencity.service.impl;

import greencity.GreenCityApplication;
import greencity.dto.favoritePlace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.exception.BadIdAndEmailException;
import greencity.exception.BadIdException;
import greencity.exception.NotFoundException;
import greencity.repository.FavoritePlaceRepo;
import greencity.service.FavoritePlaceService;
import greencity.service.PlaceService;
import greencity.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class FavoritePlaceServiceImplTest {
    @MockBean
    private FavoritePlaceRepo repo;
    @MockBean
    private UserService userService;
    @MockBean
    private PlaceService placeService;
    @MockBean
    private ModelMapper modelMapper;
    @Autowired
    private FavoritePlaceService favoritePlaceService;

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void saveTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        dto.setName("a");
        dto.setPlaceId(1L);
        dto.setUserEmail("a@");
        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        Mockito.when(userService.existsByEmail(any())).thenReturn(true);
        Mockito.when(placeService.existsById(any())).thenReturn(true);
        Mockito.when(repo.existsByPlaceIdAndUserEmail(any(), any())).thenReturn(false);
        Mockito.when(repo.save(any(FavoritePlace.class))).thenReturn(fp);
        Mockito.when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class)))
            .thenReturn(fp);
        Mockito.when(modelMapper.map(any(FavoritePlace.class), eq(FavoritePlaceDto.class)))
            .thenReturn(dto);
        Mockito.when(userService.findIdByEmail(anyString())).thenReturn((long) 3L);
        FavoritePlaceDto dto2 = favoritePlaceService.save(dto);

        verify(userService, times(1)).existsByEmail(fp.getUser().getEmail());
        verify(placeService, times(1)).existsById(any());
        verify(repo, times(1)).existsByPlaceIdAndUserEmail(any(), any());
        verify(repo, times(1)).save(any(FavoritePlace.class));
        verify(modelMapper, times(1)).map(any(FavoritePlaceDto.class), eq(FavoritePlace.class));
        verify(modelMapper, times(1)).map(any(FavoritePlace.class), eq(FavoritePlaceDto.class));
        verify(userService, times(1)).findIdByEmail(anyString());
        Assert.assertEquals(dto, dto2);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = BadIdException.class)
    public void saveBadUserEmailTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();

        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        Mockito.when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class)))
            .thenReturn(fp);
        Mockito.when(userService.existsByEmail(any())).thenReturn(false);
        favoritePlaceService.save(dto);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = BadIdException.class)
    public void saveBadPlaceIdTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();

        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        Mockito.when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class)))
            .thenReturn(fp);
        Mockito.when(userService.existsByEmail(any())).thenReturn(true);
        Mockito.when(placeService.existsById(any())).thenReturn(false);

        favoritePlaceService.save(dto);
    }

    @Test(expected = BadIdAndEmailException.class)
    public void saveFavoritePlaceAlreadyExistTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();

        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        Mockito.when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class)))
            .thenReturn(fp);
        Mockito.when(userService.existsByEmail(any())).thenReturn(true);
        Mockito.when(placeService.existsById(any())).thenReturn(true);
        Mockito.when(repo.existsByPlaceIdAndUserEmail(any(), any())).thenReturn(true);

        favoritePlaceService.save(dto);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void deleteByPlaceIdAndUserEmail() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        dto.setPlaceId(1L);
        dto.setUserEmail("a@");
        Mockito.when(repo.existsByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(true);
        doNothing().when(repo).deleteByPlaceIdAndUserEmail(anyLong(), anyString());
        favoritePlaceService.deleteByPlaceIdAndUserEmail(dto);
        verify(repo, times(1)).existsByPlaceIdAndUserEmail(anyLong(), anyString());
        verify(repo, times(1)).deleteByPlaceIdAndUserEmail(anyLong(), anyString());
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = NotFoundException.class)
    public void deleteByPlaceIdAndUserEmail_FavoritePlaceNotExist() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        dto.setPlaceId(1L);
        dto.setUserEmail("a@");
        Mockito.when(repo.existsByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(false);
        doNothing().when(repo).deleteByPlaceIdAndUserEmail(anyLong(), anyString());
        favoritePlaceService.deleteByPlaceIdAndUserEmail(dto);
        verify(repo, times(1)).existsByPlaceIdAndUserEmail(anyLong(), anyString());
        verify(repo, times(1)).deleteByPlaceIdAndUserEmail(anyLong(), anyString());
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void updateTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        dto.setName("a");
        dto.setPlaceId(1L);
        dto.setUserEmail("a@");
        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        Mockito.when(userService.existsByEmail(any())).thenReturn(true);
        Mockito.when(placeService.existsById(any())).thenReturn(true);
        Mockito.when(repo.existsByPlaceIdAndUserEmail(any(), any())).thenReturn(true);
        Mockito.when(repo.save(any(FavoritePlace.class))).thenReturn(fp);
        Mockito.when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class)))
            .thenReturn(fp);
        Mockito.when(modelMapper.map(any(FavoritePlace.class), eq(FavoritePlaceDto.class)))
            .thenReturn(dto);
        Mockito.when(userService.findIdByEmail(anyString())).thenReturn((long) 3L);
        FavoritePlace favoritePlace2 = new FavoritePlace();
        favoritePlace2.setId(22L);
        Mockito.when(repo.findByUserAndPlace(any(), any())).thenReturn(favoritePlace2);
        FavoritePlaceDto dto2 = favoritePlaceService.update(dto);
        Assert.assertEquals(dto, dto2);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = BadIdException.class)
    public void updateBadUserEmailTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();

        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        Mockito.when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class)))
            .thenReturn(fp);
        Mockito.when(userService.existsByEmail(any())).thenReturn(false);
        favoritePlaceService.update(dto);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = BadIdException.class)
    public void updateBadPlaceIdTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();

        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        Mockito.when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class)))
            .thenReturn(fp);
        Mockito.when(userService.existsByEmail(any())).thenReturn(true);
        Mockito.when(placeService.existsById(any())).thenReturn(false);

        favoritePlaceService.update(dto);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = BadIdException.class)
    public void updateFavoritePlaceNotExistTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();

        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        Mockito.when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class)))
            .thenReturn(fp);
        Mockito.when(userService.existsByEmail(any())).thenReturn(true);
        Mockito.when(placeService.existsById(any())).thenReturn(true);
        Mockito.when(repo.existsByPlaceIdAndUserEmail(any(), any())).thenReturn(false);

        favoritePlaceService.update(dto);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void findAllTest() {
        List<FavoritePlace> favoritePlaces = new ArrayList<>();
        FavoritePlace favoritePlace;
        List<FavoritePlaceDto> favoritePlaceDtos = new ArrayList<>();
        FavoritePlaceDto favoritePlaceDto;
        favoritePlace = new FavoritePlace();
        favoritePlaceDto = new FavoritePlaceDto();
        favoritePlaceDto.setName("a");
        favoritePlaceDto.setUserEmail("email");
        favoritePlaceDto.setPlaceId((long) 1);
        for (long i = 0; i < 5; i++) {
            favoritePlaces.add(favoritePlace);
            favoritePlaceDtos.add(favoritePlaceDto);
        }
        Mockito.when(userService.existsByEmail(any())).thenReturn(true);
        Mockito.when(repo.findAllByUserEmail(anyString())).thenReturn(favoritePlaces);
        Mockito.when(modelMapper.map(any(FavoritePlace.class), eq(FavoritePlaceDto.class)))
            .thenReturn(favoritePlaceDto);
        Assert.assertEquals(favoritePlaceDtos, favoritePlaceService.findAllByUserEmail("aas"));
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = NotFoundException.class)
    public void findAllWhenUserNotExistTest() {
        Mockito.when(userService.existsByEmail(any())).thenReturn(false);
        favoritePlaceService.findAllByUserEmail("aas");
    }
}
