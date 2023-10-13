package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.UserAction;
import greencity.repository.AchievementCategoryRepo;
import greencity.repository.UserActionRepo;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    private UserActionRepo userActionRepo;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;
    private final AchievementCategoryRepo achievementCategoryRepo;

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Transactional
    @Override
    public UserActionVO updateUserActions(UserActionVO userActionVO) {
        Optional<UserAction> byId = userActionRepo.findById(userActionVO.getId());
        if (byId.isPresent()) {
            UserAction toUpdate = byId.get();
            toUpdate.setCount(userActionVO.getCount());
            return modelMapper.map(userActionRepo.save(toUpdate), UserActionVO.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Transactional
    @Override
    public UserActionVO findUserActionByUserIdAndAchievementCategory(Long userId, Long categoryId) {
        UserAction userAction = userActionRepo.findByUserIdAndAchievementCategoryId(userId, categoryId);
        if (userAction == null) {
            userAction = UserAction.builder()
                .user(userRepo.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId)))
                .count(0)
                .achievementCategory(achievementCategoryRepo.findById(categoryId).orElseThrow(
                    () -> new NoSuchElementException(ErrorMessage.ACHIEVEMENT_CATEGORY_NOT_FOUND_BY_ID + categoryId)))
                .build();
            userActionRepo.save(userAction);
        }
        return userAction != null ? modelMapper.map(userAction, UserActionVO.class) : null;
    }

    @Override
    public UserActionVO save(UserActionVO userActionVO) {
        UserAction save = userActionRepo.save(modelMapper.map(userActionVO, UserAction.class));
        return modelMapper.map(save, UserActionVO.class);
    }
}
