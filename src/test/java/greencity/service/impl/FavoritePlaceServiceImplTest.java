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
import greencity.exception.BadIdOrEmailException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
        when(repo.save(any(FavoritePlace.class))).thenReturn(fp);
        when(favoritePlaceDtoMapper.convertToEntity(any(FavoritePlaceDto.class))).thenReturn(fp);
        when(favoritePlaceDtoMapper.convertToDto(any(FavoritePlace.class))).thenReturn(dto);
        when(userService.findIdByEmail(anyString())).thenReturn((long) 3L);
        FavoritePlaceDto dto2 = favoritePlaceService.save(dto, userEmail);

        verify(userService, times(1)).findIdByEmail(fp.getUser().getEmail());
        verify(placeService, times(1)).existsById(any());
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



    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void deleteByIdAndUserEmail() {
        FavoritePlace fp = new FavoritePlace();
        fp.setId(1L);
        String userEmail = "email";
        when(repo.findByIdAndUserEmail(anyLong(), anyString())).thenReturn(fp);
        Assert.assertEquals(fp.getId(), favoritePlaceService.deleteByIdAndUserEmail(fp.getId(), userEmail));
        verify(repo, times(1)).findByIdAndUserEmail(anyLong(), anyString());
        verify(repo, times(1)).delete(any());
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = BadIdException.class)
    public void deleteByIdAndUserEmail_FavoritePlaceNotExist() {
        Long id = 9L;
        String userEmail = "email";
        when(repo.findByIdAndUserEmail(anyLong(), anyString())).thenReturn(null);
        favoritePlaceService.deleteByIdAndUserEmail(id, userEmail);
        verify(repo, times(1)).findByIdAndUserEmail(anyLong(), anyString());
        verify(repo, times(1)).delete(any());
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void updateTest() {
        FavoritePlaceShowDto dto = new FavoritePlaceShowDto();
        dto.setName("a");
        dto.setId(1L);
        String userEmail = "email";
        FavoritePlace fp = new FavoritePlace();
        fp.setName("a");
        fp.setUser(new User());
        fp.getUser().setEmail("setEmail()");
        fp.setId(2L);
        when(repo.findByIdAndUserEmail(any(), any())).thenReturn(fp);
        when(repo.save(any(FavoritePlace.class))).thenReturn(fp);
        when(modelMapper.map(any(FavoritePlace.class), eq(FavoritePlaceShowDto.class))).thenReturn(dto);
        favoritePlaceService.update(dto, userEmail);
        Assert.assertEquals(dto, dto);
    }


    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = BadIdException.class)
    public void updateFavoritePlaceNotExistTest() {
        FavoritePlaceShowDto dto = new FavoritePlaceShowDto();
        dto.setId(8L);
        when(repo.findByIdAndUserEmail(any(), any())).thenReturn(null);
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
    public void getFavoritePlaceInfo() {
        PlaceInfoDto placeInfoDto = new PlaceInfoDto();
        placeInfoDto.setRate(2.0);
        placeInfoDto.setName("abc");
        FavoritePlace favoritePlace = FavoritePlace.builder().place(Place.builder().id(1L).build())
            .user(new User()).name("abc").build();
        FavoritePlace fp = new FavoritePlace();
        when(repo.findById(anyLong())).thenReturn(Optional.of(favoritePlace));
        when(modelMapper.map(any(Place.class), eq(PlaceInfoDto.class))).thenReturn(placeInfoDto);
        when(placeService.findById(anyLong())).thenReturn(new Place());
        when(placeService.averageRate(anyLong())).thenReturn(2.0);
        Assert.assertEquals(placeInfoDto, favoritePlaceService.getInfoFavoritePlace(2L));
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = BadIdException.class)
    public void getFavoritePlaceInfo_FavoritePlaceNotExist() {
        FavoritePlace fp = new FavoritePlace();
        when(repo.findById(anyLong())).thenThrow(new BadIdException(anyString()));
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
        when(repo.findById(anyLong())).thenReturn(Optional.of(favoritePlace));
        when(placeService.findById(anyLong())).thenThrow(new NotFoundException("a"));
        favoritePlaceService.getInfoFavoritePlace(2L);
    }


}