package greencity.service.impl;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import greencity.GreenCityApplication;
import greencity.constant.ErrorMessage;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.exception.BadEmailException;
import greencity.exception.BadIdAndEmailException;
import greencity.exception.BadIdException;
import greencity.exception.NotFoundException;
import greencity.mapping.FavoritePlaceDtoMapper;
import greencity.repository.FavoritePlaceRepo;
import greencity.service.FavoritePlaceService;
import greencity.service.PlaceService;
import greencity.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;


import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class FavoritePlaceServiceImplTest {
    @Mock
    private FavoritePlaceRepo repo;
    @Mock
    private UserService userService;
    @Mock
    private PlaceService placeService;
    @Mock
    private FavoritePlaceDtoMapper favoritePlaceDtoMapper;
    @InjectMocks
    private FavoritePlaceServiceImpl favoritePlaceService;

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void saveTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        String userEmail = "email";
        dto.setName("a");
        dto.setPlaceId(1L);
        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        Mockito.when(userService.findIdByEmail(any())).thenReturn(1L);
        Mockito.when(placeService.existsById(any())).thenReturn(true);
        Mockito.when(repo.existsByPlaceIdAndUserEmail(any(), any())).thenReturn(false);
        Mockito.when(repo.save(any(FavoritePlace.class))).thenReturn(fp);
        Mockito.when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
        Mockito.when(favoritePlaceDtoMapper.convertToDto(any(FavoritePlace.class))).thenReturn(dto);
        Mockito.when(userService.findIdByEmail(anyString())).thenReturn((long) 3L);
        FavoritePlaceDto dto2 = favoritePlaceService.save(dto, userEmail);

        verify(userService, times(1)).findIdByEmail(fp.getUser().getEmail());
        verify(placeService, times(1)).existsById(any());
        verify(repo, times(1)).existsByPlaceIdAndUserEmail(any(), any());
        verify(repo, times(1)).save(any(FavoritePlace.class));
        verify(favoritePlaceDtoMapper, times(1)).convertToEntity(any(FavoritePlaceDto.class));
        verify(favoritePlaceDtoMapper, times(1)).convertToDto(any(FavoritePlace.class));
        verify(userService, times(1)).findIdByEmail(anyString());
        Assert.assertEquals(dto, dto2);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = BadIdException.class)
    public void saveBadUserEmailTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        String userEmail = "email";
        dto.setName("a");
        dto.setPlaceId(1L);
        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        Mockito.when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
        favoritePlaceService.save(dto, userEmail);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = BadIdException.class)
    public void saveBadPlaceIdTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        String userEmail = "email";
        dto.setName("a");
        dto.setPlaceId(1L);
        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        Mockito.when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
        Mockito.when(placeService.existsById(any())).thenReturn(false);
        favoritePlaceService.save(dto, userEmail);
    }

    @Test(expected = BadIdAndEmailException.class)
    public void saveFavoritePlaceAlreadyExistTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        String userEmail = "email";
        dto.setName("a");
        dto.setPlaceId(1L);
        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        Mockito.when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
        Mockito.when(placeService.existsById(any())).thenReturn(true);
        Mockito.when(repo.existsByPlaceIdAndUserEmail(any(), any())).thenReturn(true);
        favoritePlaceService.save(dto, userEmail);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void deleteByPlaceIdAndUserEmail() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        dto.setPlaceId(1L);
        String userEmail = "email";
        Mockito.when(repo.existsByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(true);
         Mockito.when(repo.deleteByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(1);
        favoritePlaceService.deleteByPlaceIdAndUserEmail(dto.getPlaceId(), userEmail);
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
        String userEmail = "email";
        Mockito.when(repo.existsByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(false);
        favoritePlaceService.deleteByPlaceIdAndUserEmail(dto.getPlaceId(), userEmail);
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
        String userEmail = "email";
        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        Mockito.when(userService.findIdByEmail(any())).thenReturn(1L);
        Mockito.when(placeService.existsById(any())).thenReturn(true);
        Mockito.when(repo.existsByPlaceIdAndUserEmail(any(), any())).thenReturn(true);
        Mockito.when(repo.save(any(FavoritePlace.class))).thenReturn(fp);
        Mockito.when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
        Mockito.when(favoritePlaceDtoMapper.convertToDto(any(FavoritePlace.class))).thenReturn(dto);
        Mockito.when(userService.findIdByEmail(anyString())).thenReturn((long) 3L);
        FavoritePlace favoritePlace2 = new FavoritePlace();
        favoritePlace2.setId(22L);
        Mockito.when(repo.findByUserAndPlace(any(), any())).thenReturn(favoritePlace2);
        FavoritePlaceDto dto2 = favoritePlaceService.update(dto, userEmail);
        Assert.assertEquals(dto, dto2);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = BadEmailException.class)
    public void updateBadUserEmailTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        FavoritePlace fp = new FavoritePlace();
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        Mockito.when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
        Mockito.when(placeService.existsById(any())).thenReturn(true);
        Mockito.when(repo.existsByPlaceIdAndUserEmail(any(), any())).thenReturn(true);
        Mockito.when(userService.findIdByEmail(any())).thenThrow(new BadEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        favoritePlaceService.update(dto, "setEmail()");
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
        favoritePlaceService.update(dto, "setEmail()");
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = BadIdAndEmailException.class)
    public void updateFavoritePlaceNotExistTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();

        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        Mockito.when(placeService.existsById(any())).thenReturn(true);
        Mockito.when(repo.existsByPlaceIdAndUserEmail(any(), any())).thenReturn(false);

        favoritePlaceService.update(dto, "setEmail()");
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
        favoritePlaceDto.setPlaceId((long) 1);
        for (long i = 0; i < 5; i++) {
            favoritePlaces.add(favoritePlace);
            favoritePlaceDtos.add(favoritePlaceDto);
        }
        Mockito.when(repo.findAllByUserEmail(anyString())).thenReturn(favoritePlaces);
        Mockito.when(favoritePlaceDtoMapper.convertToDto(any(FavoritePlace.class))).thenReturn(favoritePlaceDto);
        Assert.assertEquals(favoritePlaceDtos, favoritePlaceService.findAllByUserEmail("aas"));
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void findAllWhenNotRecords() {
        List result = new ArrayList();
        Mockito.when(repo.findAllByUserEmail(anyString())).thenReturn(result);
        Assert.assertEquals(result, favoritePlaceService.findAllByUserEmail("aas"));
        favoritePlaceService.findAllByUserEmail("aas");
    }
}
