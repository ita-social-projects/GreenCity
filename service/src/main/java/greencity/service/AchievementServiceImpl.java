package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.client.RestClient;
import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.*;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.Achievement;
import greencity.entity.AchievementCategory;
import greencity.entity.UserAchievement;
import greencity.entity.localization.AchievementTranslation;
import greencity.enums.AchievementCategoryType;
import greencity.enums.AchievementType;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.AchievementRepo;
import greencity.repository.AchievementTranslationRepo;
import greencity.repository.UserAchievementRepo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@EnableCaching
public class AchievementServiceImpl implements AchievementService {
    private final AchievementRepo achievementRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final AchievementCategoryService achievementCategoryService;
    private final UserActionService userActionService;
    private UserAchievementRepo userAchievementRepo;
    private final AchievementTranslationRepo achievementTranslationRepo;
    private AchievementCalculation achievementCalculation;

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public AchievementVO save(AchievementPostDto achievementPostDto) {
        Achievement achievement = modelMapper.map(achievementPostDto, Achievement.class);
        AchievementCategoryVO achievementCategoryVO =
            achievementCategoryService.findByName(achievementPostDto.getAchievementCategory().getName());
        achievement.getTranslations().forEach(adviceTranslation -> adviceTranslation.setAchievement(achievement));
        achievement.setAchievementCategory(modelMapper.map(achievementCategoryVO, AchievementCategory.class));
        AchievementVO achievementVO = modelMapper.map(achievementRepo.save(achievement), AchievementVO.class);
        UserAchievementVO userAchievementVO = new UserAchievementVO();
        UserActionVO userActionVO = new UserActionVO();
        userAchievementVO.setAchievement(achievementVO);
        List<UserVO> all = restClient.findAll();
        all.forEach(userVO -> {
            UserActionVO userActionByUserIdAndAchievementCategory =
                userActionService.findUserActionByUserIdAndAchievementCategory(userVO.getId(),
                    achievementCategoryVO.getId());
            if (userActionByUserIdAndAchievementCategory == null) {
                userActionVO.setAchievementCategory(achievementCategoryVO);
                userActionVO.setUser(userVO);
                userActionService.save(userActionVO);
            }
            userVO.getUserAchievements().add(userAchievementVO);
            userAchievementVO.setUser(userVO);
            restClient.save(userVO);
        });
        return achievementVO;
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    @Cacheable(value = CacheConstants.ALL_ACHIEVEMENTS_CACHE_NAME)
    @Override
    public List<AchievementVO> findAll() {
        return achievementRepo.findAll()
            .stream()
            .map(achieve -> modelMapper.map(achieve, AchievementVO.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public PageableAdvancedDto<AchievementVO> findAll(Pageable page) {
        Page<Achievement> pages = achievementRepo.findAll(page);
        List<AchievementVO> achievementVOS = pages
            .stream()
            .map(achievement -> modelMapper.map(achievement, AchievementVO.class))
            .collect(Collectors.toList());
        return createPageable(achievementVOS, pages);
    }

    private PageableAdvancedDto<AchievementVO> createPageable(List<AchievementVO> achievementVOS,
        Page<Achievement> pages) {
        return new PageableAdvancedDto<>(
            achievementVOS,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages(),
            pages.getNumber(),
            pages.hasPrevious(),
            pages.hasNext(),
            pages.isFirst(),
            pages.isLast());
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public PageableAdvancedDto<AchievementVO> searchAchievementBy(Pageable paging, String query) {
        Page<Achievement> page = achievementRepo.searchAchievementsBy(paging, query);

        List<AchievementVO> achievementVOS = page.stream()
            .map(achievement -> modelMapper.map(achievement, AchievementVO.class))
            .collect(Collectors.toList());
        return createPageable(achievementVOS, page);
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public Long delete(Long id) {
        try {
            achievementRepo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotDeletedException(ErrorMessage.ACHIEVEMENT_NOT_DELETED);
        }
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public void deleteAll(List<Long> listId) {
        listId.forEach(achievementRepo::deleteById);
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public AchievementVO findById(Long id) {
        return modelMapper.map(
            achievementRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ACHIEVEMENT_NOT_FOUND_BY_ID + id)),
            AchievementVO.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public AchievementPostDto update(AchievementManagementDto achievementManagementDto) {
        Achievement achievement = achievementRepo.findById(achievementManagementDto.getId())
            .orElseThrow(() -> new NotUpdatedException(ErrorMessage.ACHIEVEMENT_NOT_FOUND_BY_ID
                + achievementManagementDto.getId()));
        setTranslations(achievement, achievementManagementDto);
        achievement.setCondition(achievementManagementDto.getCondition());
        Achievement updated = achievementRepo.save(achievement);
        return modelMapper.map(updated, AchievementPostDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    @Transactional
    public AchievementVO findByCategoryIdAndCondition(Long categoryId, Integer condition) {
        Achievement achievement =
            achievementRepo.findByAchievementCategoryIdAndCondition(categoryId, condition).orElse(null);
        return achievement != null ? modelMapper.map(achievement, AchievementVO.class) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AchievementNotification> findAchievementsWithStatusActive(Long userId) {
        UserVO user = restClient.findById(userId);
        List<AchievementTranslation> translationList = achievementTranslationRepo
            .findAchievementsWithStatusActive(userId, user.getLanguageVO().getId());
        return setAchievementNotifications(new ArrayList<>(), translationList, userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateAchievements(Long id, AchievementType achievementType,
        AchievementCategoryType achievementCategory, Integer size) {
        achievementCalculation.calculateAchievement(id, achievementType, achievementCategory, size);
    }

    private List<AchievementNotification> setAchievementNotifications(
        List<AchievementNotification> achievementNotifications,
        List<AchievementTranslation> translationList, Long userId) {
        translationList.forEach(translation -> {
            achievementNotifications.add(AchievementNotification.builder()
                .id(translation.getId())
                .title(translation.getTitle())
                .description(translation.getDescription())
                .message(translation.getMessage())
                .build());
            UserAchievement userAchievement = userAchievementRepo
                .getUserAchievementByIdAndAchievementId(userId, translation.getAchievement().getId());
            userAchievement.setNotified(true);
            userAchievementRepo.save(userAchievement);
        });
        return achievementNotifications;
    }

    private void setTranslations(Achievement achievement, AchievementManagementDto achievementManagementDto) {
        achievement.getTranslations()
            .forEach(achievementTranslation -> {
                AchievementTranslationVO achievementTranslationVO = achievementManagementDto
                    .getTranslations().stream()
                    .filter(newTranslation -> newTranslation.getLanguage().getCode()
                        .equals(achievementTranslation.getLanguage().getCode()))
                    .findFirst().get();
                achievementTranslation.setTitle(achievementTranslationVO.getTitle());
                achievementTranslation.setDescription(achievementTranslationVO.getDescription());
                achievementTranslation.setMessage(achievementTranslationVO.getMessage());
            });
    }
}
