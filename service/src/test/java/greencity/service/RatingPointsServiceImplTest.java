package greencity.service;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.ratingstatistics.RatingPointsDto;
import greencity.entity.RatingPoints;
import greencity.enums.Status;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.RatingPointsRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RatingPointsServiceImplTest {

    @Mock
    private RatingPointsRepo ratingPointsRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RatingPointsServiceImpl ratingPointsService;

    private RatingPoints ratingPoints;
    private RatingPointsDto ratingPointsDto;
    private final Pageable pageable = PageRequest.of(3, 5);
    private final Page<RatingPoints> ratingPointsPage = Page.empty(pageable);

    @BeforeEach
    void setUp() {
        ratingPoints = new RatingPoints("Test Points");
        ratingPoints.setId(1L);

        ratingPointsDto = new RatingPointsDto();
        ratingPointsDto.setId(1L);
        ratingPointsDto.setName("Test Points");
        ratingPointsDto.setPoints(10);
    }

    @Test
    void getAllRatingPointsByPage_ShouldReturnPageableAdvancedDto() {
        PageableAdvancedDto<RatingPointsDto> expected = new PageableAdvancedDto<>(
            Collections.emptyList(), ratingPointsPage.getTotalElements(), 3,
            ratingPointsPage.getTotalPages(),
            ratingPointsPage.getNumber(), ratingPointsPage.hasPrevious(),
            ratingPointsPage.hasNext(),
            ratingPointsPage.isFirst(), ratingPointsPage.isLast());
        when(ratingPointsRepo.findAllByStatus(Status.ACTIVE, pageable)).thenReturn(ratingPointsPage);

        PageableAdvancedDto<RatingPointsDto> result = ratingPointsService.getAllRatingPointsByPage(pageable);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(ratingPointsRepo, times(1)).findAllByStatus(Status.ACTIVE, pageable);
    }

    @Test
    void createRatingPoints_ShouldReturnListOfRatingPointsDto() {
        when(ratingPointsRepo.save(any(RatingPoints.class))).thenReturn(ratingPoints);
        when(modelMapper.map(ratingPoints, RatingPointsDto.class)).thenReturn(ratingPointsDto);

        List<RatingPointsDto> result = ratingPointsService.createRatingPoints("Test Points");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(ratingPointsRepo, times(2)).save(any(RatingPoints.class));
    }

    @Test
    void updateRatingPoints_ShouldReturnUpdatedRatingPointsDto() {
        when(ratingPointsRepo.findById(anyLong())).thenReturn(Optional.of(ratingPoints));
        when(ratingPointsRepo.save(any(RatingPoints.class))).thenReturn(ratingPoints);
        when(modelMapper.map(ratingPoints, RatingPointsDto.class)).thenReturn(ratingPointsDto);

        RatingPointsDto result = ratingPointsService.updateRatingPoints(ratingPointsDto);

        assertNotNull(result);
        assertEquals(ratingPointsDto.getName(), result.getName());
        verify(ratingPointsRepo, times(1)).findById(anyLong());
        verify(ratingPointsRepo, times(1)).save(any(RatingPoints.class));
    }

    @Test
    void updateRatingPoints_ShouldThrowNotFoundException_WhenRatingPointsNotFound() {
        when(ratingPointsRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> ratingPointsService.updateRatingPoints(ratingPointsDto)
        );

        assertEquals(ErrorMessage.RATING_POINTS_NOT_FOUND_BY_ID + ratingPointsDto.getId(), exception.getMessage());
        verify(ratingPointsRepo, times(1)).findById(anyLong());
        verify(ratingPointsRepo, never()).save(any(RatingPoints.class));
    }

    @Test
    void deleteRatingPoints_ShouldCallUpdateStatusById() {
        doNothing().when(ratingPointsRepo).updateStatusById(1L, Status.DELETE);

        ratingPointsService.deleteRatingPoints(1L);

        verify(ratingPointsRepo, times(1)).updateStatusById(1L, Status.DELETE);
    }

    @Test
    void deleteRatingPoints_ShouldThrowNotFoundException_WhenEmptyResultDataAccessExceptionThrown() {
        doThrow(new EmptyResultDataAccessException(1))
            .when(ratingPointsRepo).updateStatusById(1L, Status.DELETE);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> ratingPointsService.deleteRatingPoints(1L));

        assertTrue(exception.getMessage().contains(ErrorMessage.RATING_POINTS_NOT_FOUND_BY_ID + 1));
        verify(ratingPointsRepo, times(1)).updateStatusById(1L, Status.DELETE);
    }

    @Test
    void getDeletedRatingPoints_ShouldReturnPageableAdvancedDto() {
        PageableAdvancedDto<RatingPointsDto> expected = new PageableAdvancedDto<>(
            Collections.emptyList(), ratingPointsPage.getTotalElements(), 3,
            ratingPointsPage.getTotalPages(),
            ratingPointsPage.getNumber(), ratingPointsPage.hasPrevious(),
            ratingPointsPage.hasNext(),
            ratingPointsPage.isFirst(), ratingPointsPage.isLast());
        when(ratingPointsRepo.findAllByStatus(Status.DELETE, pageable)).thenReturn(ratingPointsPage);

        PageableAdvancedDto<RatingPointsDto> actual = ratingPointsService.getDeletedRatingPoints(pageable);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(ratingPointsRepo, times(1)).findAllByStatus(Status.DELETE, pageable);
    }

    @Test
    void restoreDeletedRatingPoints_ShouldCallUpdateStatusById() {
        doNothing().when(ratingPointsRepo).updateStatusById(1L, Status.ACTIVE);

        ratingPointsService.restoreDeletedRatingPoints(1L);

        verify(ratingPointsRepo, times(1)).updateStatusById(1L, Status.ACTIVE);
    }

    @Test
    void restoreDeletedRatingPoints_ShouldThrowNotFoundException_WhenExceptionThrown() {
        doThrow(new RuntimeException("DB error"))
            .when(ratingPointsRepo).updateStatusById(1L, Status.ACTIVE);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> ratingPointsService.restoreDeletedRatingPoints(1L));

        assertTrue(exception.getMessage().contains(ErrorMessage.RATING_POINTS_NOT_FOUND_BY_ID + 1));
        verify(ratingPointsRepo, times(1)).updateStatusById(1L, Status.ACTIVE);
    }

    @Test
    void updateRatingPointsName_ShouldCallUpdateRatingPointsName() {
        doNothing().when(ratingPointsRepo).updateRatingPointsName(anyString(), anyString());

        ratingPointsService.updateRatingPointsName("Old Name", "New Name");

        verify(ratingPointsRepo, times(1)).updateRatingPointsName("Old Name", "New Name");
        verify(ratingPointsRepo, times(1)).updateRatingPointsName("UNDO_Old Name", "UNDO_New Name");
    }

    @Test
    void updateRatingPointsName_ShouldThrowNotFoundException_WhenExceptionThrown() {
        doThrow(new RuntimeException("DB error"))
            .when(ratingPointsRepo).updateRatingPointsName("Old Name", "New Name");

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> ratingPointsService.updateRatingPointsName("Old Name", "New Name"));

        assertTrue(exception.getMessage().contains(ErrorMessage.RATING_POINTS_NOT_FOUND_BY_NAME + "Old Name"));
        verify(ratingPointsRepo, times(1))
            .updateRatingPointsName("Old Name", "New Name");
    }
}
