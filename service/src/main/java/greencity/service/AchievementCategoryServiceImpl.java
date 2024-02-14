package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.achievementcategory.AchievementCategoryDto;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.entity.AchievementCategory;
import greencity.exception.exceptions.BadCategoryRequestException;
import greencity.repository.AchievementCategoryRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AchievementCategoryServiceImpl implements AchievementCategoryService {
    private final AchievementCategoryRepo achievementCategoryRepo;
    private final ModelMapper modelMapper;

    /**
     * Method for saving {@link AchievementCategoryVO} to database.
     *
     * @param achievementCategoryDto - dto for Category entity
     * @return {@link AchievementCategoryVO}
     * @author Orest Mamchuk
     */
    @Override
    public AchievementCategoryVO save(AchievementCategoryDto achievementCategoryDto) {
        AchievementCategory achievementCategory = achievementCategoryRepo.findByName(achievementCategoryDto.getName());
        if (achievementCategory != null) {
            throw new BadCategoryRequestException(
                ErrorMessage.CATEGORY_ALREADY_EXISTS_BY_THIS_NAME);
        }
        AchievementCategory achievementCategoryToSave =
            modelMapper.map(achievementCategoryDto, AchievementCategory.class);

        return modelMapper.map(achievementCategoryRepo.save(achievementCategoryToSave), AchievementCategoryVO.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public List<AchievementCategoryVO> findAll() {
        return modelMapper.map(achievementCategoryRepo.findAll(), new TypeToken<List<AchievementCategoryVO>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    @Transactional
    public AchievementCategoryVO findByName(String name) {
        AchievementCategory achievementCategory = achievementCategoryRepo.findByName(name);
        if (achievementCategory == null) {
            throw new BadCategoryRequestException(ErrorMessage.CATEGORY_NOT_FOUND_BY_NAME);
        }
        return modelMapper.map(achievementCategory, AchievementCategoryVO.class);
    }
}
