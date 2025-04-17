package greencity.mapping;

import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserManagementVOMapper extends AbstractConverter<User, UserManagementVO> {

    private final ModelMapper modelMapper;

    @Lazy
    public UserManagementVOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    protected UserManagementVO convert(User user) {
        UserVO userVO = modelMapper.map(user, UserVO.class);

        return UserManagementVO.builder()
            .id(user.getId())
            .name(userVO.getName())
            .email(userVO.getEmail())
            .userCredo(userVO.getUserCredo())
            .role(userVO.getRole())
            .userStatus(userVO.getUserStatus())
            .build();
    }

    /**
     * Method to convert {@link Page} of {@link User} to {@link Page} of
     * {@link UserManagementVO}.
     *
     * @param users {@link Page} of {@link User}
     * @return {@link Page} of {@link UserManagementVO}
     * @author Anton Bondar
     */
    public Page<UserManagementVO> mapAllToPage(Page<User> users) {
        return users.map(this::convert);
    }
}
