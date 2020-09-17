package greencity.service.impl;

import greencity.dto.goal.BulkCustomGoalDto;
import greencity.dto.goal.BulkSaveCustomGoalDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.goal.CustomGoalSaveRequestDto;
import greencity.entity.CustomGoal;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.exceptions.CustomGoalNotSavedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CustomGoalRepo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;

@ExtendWith(MockitoExtension.class)
public class CustomGoalServiceImplTest {

    @Mock
    private CustomGoalRepo customGoalRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomGoalServiceImpl customGoalService;

    private User user =
        User.builder()
            .id(1L)
            .name("Test Testing")
            .email("test@gmail.com")
            .role(ROLE.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .customGoals(new ArrayList<>())
            .build();

    @Test
    public void saveEmptyBulkSaveCustomGoalDtoTest() {
        List<CustomGoalResponseDto> saveResult = customGoalService.save(
            new BulkSaveCustomGoalDto(Collections.emptyList()),
            user
        );
        assertTrue(saveResult.isEmpty());
        assertTrue(user.getCustomGoals().isEmpty());
    }

    @Test
    public void saveNonExistentBulkSaveCustomGoalDtoTest() {
        CustomGoalSaveRequestDto customGoalDtoToSave = new CustomGoalSaveRequestDto("foo");
        CustomGoal customGoal = new CustomGoal(1L, customGoalDtoToSave.getText(), null, null);
        when(modelMapper.map(customGoalDtoToSave, CustomGoal.class)).thenReturn(customGoal);
        when(modelMapper.map(customGoal, CustomGoalResponseDto.class)).thenReturn(new CustomGoalResponseDto(1L, "bar"));
        List<CustomGoalResponseDto> saveResult = customGoalService.save(
            new BulkSaveCustomGoalDto(Collections.singletonList(customGoalDtoToSave)),
            user
        );
        assertEquals(user.getCustomGoals().get(0), customGoal);
        assertEquals("bar", saveResult.get(0).getText());
    }

    @Test
    public void saveDuplicatedBulkSaveCustomGoalDtoTest() {
        CustomGoalSaveRequestDto customGoalDtoToSave = new CustomGoalSaveRequestDto("foo");
        CustomGoal customGoal = new CustomGoal(1L, customGoalDtoToSave.getText(), null, null);
        user.setCustomGoals(Collections.singletonList(customGoal));
        when(modelMapper.map(customGoalDtoToSave, CustomGoal.class)).thenReturn(customGoal);
        Assertions
            .assertThrows(CustomGoalNotSavedException.class,
                () -> customGoalService.save(
                    new BulkSaveCustomGoalDto(Collections.singletonList(customGoalDtoToSave)),
                    user
                ));
    }

    @Test
    public void findAllTest() {
        CustomGoal customGoal = new CustomGoal(1L, "foo", null, null);
        when(customGoalRepo.findAll()).thenReturn(Collections.singletonList(customGoal));
        when(modelMapper.map(customGoal, CustomGoalResponseDto.class))
            .thenReturn(new CustomGoalResponseDto(customGoal.getId(), customGoal.getText()));
        List<CustomGoalResponseDto> findAllResult = customGoalService.findAll();
        assertEquals("foo", findAllResult.get(0).getText());
        assertEquals(1L, (long) findAllResult.get(0).getId());
    }

    @Test
    public void findByNullIdTest() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> customGoalService.findById(null));
    }

    @Test
    public void findByIdTest() {
        CustomGoal customGoal = new CustomGoal(1L, "foo", null, null);
        when(customGoalRepo.findById(anyLong())).thenReturn(java.util.Optional.of(customGoal));
        when(modelMapper.map(customGoal, CustomGoalResponseDto.class))
            .thenReturn(new CustomGoalResponseDto(customGoal.getId(), customGoal.getText()));
        CustomGoalResponseDto findByIdResult = customGoalService.findById(1L);
        assertEquals("foo", findByIdResult.getText());
        assertEquals(1L, (long) findByIdResult.getId());
    }

    @Test
    public void updateEmptyInputBulkTest() {
        List<CustomGoalResponseDto> updateBulkResult =
            customGoalService.updateBulk(new BulkCustomGoalDto(Collections.emptyList()));
        assertTrue(updateBulkResult.isEmpty());
    }

    @Test
    public void updateNonDuplicatedBulkIdTest() {
        CustomGoalResponseDto customGoalResponseDto = new CustomGoalResponseDto(1L, "foo");
        CustomGoal customGoal =
            new CustomGoal(customGoalResponseDto.getId(), customGoalResponseDto.getText(), user, null);
        when(customGoalRepo.findById(customGoalResponseDto.getId())).thenReturn(Optional.of(customGoal));
        when(modelMapper.map(customGoal, CustomGoalResponseDto.class)).thenReturn(customGoalResponseDto);
        List<CustomGoalResponseDto> updateBulkResult =
            customGoalService.updateBulk(new BulkCustomGoalDto(Collections.singletonList(customGoalResponseDto)));
        assertEquals(updateBulkResult.get(0).getId(), customGoalResponseDto.getId());
        assertEquals(updateBulkResult.get(0).getText(), customGoalResponseDto.getText());
    }

    @Test
    public void updateDuplicateBulkIdTest() {
        CustomGoalResponseDto customGoalResponseDto = new CustomGoalResponseDto(1L, "foo");
        CustomGoal customGoal =
            new CustomGoal(customGoalResponseDto.getId(), customGoalResponseDto.getText(), user, null);
        user.setCustomGoals(Collections.singletonList(customGoal));
        when(customGoalRepo.findById(customGoalResponseDto.getId())).thenReturn(Optional.of(customGoal));
        Assertions
            .assertThrows(CustomGoalNotSavedException.class,
                () -> customGoalService
                    .updateBulk(new BulkCustomGoalDto(Collections.singletonList(customGoalResponseDto))));
    }

    @Test
    public void updateNonExistentCustomGoalTest() {
        CustomGoalResponseDto customGoalResponseDto = new CustomGoalResponseDto(1L, "foo");
        when(customGoalRepo.findById(customGoalResponseDto.getId())).thenReturn(Optional.empty());
        Assertions
            .assertThrows(NotFoundException.class,
                () -> customGoalService
                    .updateBulk(new BulkCustomGoalDto(Collections.singletonList(customGoalResponseDto))));
    }

    @Test
    public void findAllByUserWithNullIdTest() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> customGoalService.findAllByUser(null));
    }

    @Test
    public void findAllByUserWithNonExistentIdTest() {
        when(customGoalRepo.findAllByUserId(user.getId())).thenReturn(Collections.emptyList());
        Assertions
            .assertThrows(NotFoundException.class,
                () -> customGoalService.findAllByUser(user.getId()));
    }

    @Test
    public void findAllByUserWithExistentIdTest() {
        CustomGoal customGoal = new CustomGoal(1L, "foo", user, null);
        CustomGoalResponseDto customGoalResponseDto =
            new CustomGoalResponseDto(customGoal.getId(), customGoal.getText());
        when(customGoalRepo.findAllByUserId(user.getId())).thenReturn(Collections.singletonList(customGoal));
        when(modelMapper.map(customGoal, CustomGoalResponseDto.class))
            .thenReturn(customGoalResponseDto);
        List<CustomGoalResponseDto> findAllByUserResult = customGoalService.findAllByUser(user.getId());
        assertEquals(findAllByUserResult.get(0).getId(), customGoalResponseDto.getId());
        assertEquals(findAllByUserResult.get(0).getText(), customGoalResponseDto.getText());
    }

    @Test
    public void bulkDeleteWithNonExistentIdTest() {
        doThrow(new EmptyResultDataAccessException(1)).when(customGoalRepo).deleteById(1L);
        Assertions
            .assertThrows(NotFoundException.class,
                () -> customGoalService.bulkDelete("1"));
    }

    @Test
    public void bulkDeleteWithExistentIdTest() {
        doNothing().when(customGoalRepo).deleteById(anyLong());
        List<Long> bulkDeleteResult = customGoalService.bulkDelete("1,2,3");
        ArrayList<Long> expectedResult = new ArrayList<>();
        expectedResult.add(1L);
        expectedResult.add(2L);
        expectedResult.add(3L);
        assertEquals(expectedResult, bulkDeleteResult);
    }
}
