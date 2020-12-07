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
public class UserActionServiceImpl implements UserActionService{
    private UserActionRepo userActionRepo;
    private final ModelMapper modelMapper;
    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public UserActionVO updateUserActions(UserActionVO userActionVO) {
        Optional<UserAction> byId = userActionRepo.findById(userActionVO.getId());
        if (byId.isPresent()) {
            UserAction toUpdate = byId.get();
            toUpdate.setAchievements(userActionVO.getAchievements());
            toUpdate.setAcquiredHabit(userActionVO.getAcquiredHabit());
            toUpdate.setEcoNews(userActionVO.getEcoNews());
            toUpdate.setEcoNewsComments(userActionVO.getEcoNewsComments());
            toUpdate.setEcoNewsLikes(userActionVO.getEcoNewsLikes());
            toUpdate.setHabitStreak(userActionVO.getHabitStreak());
            toUpdate.setRating(userActionVO.getRating());
            toUpdate.setSocialNetworks(userActionVO.getSocialNetworks());
            toUpdate.setTipsAndTricksComments(userActionVO.getTipsAndTricksComments());
            toUpdate.setTipsAndTricksLikes(userActionVO.getTipsAndTricksLikes());
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
    public UserActionVO findUserActionByUserId(Long id) {
        UserAction userAction = userActionRepo.findByUserId(id);
        return modelMapper.map(userAction, UserActionVO.class);
    }
}
