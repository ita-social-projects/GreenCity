package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.AchievementManagementDto;
import greencity.dto.achievement.AchievementPostDto;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.ActionDto;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.Achievement;
import greencity.entity.AchievementCategory;
import greencity.entity.UserAchievement;
import greencity.enums.AchievementCategoryType;
import greencity.enums.AchievementAction;
import greencity.enums.AchievementStatus;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.AchievementRepo;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import greencity.repository.UserAchievementRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@EnableCaching
public class AchievementServiceImpl implements AchievementService {
    private final AchievementRepo achievementRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final AchievementCategoryService achievementCategoryService;
    private final UserActionService userActionService;
    private final AchievementCalculation achievementCalculation;
    private final UserService userService;
    private final UserAchievementRepo userAchievementRepo;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void achieve(ActionDto user) {
        List<UserAchievement> userAchievements =
            userAchievementRepo.getUserAchievementByUserId(user.getUserId())
                .stream()
                .filter(userAchievement -> !userAchievement.isNotified())
                .toList();
        messagingTemplate.convertAndSend("/topic/" + user.getUserId() + "/notification", !userAchievements.isEmpty());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AchievementVO save(AchievementPostDto achievementPostDto) {
        Achievement achievement = modelMapper.map(achievementPostDto, Achievement.class);
        AchievementCategoryVO achievementCategoryVO =
            achievementCategoryService.findByName(achievementPostDto.getAchievementCategory().getName());
        achievement.setTitle(achievementPostDto.getTitle());
        achievement.setName(achievementPostDto.getName());
        achievement.setNameEng(achievementPostDto.getNameEng());
        achievement.setAchievementCategory(modelMapper.map(achievementCategoryVO, AchievementCategory.class));
        AchievementVO achievementVO = modelMapper.map(achievementRepo.save(achievement), AchievementVO.class);
        UserAchievementVO userAchievementVO = new UserAchievementVO();
        UserActionVO userActionVO = new UserActionVO();
        userAchievementVO.setAchievement(achievementVO);
        restClient.findAll().forEach(userVO -> {
            UserActionVO userActionByUserIdAndAchievementCategory =
                userActionService.findUserAction(userVO.getId(),
                    achievementCategoryVO.getId());
            if (userActionByUserIdAndAchievementCategory == null) {
                userActionVO.setAchievementCategory(achievementCategoryVO);
                userActionVO.setUser(userVO);
                userActionService.save(userActionVO);
            }
            userVO.getUserAchievements().add(userAchievementVO);
            userAchievementVO.setUser(userVO);
            userService.updateUserRating(userVO.getId(), userVO.getRating());
        });
        return achievementVO;
    }

    /**
     * {@inheritDoc}
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

    private List<AchievementVO> findAll() {
        return achievementRepo.findAll()
            .stream()
            .map(achieve -> modelMapper.map(achieve, AchievementVO.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AchievementVO> findAllByType(String principalEmail, AchievementStatus achievementStatus) {
        Long userId = userService.findByEmail(principalEmail).getId();
        if (achievementStatus == AchievementStatus.ACHIEVED) {
            return findAllAchieved(userId);
        } else if (achievementStatus == AchievementStatus.UNACHIEVED) {
            return achievementRepo.searchAchievementsUnAchieved(userId).stream()
                .map(achieve -> modelMapper.map(achieve, AchievementVO.class))
                .toList();
        }
        return findAll();
    }

    private List<AchievementVO> findAllAchieved(Long userId) {
        return userAchievementRepo.getUserAchievementByUserId(userId).stream()
            .map(userAchievement -> userAchievement.getAchievement().getId())
            .map(achievementRepo::findById)
            .flatMap(Optional::stream)
            .map(achieve -> modelMapper.map(achieve, AchievementVO.class))
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
     */
    @Override
    public void deleteAll(List<Long> listId) {
        listId.forEach(achievementRepo::deleteById);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AchievementVO findById(Long id) {
        return achievementRepo.findById(id)
            .map(a -> modelMapper.map(a, AchievementVO.class))
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ACHIEVEMENT_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AchievementPostDto update(AchievementManagementDto achievementManagementDto) {
        Achievement achievement = achievementRepo.findById(achievementManagementDto.getId())
            .orElseThrow(() -> new NotUpdatedException(ErrorMessage.ACHIEVEMENT_NOT_FOUND_BY_ID
                + achievementManagementDto.getId()));
        Achievement updated = achievementRepo.save(achievement);
        return modelMapper.map(updated, AchievementPostDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AchievementVO findByCategoryIdAndCondition(Long categoryId, Integer condition) {
        return achievementRepo.findByAchievementCategoryIdAndCondition(categoryId, condition)
            .map(a -> modelMapper.map(a, AchievementVO.class))
            .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateAchievements(Long id, AchievementCategoryType achievementCategory,
        AchievementAction achievementAction) {
        achievementCalculation.calculateAchievement(userService.findById(id), achievementCategory, achievementAction);
    }
}
