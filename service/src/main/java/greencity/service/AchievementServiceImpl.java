package greencity.service;

import greencity.client.RestClient;
import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.*;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.user.UserVO;
import greencity.entity.Achievement;
import greencity.entity.AchievementCategory;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.entity.localization.AchievementTranslation;
import greencity.enums.AchievementStatus;
import greencity.enums.ActionContextType;
import greencity.enums.UserActionType;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.*;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
@EnableCaching
public class AchievementServiceImpl implements AchievementService {
    private final AchievementRepo achievementRepo;
    private final AchievementTranslationRepo achievementTranslationRepo;
    private final UserAchievementRepo userAchievementRepo;
    private final UserActionRepo userActionRepo;
    private final UserRepo userRepo;
    private final AchievementCategoryService achievementCategoryService;
    private final FileService fileService;
    private final ModelMapper modelMapper;
    private final RestClient restClient;

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public AchievementVO save(AchievementPostDto achievementPostDto, MultipartFile icon) {
        Achievement achievement = modelMapper.map(achievementPostDto, Achievement.class);
        AchievementCategoryVO achievementCategoryVO =
            achievementCategoryService.findByName(achievementPostDto.getAchievementCategory().getName());
        achievement.getTranslations().forEach(translation -> translation.setAchievement(achievement));
        achievement.setAchievementCategory(modelMapper.map(achievementCategoryVO, AchievementCategory.class));
        achievement.setIcon(fileService.upload(icon));
        Achievement savedAchievement = achievementRepo.save(achievement);

        userRepo.findAll()
            .forEach(user -> tryToGiveUserAchievement(user, savedAchievement));

        return modelMapper.map(savedAchievement, AchievementVO.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    @Cacheable(value = CacheConstants.ALL_ACHIEVEMENTS_CACHE_NAME)
    @Override
    public List<AchievementVO> findAll() {
        return filterActiveAndMapToAchievementVO(achievementRepo.findAll());
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public PageableAdvancedDto<AchievementVO> findAll(Pageable page) {
        Page<Achievement> pages = achievementRepo.findAll(page);
        List<AchievementVO> achievementVOS = filterActiveAndMapToAchievementVO(pages);
        return createPageable(achievementVOS, pages);
    }

    private List<AchievementVO> filterActiveAndMapToAchievementVO(Iterable<Achievement> achievements) {
        return StreamSupport.stream(achievements.spliterator(), false)
            .filter(achievement -> AchievementStatus.ACTIVE.equals(achievement.getAchievementStatus()))
            .map(achievement -> modelMapper.map(achievement, AchievementVO.class))
            .collect(Collectors.toList());
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
        List<AchievementVO> achievementVOS = filterActiveAndMapToAchievementVO(page);
        return createPageable(achievementVOS, page);
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public Long delete(Long id) {
        Achievement achievement = achievementRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ACHIEVEMENT_NOT_FOUND_BY_ID + id));
        achievement.setAchievementStatus(AchievementStatus.INACTIVE);
        achievementRepo.save(achievement);

        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public void deleteAll(List<Long> listId) {
        listId.forEach(this::delete);
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
     */
    @Override
    public List<AchievementNotification> findAllUnnotifiedForUser(Long userId) {
        UserVO user = restClient.findById(userId);
        List<AchievementTranslation> translationList = achievementTranslationRepo
            .findAllUnnotifiedForUser(userId, user.getLanguageVO().getId());
        return setAchievementNotifications(new ArrayList<>(), translationList, userId);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAchieved(User user, Achievement achievement) {
        return achievement.getCondition().entrySet().stream()
            .allMatch(condition -> {
                switch (condition.getKey()) {
                    case REGISTERED:
                        return true;
                    case HABIT_ACQUIRED:
                        return userActionRepo.existsByUserAndActionTypeAndContextTypeAndContextId(
                            user, UserActionType.HABIT_ACQUIRED, ActionContextType.HABIT, condition.getValue());
                    default:
                        return userActionRepo.countAllByUserAndActionType(user, condition.getKey()) >= condition
                            .getValue();
                }
            });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tryToGiveUserAchievement(User user, Achievement achievement) {
        if (AchievementStatus.INACTIVE.equals(achievement.getAchievementStatus())) {
            return;
        }
        if (userAchievementRepo.existsByUserAndAchievement(user, achievement)) {
            return;
        }
        if (!isAchieved(user, achievement)) {
            return;
        }
        UserAchievement newUserAchievement = UserAchievement.builder()
            .achievement(achievement)
            .user(user)
            .achievedTimestamp(ZonedDateTime.now())
            .notified(false).build();
        userAchievementRepo.save(newUserAchievement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tryToGiveUserAchievementsByActionType(User user, UserActionType actionType) {
        achievementRepo.findAllByActionType(actionType)
            .forEach(achievement -> tryToGiveUserAchievement(user, achievement));
    }
}
