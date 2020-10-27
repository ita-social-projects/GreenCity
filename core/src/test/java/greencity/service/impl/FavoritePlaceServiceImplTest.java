package greencity.service.impl;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.constant.ErrorMessage;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.FavoritePlaceRepo;
import greencity.service.PlaceService;
import greencity.service.UserService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class FavoritePlaceServiceImplTest {
    @Mock
    private FavoritePlaceRepo repo;
    @Mock
    private UserService userService;
    @Mock
    private PlaceService placeService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private FavoritePlaceServiceImpl favoritePlaceService;

    private FavoritePlaceDto dto = ModelUtils.getFavoritePlaceDto();
    private FavoritePlace fp = ModelUtils.getFavoritePlace();
    private String userEmail = fp.getUser().getEmail();
    private Long favoritePlaceId = fp.getId();

    @Test
    void saveFavoritePlaceAlreadyExistTest() {
        when(placeService.existsById(any())).thenReturn(true);
        when(repo.findByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(new FavoritePlace());
        when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class))).thenReturn(fp);

        Exception exception = assertThrows(
            WrongIdException.class,
            () -> favoritePlaceService.save(dto, userEmail));

        String expectedMessage =
            "Favorite place already exist for this placeId: " + dto.getPlaceId() + " and user with email: " +
                userEmail;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void saveBadUserEmailTest() {
        String userEmail = "email";

        when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class))).thenReturn(fp);

        Exception exception = assertThrows(
            WrongIdException.class,
            () -> favoritePlaceService.save(dto, userEmail));

        String expectedMessage = ErrorMessage.PLACE_NOT_FOUND_BY_ID;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void saveBadPlaceIdTest() {
        dto.setPlaceId(1L);
        when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class))).thenReturn(fp);
        when(placeService.existsById(any())).thenReturn(false);

        Exception exception = assertThrows(
            WrongIdException.class,
            () -> {
                favoritePlaceService.save(dto, userEmail);
            });
        String expectedMessage = ErrorMessage.PLACE_NOT_FOUND_BY_ID;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void saveTest() {
        when(modelMapper.map(any(FavoritePlace.class), eq(FavoritePlaceDto.class))).thenReturn(dto);
        when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class))).thenReturn(fp);
        when(placeService.existsById(any())).thenReturn(true);
        when(repo.findByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(null);
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        FavoritePlaceDto actual = favoritePlaceService.save(dto, userEmail);

        verify(userService, times(1)).findIdByEmail(userEmail);
        verify(placeService, times(1)).existsById(any());
        verify(repo, times(1)).findByPlaceIdAndUserEmail(anyLong(), anyString());
        verify(repo, times(1)).save(any(FavoritePlace.class));
        verify(modelMapper, times(1)).map(any(FavoritePlaceDto.class), eq(FavoritePlace.class));
        verify(modelMapper, times(1)).map(any(FavoritePlace.class), eq(FavoritePlaceDto.class));
        verify(userService, times(1)).findIdByEmail(anyString());

        assertEquals(dto, actual);
    }

    @Test
    void deleteByIdAndUserEmail() {
        when(repo.findByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(fp);

        assertEquals(favoritePlaceId, favoritePlaceService.deleteByUserEmailAndPlaceId(favoritePlaceId, userEmail));

        verify(repo, times(1)).findByPlaceIdAndUserEmail(anyLong(), anyString());
        verify(repo, times(1)).delete(any());
    }

    @Test
    void deleteByIdAndUserEmail_FavoritePlaceNotExist() {
        when(repo.findByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(null);

        Exception exception = assertThrows(
            WrongIdException.class,
            () -> favoritePlaceService.deleteByUserEmailAndPlaceId(favoritePlaceId, userEmail));
        String expectedMessage = ErrorMessage.FAVORITE_PLACE_NOT_FOUND;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void updateTest() {
        when(repo.findByPlaceIdAndUserEmail(any(), any())).thenReturn(fp);
        when(repo.save(any(FavoritePlace.class))).thenReturn(fp);
        when(modelMapper.map(fp, FavoritePlaceDto.class)).thenReturn(dto);

        FavoritePlaceDto actual = favoritePlaceService.update(dto, userEmail);

        assertEquals(dto, actual);
    }

    @Test
    void updateFavoritePlaceNotExistTest() {
        when(repo.findByPlaceIdAndUserEmail(any(), any())).thenReturn(null);

        Exception exception = assertThrows(
            WrongIdException.class,
            () -> favoritePlaceService.update(dto, userEmail));

        String expectedMessage = ErrorMessage.FAVORITE_PLACE_NOT_FOUND;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void findAllTest() {
        List<FavoritePlace> favoritePlaces = new ArrayList<>();
        List<FavoritePlaceDto> favoritePlaceDtos = new ArrayList<>();
        for (long i = 0; i < 5; i++) {
            favoritePlaces.add(fp);
            favoritePlaceDtos.add(dto);
        }

        when(repo.findAllByUserEmail(anyString())).thenReturn(favoritePlaces);
        when(modelMapper.map(any(FavoritePlace.class), eq(FavoritePlaceDto.class))).thenReturn(dto);

        assertEquals(favoritePlaceDtos, favoritePlaceService.findAllByUserEmail(TestConst.EMAIL));
    }

    @Test
    void findAllWhenNotRecords() {
        when(repo.findAllByUserEmail(anyString())).thenReturn(Collections.emptyList());

        boolean isNoRecords = favoritePlaceService.findAllByUserEmail(TestConst.EMAIL).isEmpty();

        assertTrue(isNoRecords);
    }

    @Test
    void getFavoritePlaceInfo() {
        PlaceInfoDto placeInfoDto = new PlaceInfoDto();
        placeInfoDto.setRate(2.0);
        placeInfoDto.setName("name");

        when(repo.findByPlaceId(anyLong())).thenReturn(fp);
        when(modelMapper.map(any(Place.class), eq(PlaceInfoDto.class))).thenReturn(placeInfoDto);
        when(placeService.findById(anyLong())).thenReturn(new Place());
        when(placeService.averageRate(anyLong())).thenReturn(2.0);

        assertEquals(placeInfoDto, favoritePlaceService.getInfoFavoritePlace(favoritePlaceId));
    }

    @Test
    void getFavoritePlaceInfo_FavoritePlaceNotExist() {
        Exception exception = assertThrows(
            WrongIdException.class,
            () -> favoritePlaceService.getInfoFavoritePlace(2L));
        String expectedMessage = ErrorMessage.FAVORITE_PLACE_NOT_FOUND;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getFavoritePlaceInfo_PlaceNotExist() {
        when(repo.findByPlaceId(2L)).thenReturn(null);

        Exception exception = assertThrows(WrongIdException.class,
            () -> favoritePlaceService.getInfoFavoritePlace(2L));

        String expectedMessage = ErrorMessage.FAVORITE_PLACE_NOT_FOUND;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}