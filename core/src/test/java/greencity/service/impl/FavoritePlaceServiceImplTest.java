package greencity.service.impl;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.constant.ErrorMessage;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.FavoritePlaceRepo;
import greencity.service.PlaceService;
import greencity.service.UserService;
import java.util.ArrayList;
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
public class FavoritePlaceServiceImplTest {
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

    @Test
    public void saveFavoritePlaceAlreadyExistTest() {
        FavoritePlaceDto dto = ModelUtils.getFavoritePlaceDto();
        FavoritePlace fp = ModelUtils.getFavoritePlace();

        Exception exception = assertThrows(
            WrongIdException.class,
            () -> {
                when(placeService.existsById(any())).thenReturn(true);
                when(repo.findByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(new FavoritePlace());
                when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class))).thenReturn(fp);
                favoritePlaceService.save(dto, fp.getUser().getEmail());
            });
        String expectedMessage =
            "Favorite place already exist for this placeId: " + dto.getPlaceId() + " and user with email: " +
                fp.getUser().getEmail();
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void saveBadUserEmailTest() {
        FavoritePlaceDto dto = ModelUtils.getFavoritePlaceDto();
        FavoritePlace fp = ModelUtils.getFavoritePlace();
        String userEmail = "email";

        Exception exception = assertThrows(
            WrongIdException.class,
            () -> {
                when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class))).thenReturn(fp);
                favoritePlaceService.save(dto, userEmail);
            });
        String expectedMessage = ErrorMessage.PLACE_NOT_FOUND_BY_ID;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void saveBadPlaceIdTest() {
        FavoritePlaceDto dto = ModelUtils.getFavoritePlaceDto();
        FavoritePlace fp = ModelUtils.getFavoritePlace();
        dto.setPlaceId(1L);

        Exception exception = assertThrows(
            WrongIdException.class,
            () -> {
                when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class))).thenReturn(fp);
                when(placeService.existsById(any())).thenReturn(false);
                favoritePlaceService.save(dto, fp.getUser().getEmail());
            });
        String expectedMessage = ErrorMessage.PLACE_NOT_FOUND_BY_ID;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void saveTest() {
        FavoritePlaceDto dto = ModelUtils.getFavoritePlaceDto();
        FavoritePlace fp = ModelUtils.getFavoritePlace();
        when(modelMapper.map(any(FavoritePlace.class), eq(FavoritePlaceDto.class))).thenReturn(dto);
        when(modelMapper.map(any(FavoritePlaceDto.class), eq(FavoritePlace.class))).thenReturn(fp);
        when(placeService.existsById(any())).thenReturn(true);
        when(repo.findByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(null);
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        FavoritePlaceDto dto2 = favoritePlaceService.save(dto, fp.getUser().getEmail());

        verify(userService, times(1)).findIdByEmail(fp.getUser().getEmail());
        verify(placeService, times(1)).existsById(any());
        verify(repo, times(1)).findByPlaceIdAndUserEmail(anyLong(), anyString());
        verify(repo, times(1)).save(any(FavoritePlace.class));
        verify(modelMapper, times(1)).map(any(FavoritePlaceDto.class), eq(FavoritePlace.class));
        verify(modelMapper, times(1)).map(any(FavoritePlace.class), eq(FavoritePlaceDto.class));
        verify(userService, times(1)).findIdByEmail(anyString());
        assertEquals(dto, dto2);
    }

    @Test
    public void deleteByIdAndUserEmail() {
        FavoritePlace fp = ModelUtils.getFavoritePlace();
        when(repo.findByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(fp);
        assertEquals(fp.getId(), favoritePlaceService.deleteByUserEmailAndPlaceId(fp.getId(), fp.getUser().getEmail()));
        verify(repo, times(1)).findByPlaceIdAndUserEmail(anyLong(), anyString());
        verify(repo, times(1)).delete(any());
    }

    @Test
    public void deleteByIdAndUserEmail_FavoritePlaceNotExist() {
        FavoritePlace fp = ModelUtils.getFavoritePlace();
        Exception exception = assertThrows(
            WrongIdException.class,
            () -> {
                when(repo.findByPlaceIdAndUserEmail(anyLong(), anyString())).thenReturn(null);
                favoritePlaceService.deleteByUserEmailAndPlaceId(fp.getId(), fp.getUser().getEmail());
                verify(repo, times(1)).findByPlaceIdAndUserEmail(anyLong(), anyString());
                verify(repo, times(1)).delete(any());
            });
        String expectedMessage = ErrorMessage.FAVORITE_PLACE_NOT_FOUND;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void updateTest() {
        FavoritePlaceDto dto = ModelUtils.getFavoritePlaceDto();
        FavoritePlace fp = ModelUtils.getFavoritePlace();
        when(repo.findByPlaceIdAndUserEmail(any(), any())).thenReturn(fp);
        when(repo.save(any(FavoritePlace.class))).thenReturn(fp);
        favoritePlaceService.update(dto, fp.getUser().getEmail());
        assertEquals(dto, dto);
    }

    @Test
    public void updateFavoritePlaceNotExistTest() {
        FavoritePlaceDto dto = ModelUtils.getFavoritePlaceDto();
        FavoritePlace fp = ModelUtils.getFavoritePlace();
        Exception exception = assertThrows(
            WrongIdException.class,
            () -> {
                when(repo.findByPlaceIdAndUserEmail(any(), any())).thenReturn(null);
                favoritePlaceService.update(dto, fp.getUser().getEmail());
            });
        String expectedMessage = ErrorMessage.FAVORITE_PLACE_NOT_FOUND;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void findAllTest() {
        List<FavoritePlace> favoritePlaces = new ArrayList<>();
        List<FavoritePlaceDto> favoritePlaceDtos = new ArrayList<>();
        FavoritePlaceDto dto = ModelUtils.getFavoritePlaceDto();
        FavoritePlace fp = ModelUtils.getFavoritePlace();
        for (long i = 0; i < 5; i++) {
            favoritePlaces.add(fp);
            favoritePlaceDtos.add(dto);
        }
        when(repo.findAllByUserEmail(anyString())).thenReturn(favoritePlaces);
        when(modelMapper.map(any(FavoritePlace.class), eq(FavoritePlaceDto.class))).thenReturn(dto);
        assertEquals(favoritePlaceDtos, favoritePlaceService.findAllByUserEmail(TestConst.EMAIL));
    }

    @Test
    public void findAllWhenNotRecords() {
        List<FavoritePlace> result = new ArrayList<>();
        when(repo.findAllByUserEmail(anyString())).thenReturn(result);
        assertEquals(result, favoritePlaceService.findAllByUserEmail(TestConst.EMAIL));
    }

    @Test
    public void getFavoritePlaceInfo() {
        PlaceInfoDto placeInfoDto = new PlaceInfoDto();
        placeInfoDto.setRate(2.0);
        placeInfoDto.setName("name");
        FavoritePlace fp = ModelUtils.getFavoritePlace();
        when(repo.findByPlaceId(anyLong())).thenReturn(fp);
        when(modelMapper.map(any(Place.class), eq(PlaceInfoDto.class))).thenReturn(placeInfoDto);
        when(placeService.findById(anyLong())).thenReturn(new Place());
        when(placeService.averageRate(anyLong())).thenReturn(2.0);
        assertEquals(placeInfoDto, favoritePlaceService.getInfoFavoritePlace(fp.getId()));
    }

    @Test
    public void getFavoritePlaceInfo_FavoritePlaceNotExist() {
        Exception exception = assertThrows(
            WrongIdException.class,
            () -> favoritePlaceService.getInfoFavoritePlace(2L));
        String expectedMessage = ErrorMessage.FAVORITE_PLACE_NOT_FOUND;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getFavoritePlaceInfo_PlaceNotExist() {
        Exception exception = assertThrows(
            NotFoundException.class,
            () -> {
                when(repo.findByPlaceId(anyLong()))
                    .thenThrow(new NotFoundException("The place does not exist by this id: "));
                favoritePlaceService.getInfoFavoritePlace(2L);
            });
        String expectedMessage = ErrorMessage.PLACE_NOT_FOUND_BY_ID;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}