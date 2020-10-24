package greencity.service;

import greencity.ModelUtils;
import greencity.dto.goal.BulkCustomGoalDto;
import greencity.dto.goal.BulkSaveCustomGoalDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.goal.CustomGoalSaveRequestDto;
import greencity.dto.user.UserVO;
import greencity.entity.CustomGoal;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.ROLE;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.CustomGoalNotSavedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CustomGoalRepo;
import greencity.service.CustomGoalServiceImpl;
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
class CustomGoalServiceImplTest {

    @Mock
    private CustomGoalRepo customGoalRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserService userService;

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
    void saveEmptyBulkSaveCustomGoalDtoTest() {
        UserVO userVO = ModelUtils.getUserVO();
        when(userService.findById(1L)).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        List<CustomGoalResponseDto> saveResult = customGoalService.save(
            new BulkSaveCustomGoalDto(Collections.emptyList()),
            1L
        );
        assertTrue(saveResult.isEmpty());
        assertTrue(user.getCustomGoals().isEmpty());
    }

    @Test
    void saveNonExistentBulkSaveCustomGoalDtoTest() {
        CustomGoalSaveRequestDto customGoalDtoToSave = new CustomGoalSaveRequestDto("foo");
        CustomGoal customGoal = new CustomGoal(1L, customGoalDtoToSave.getText(), null, null);
        UserVO userVO = ModelUtils.getUserVO();
        when(userService.findById(1L)).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(customGoalDtoToSave, CustomGoal.class)).thenReturn(customGoal);
        when(modelMapper.map(customGoal, CustomGoalResponseDto.class)).thenReturn(new CustomGoalResponseDto(1L, "bar"));
        List<CustomGoalResponseDto> saveResult = customGoalService.save(
            new BulkSaveCustomGoalDto(Collections.singletonList(customGoalDtoToSave)),
            1L
        );
        assertEquals(user.getCustomGoals().get(0), customGoal);
        assertEquals("bar", saveResult.get(0).getText());
    }

    @Test
    void saveDuplicatedBulkSaveCustomGoalDtoTest() {
        CustomGoalSaveRequestDto customGoalDtoToSave = new CustomGoalSaveRequestDto("foo");
        CustomGoal customGoal = new CustomGoal(1L, customGoalDtoToSave.getText(), null, null);
        user.setCustomGoals(Collections.singletonList(customGoal));
        UserVO userVO = ModelUtils.getUserVO();
        when(userService.findById(1L)).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(customGoalDtoToSave, CustomGoal.class)).thenReturn(customGoal);
        BulkSaveCustomGoalDto bulkSave = new BulkSaveCustomGoalDto(Collections.singletonList(customGoalDtoToSave));
        Assertions.assertThrows(CustomGoalNotSavedException.class, () -> customGoalService.save(bulkSave, 1L));
    }

    @Test
    void findAllTest() {
        CustomGoal customGoal = new CustomGoal(1L, "foo", null, null);
        when(customGoalRepo.findAll()).thenReturn(Collections.singletonList(customGoal));
        when(modelMapper.map(customGoal, CustomGoalResponseDto.class))
            .thenReturn(new CustomGoalResponseDto(customGoal.getId(), customGoal.getText()));
        List<CustomGoalResponseDto> findAllResult = customGoalService.findAll();
        assertEquals("foo", findAllResult.get(0).getText());
        assertEquals(1L, (long) findAllResult.get(0).getId());
    }

    @Test
    void findByNullIdTest() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> customGoalService.findById(null));
    }

    @Test
    void findByIdTest() {
        CustomGoal customGoal = new CustomGoal(1L, "foo", null, null);
        when(customGoalRepo.findById(anyLong())).thenReturn(java.util.Optional.of(customGoal));
        when(modelMapper.map(customGoal, CustomGoalResponseDto.class))
            .thenReturn(new CustomGoalResponseDto(customGoal.getId(), customGoal.getText()));
        CustomGoalResponseDto findByIdResult = customGoalService.findById(1L);
        assertEquals("foo", findByIdResult.getText());
        assertEquals(1L, (long) findByIdResult.getId());
    }

    @Test
    void updateEmptyInputBulkTest() {
        List<CustomGoalResponseDto> updateBulkResult =
            customGoalService.updateBulk(new BulkCustomGoalDto(Collections.emptyList()));
        assertTrue(updateBulkResult.isEmpty());
    }

    @Test
    void updateNonDuplicatedBulkIdTest() {
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
    void updateDuplicateBulkIdTest() {
        CustomGoalResponseDto customGoalResponseDto = new CustomGoalResponseDto(1L, "foo");
        CustomGoal customGoal =
            new CustomGoal(customGoalResponseDto.getId(), customGoalResponseDto.getText(), user, null);
        user.setCustomGoals(Collections.singletonList(customGoal));
        when(customGoalRepo.findById(customGoalResponseDto.getId())).thenReturn(Optional.of(customGoal));
        BulkCustomGoalDto bulkCustomGoalDto = new BulkCustomGoalDto(Collections.singletonList(customGoalResponseDto));
        Assertions
            .assertThrows(CustomGoalNotSavedException.class, () -> customGoalService.updateBulk(bulkCustomGoalDto));
    }

    @Test
    void updateNonExistentCustomGoalTest() {
        CustomGoalResponseDto customGoalResponseDto = new CustomGoalResponseDto(1L, "foo");
        when(customGoalRepo.findById(customGoalResponseDto.getId())).thenReturn(Optional.empty());
        BulkCustomGoalDto bulkCustomGoalDto = new BulkCustomGoalDto(Collections.singletonList(customGoalResponseDto));
        Assertions
            .assertThrows(NotFoundException.class,
                () -> customGoalService
                    .updateBulk(bulkCustomGoalDto));
    }

    @Test
    void findAllByUserWithNullIdTest() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> customGoalService.findAllByUser(null));
    }

    @Test
    void findAllByUserWithNonExistentIdTest() {
        when(customGoalRepo.findAllByUserId(1L)).thenReturn(Collections.emptyList());
        Assertions
            .assertThrows(NotFoundException.class,
                () -> customGoalService.findAllByUser(1L));
    }

    @Test
    void findAllByUserWithExistentIdTest() {
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
    void bulkDeleteWithNonExistentIdTest() {
        doThrow(new EmptyResultDataAccessException(1)).when(customGoalRepo).deleteById(1L);
        Assertions
            .assertThrows(NotFoundException.class,
                () -> customGoalService.bulkDelete("1"));
    }

    @Test
    void bulkDeleteWithExistentIdTest() {
        doNothing().when(customGoalRepo).deleteById(anyLong());
        List<Long> bulkDeleteResult = customGoalService.bulkDelete("1,2,3");
        ArrayList<Long> expectedResult = new ArrayList<>();
        expectedResult.add(1L);
        expectedResult.add(2L);
        expectedResult.add(3L);
        assertEquals(expectedResult, bulkDeleteResult);
    }
}
