package greencity.service;

import greencity.dto.useraction.UserActionVO;
import greencity.entity.UserAction;
import greencity.repository.UserActionRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    private UserActionRepo userActionRepo;
    private final ModelMapper modelMapper;

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
        return userAction != null ? modelMapper.map(userAction, UserActionVO.class) : null;
    }

    @Override
    public UserActionVO save(UserActionVO userActionVO) {
        UserAction save = userActionRepo.save(modelMapper.map(userActionVO, UserAction.class));
        return modelMapper.map(save, UserActionVO.class);
    }
}
