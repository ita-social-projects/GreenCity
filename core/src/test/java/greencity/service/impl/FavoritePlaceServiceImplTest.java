package greencity.service.impl;

import greencity.GreenCityApplication;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongIdException;
import greencity.mapping.FavoritePlaceDtoMapper;
import greencity.repository.FavoritePlaceRepo;
import greencity.service.PlaceService;
import greencity.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

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
    @Test(expected = WrongIdException.class)
    public void saveFavoritePlaceAlreadyExistTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        String userEmail = "email";
        dto.setName("a");
        dto.setFavoritePlaceId(1L);
        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        when(placeService.existsById(any())).thenReturn(true);
        when(repo.findByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(new FavoritePlace());
        when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
        favoritePlaceService.save(dto, userEmail);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = WrongIdException.class)
    public void saveBadUserEmailTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        String userEmail = "email";
        dto.setName("a");
        dto.setFavoritePlaceId(1L);
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
    @Test(expected = WrongIdException.class)
    public void saveBadPlaceIdTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        String userEmail = "email";
        dto.setName("a");
        dto.setFavoritePlaceId(1L);
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

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void saveTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        String userEmail = "email";
        dto.setName("a");
        dto.setFavoritePlaceId(1L);
        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setPlace(new Place());
        fp.getPlace().setId(2L);
        when(favoritePlaceDtoMapper.convertToDto(any(FavoritePlace.class))).thenReturn(dto);
        when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
        when(placeService.existsById(any())).thenReturn(true);
        when(repo.findByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(null);
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        FavoritePlaceDto dto2 = favoritePlaceService.save(dto, userEmail);

        verify(userService, times(1)).findIdByEmail(fp.getUser().getEmail());
        verify(placeService, times(1)).existsById(any());
        verify(repo, times(1)).findByPlaceIdAndUserEmail(anyLong(), anyString());
        verify(repo, times(1)).save(any(FavoritePlace.class));
        verify(favoritePlaceDtoMapper, times(1)).convertToEntity(any(FavoritePlaceDto.class));
        verify(favoritePlaceDtoMapper, times(1)).convertToDto(any(FavoritePlace.class));
        verify(userService, times(1)).findIdByEmail(anyString());
        Assert.assertEquals(dto, dto2);
    }


    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void deleteByIdAndUserEmail() {
        FavoritePlace fp = new FavoritePlace();
        fp.setId(1L);
        String userEmail = "email";
        when(repo.findByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(fp);
        Assert.assertEquals(fp.getId(), favoritePlaceService.deleteByUserEmailAndPlaceId(fp.getId(), userEmail));
        verify(repo, times(1)).findByPlaceIdAndUserEmail(anyLong(), anyString());
        verify(repo, times(1)).delete(any());
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = WrongIdException.class)
    public void deleteByIdAndUserEmail_FavoritePlaceNotExist() {
        Long id = 9L;
        String userEmail = "email";
        when(repo.findByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(null);
        favoritePlaceService.deleteByUserEmailAndPlaceId(id, userEmail);
        verify(repo, times(1)).findByPlaceIdAndUserEmail(anyLong(), anyString());
        verify(repo, times(1)).delete(any());
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void updateTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        dto.setName("a");
        dto.setFavoritePlaceId(1L);
        String userEmail = "email";
        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setId(2L);
        when(repo.findByPlaceIdAndUserEmail(any(), any())).thenReturn(fp);
        when(repo.save(any(FavoritePlace.class))).thenReturn(fp);
        favoritePlaceService.update(dto, userEmail);
        Assert.assertEquals(dto, dto);
    }


    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = WrongIdException.class)
    public void updateFavoritePlaceNotExistTest() {
        FavoritePlaceDto dto = new FavoritePlaceDto();
        dto.setFavoritePlaceId(8L);
        when(repo.findByPlaceIdAndUserEmail(any(), any())).thenReturn(null);
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
        favoritePlaceDto.setFavoritePlaceId((long) 1);
        for (long i = 0; i < 5; i++) {
            favoritePlaces.add(favoritePlace);
            favoritePlaceDtos.add(favoritePlaceDto);
        }
        when(repo.findAllByUserEmail(anyString())).thenReturn(favoritePlaces);
        when(favoritePlaceDtoMapper.convertToDto(any(FavoritePlace.class))).thenReturn(favoritePlaceDto);
        Assert.assertEquals(favoritePlaceDtos, favoritePlaceService.findAllByUserEmail("aas"));
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
    public void getFavoritePlaceInfo() {
        PlaceInfoDto placeInfoDto = new PlaceInfoDto();
        placeInfoDto.setRate(2.0);
        placeInfoDto.setName("abc");
        FavoritePlace favoritePlace = FavoritePlace.builder().place(Place.builder().id(1L).build())
            .user(new User()).name("abc").build();
        FavoritePlace fp = new FavoritePlace();
        when(repo.findByPlaceId(anyLong())).thenReturn(favoritePlace);
        when(modelMapper.map(any(Place.class), eq(PlaceInfoDto.class))).thenReturn(placeInfoDto);
        when(placeService.findById(anyLong())).thenReturn(new Place());
        when(placeService.averageRate(anyLong())).thenReturn(2.0);
        Assert.assertEquals(placeInfoDto, favoritePlaceService.getInfoFavoritePlace(2L));
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = WrongIdException.class)
    public void getFavoritePlaceInfo_FavoritePlaceNotExist() {
        FavoritePlace fp = new FavoritePlace();
        favoritePlaceService.getInfoFavoritePlace(2L);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = NotFoundException.class)
    public void getFavoritePlaceInfo_PlaceNotExist() {
        FavoritePlace favoritePlace = FavoritePlace.builder().place(Place.builder().id(1L).build())
            .user(new User()).name("abc").build();
        FavoritePlace fp = new FavoritePlace();
        when(repo.findByPlaceId(anyLong())).thenThrow(new NotFoundException("a"));
        favoritePlaceService.getInfoFavoritePlace(2L);
    }


}