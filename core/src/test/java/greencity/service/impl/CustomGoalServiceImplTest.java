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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
            .firstName("test")
            .lastName("test")
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

    @Test(expected = CustomGoalNotSavedException.class)
    public void saveDuplicatedBulkSaveCustomGoalDtoTest() {
        CustomGoalSaveRequestDto customGoalDtoToSave = new CustomGoalSaveRequestDto("foo");
        CustomGoal customGoal = new CustomGoal(1L, customGoalDtoToSave.getText(), null, null);
        user.setCustomGoals(Collections.singletonList(customGoal));
        when(modelMapper.map(customGoalDtoToSave, CustomGoal.class)).thenReturn(customGoal);
        customGoalService.save(
            new BulkSaveCustomGoalDto(Collections.singletonList(customGoalDtoToSave)),
            user
        );
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

    @Test(expected = NotFoundException.class)
    public void findByNullIdTest() {
        customGoalService.findById(null);
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

    @Test(expected = CustomGoalNotSavedException.class)
    public void updateDuplicateBulkIdTest() {
        CustomGoalResponseDto customGoalResponseDto = new CustomGoalResponseDto(1L, "foo");
        CustomGoal customGoal =
            new CustomGoal(customGoalResponseDto.getId(), customGoalResponseDto.getText(), user, null);
        user.setCustomGoals(Collections.singletonList(customGoal));
        when(customGoalRepo.findById(customGoalResponseDto.getId())).thenReturn(Optional.of(customGoal));
        customGoalService.updateBulk(new BulkCustomGoalDto(Collections.singletonList(customGoalResponseDto)));
    }

    @Test(expected = NotFoundException.class)
    public void updateNonExistentCustomGoalTest() {
        CustomGoalResponseDto customGoalResponseDto = new CustomGoalResponseDto(1L, "foo");
        when(customGoalRepo.findById(customGoalResponseDto.getId())).thenReturn(Optional.empty());
        customGoalService.updateBulk(new BulkCustomGoalDto(Collections.singletonList(customGoalResponseDto)));
    }

    @Test(expected = NotFoundException.class)
    public void findAllByUserWithNullIdTest() {
        customGoalService.findAllByUser(null);
    }

    @Test(expected = NotFoundException.class)
    public void findAllByUserWithNonExistentIdTest() {
        when(customGoalRepo.findAllByUserId(user.getId())).thenReturn(Collections.emptyList());
        customGoalService.findAllByUser(user.getId());
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

    @Test(expected = NotFoundException.class)
    public void bulkDeleteWithNonExistentIdTest() {
        doThrow(new EmptyResultDataAccessException(1)).when(customGoalRepo).deleteById(1L);
        customGoalService.bulkDelete("1");
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
