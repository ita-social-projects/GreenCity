package greencity.service;

import greencity.ModelUtils;
import greencity.dto.achievementcategory.AchievementCategoryDto;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.entity.AchievementCategory;
import greencity.exception.exceptions.BadCategoryRequestException;
import greencity.repository.AchievementCategoryRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AchievementCategoryServiceImplTest {

    @Mock
    private AchievementCategoryRepo achievementCategoryRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AchievementCategoryServiceImpl achievementCategoryService;

    @Test
    void saveThrowExceptionTest() {
        AchievementCategoryDto achievementCategoryDto = ModelUtils.getAchievementCategoryDto();
        when(achievementCategoryRepo.findByName(achievementCategoryDto.getName()))
            .thenReturn(ModelUtils.getAchievementCategory());
        assertThrows(BadCategoryRequestException.class, () -> achievementCategoryService.save(achievementCategoryDto));
    }

    @Test
    void saveTest() {
        AchievementCategoryDto achievementCategoryDto = ModelUtils.getAchievementCategoryDto();
        when(achievementCategoryRepo.findByName(achievementCategoryDto.getName())).thenReturn(null);
        AchievementCategory toSave = ModelUtils.getAchievementCategory();
        AchievementCategoryVO expected = ModelUtils.getAchievementCategoryVO();
        when(modelMapper.map(achievementCategoryDto, AchievementCategory.class)).thenReturn(toSave);
        when(achievementCategoryRepo.save(toSave)).thenReturn(toSave);
        when(modelMapper.map(toSave, AchievementCategoryVO.class)).thenReturn(expected);
        AchievementCategoryVO actual = achievementCategoryService.save(achievementCategoryDto);
        assertEquals(expected, actual);
    }

    @Test
    void findAllTest() {
        List<AchievementCategory> list = Collections.singletonList(ModelUtils.getAchievementCategory());
        List<AchievementCategoryVO> expected = Collections.singletonList(ModelUtils.getAchievementCategoryVO());
        when(achievementCategoryRepo.findAll()).thenReturn(list);
        when(modelMapper.map(list, new TypeToken<List<AchievementCategoryVO>>() {
        }.getType())).thenReturn(expected);
        List<AchievementCategoryVO> actual = achievementCategoryService.findAll();
        assertEquals(expected, actual);
    }

    @Test
    void findByNameTest() {
        AchievementCategory achievementCategory = ModelUtils.getAchievementCategory();
        AchievementCategoryVO expected = ModelUtils.getAchievementCategoryVO();
        when(achievementCategoryRepo.findByName("Name")).thenReturn(achievementCategory);
        when(modelMapper.map(achievementCategory, AchievementCategoryVO.class)).thenReturn(expected);
        AchievementCategoryVO actual = achievementCategoryService.findByName("Name");
        assertEquals(expected, actual);
    }

    @Test
    void findByNameThrowException() {
        when(achievementCategoryRepo.findByName("Not Exist")).thenReturn(null);
        assertThrows(BadCategoryRequestException.class, () -> achievementCategoryService.findByName("Not Exist"));
    }
}
