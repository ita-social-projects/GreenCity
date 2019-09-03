package greencity.service.impl;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import greencity.GreenCityApplication;
import greencity.constant.ErrorMessage;
import greencity.dto.favoriteplace.FavoritePlaceShowDto;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.exception.BadEmailException;
import greencity.exception.BadIdAndEmailException;
import greencity.exception.BadIdException;
import greencity.exception.NotFoundException;
import greencity.mapping.FavoritePlaceDtoMapper;
import greencity.repository.FavoritePlaceRepo;
import greencity.service.PlaceService;
import greencity.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

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
    @Mock
    private ModelMapper modelMapper;
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
        when(userService.findIdByEmail(any())).thenReturn(1L);
        when(placeService.existsById(any())).thenReturn(true);
        when(repo.existsByPlaceIdAndUserEmail(any(), any())).thenReturn(false);
        when(repo.save(any(FavoritePlace.class))).thenReturn(fp);
        when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
        when(favoritePlaceDtoMapper.convertToDto(any(FavoritePlace.class))).thenReturn(dto);
        when(userService.findIdByEmail(anyString())).thenReturn((long) 3L);
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
        when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
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
        when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
        when(placeService.existsById(any())).thenReturn(false);
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
        when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
        when(placeService.existsById(any())).thenReturn(true);
        when(repo.existsByPlaceIdAndUserEmail(any(), any())).thenReturn(true);
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
        when(repo.existsByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(true);
        when(repo.deleteByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(1);
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
        when(repo.existsByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(false);
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
        when(userService.findIdByEmail(any())).thenReturn(1L);
        when(placeService.existsById(any())).thenReturn(true);
        when(repo.existsByPlaceIdAndUserEmail(any(), any())).thenReturn(true);
        when(repo.save(any(FavoritePlace.class))).thenReturn(fp);
        when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
        when(favoritePlaceDtoMapper.convertToDto(any(FavoritePlace.class))).thenReturn(dto);
        when(userService.findIdByEmail(anyString())).thenReturn((long) 3L);
        FavoritePlace favoritePlace2 = new FavoritePlace();
        favoritePlace2.setId(22L);
        when(repo.findByUserAndPlace(any(), any())).thenReturn(favoritePlace2);
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
        when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
        when(placeService.existsById(any())).thenReturn(true);
        when(repo.existsByPlaceIdAndUserEmail(any(), any())).thenReturn(true);
        when(userService.findIdByEmail(any())).thenThrow(new BadEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
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
        when(placeService.existsById(any())).thenReturn(true);
        when(repo.existsByPlaceIdAndUserEmail(any(), any())).thenReturn(false);

        favoritePlaceService.update(dto, "setEmail()");
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void findAllTest() {
        List<FavoritePlace> favoritePlaces = new ArrayList<>();
        FavoritePlace favoritePlace;
        List<FavoritePlaceShowDto> favoritePlaceShowDtos = new ArrayList<>();
        FavoritePlaceShowDto favoritePlaceShowDto;
        favoritePlace = new FavoritePlace();
        favoritePlaceShowDto = new FavoritePlaceShowDto();
        favoritePlaceShowDto.setName("a");
        favoritePlaceShowDto.setId((long) 1);
        for (long i = 0; i < 5; i++) {
            favoritePlaces.add(favoritePlace);
            favoritePlaceShowDtos.add(favoritePlaceShowDto);
        }
        when(repo.findAllByUserEmail(anyString())).thenReturn(favoritePlaces);
        when(modelMapper.map(any(FavoritePlace.class), eq(FavoritePlaceShowDto.class))).thenReturn(favoritePlaceShowDto);
        Assert.assertEquals(favoritePlaceShowDtos, favoritePlaceService.findAllByUserEmail("aas"));
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void findAllWhenNotRecords() {
        List result = new ArrayList();
        when(repo.findAllByUserEmail(anyString())).thenReturn(result);
        Assert.assertEquals(result, favoritePlaceService.findAllByUserEmail("aas"));
        favoritePlaceService.findAllByUserEmail("aas");
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void getAccessPlaceAsFavoritePlace() {
        PlaceInfoDto placeInfoDto = new PlaceInfoDto();
        placeInfoDto.setRate((byte) 2);
        placeInfoDto.setName("abc");
        FavoritePlace favoritePlace = FavoritePlace.builder().place(Place.builder().id(1L).build())
            .user(new User()).name("abc").build();
        FavoritePlace fp = new FavoritePlace();
        when(repo.findById(anyLong())).thenReturn(Optional.of(favoritePlace));
        when(modelMapper.map(any(Place.class), eq(PlaceInfoDto.class))).thenReturn(placeInfoDto);
        when(placeService.findById(anyLong())).thenReturn(new Place());
        when(placeService.averageRate(anyLong())).thenReturn((byte) 2);
        Assert.assertEquals(placeInfoDto, favoritePlaceService.getAccessPlaceAsFavoritePlace(2L));
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = NotFoundException.class)
    public void getAccessPlaceAsFavoritePlace_FavoritePlaceNotExist() {
        FavoritePlace fp = new FavoritePlace();
        when(repo.findById(anyLong())).thenThrow(new NotFoundException(anyString()));
        favoritePlaceService.getAccessPlaceAsFavoritePlace(2L);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = NotFoundException.class)
    public void getAccessPlaceAsFavoritePlace_PlaceNotExist() {
        FavoritePlace favoritePlace = FavoritePlace.builder().place(Place.builder().id(1L).build())
            .user(new User()).name("abc").build();
        FavoritePlace fp = new FavoritePlace();
        when(repo.findById(anyLong())).thenReturn(Optional.of(favoritePlace));
        when(placeService.findById(anyLong())).thenThrow(new NotFoundException("a"));
        favoritePlaceService.getAccessPlaceAsFavoritePlace(2L);
    }


}