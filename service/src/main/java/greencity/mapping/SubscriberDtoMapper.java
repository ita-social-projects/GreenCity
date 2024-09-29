package greencity.mapping;

import greencity.dto.user.SubscriberDto;
import greencity.dto.user.UserVO;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class SubscriberDtoMapper extends AbstractConverter<UserVO, SubscriberDto> {
    @Override
    protected SubscriberDto convert(UserVO userVO) {
        return SubscriberDto.builder()
            .email(userVO.getEmail())
            .name(userVO.getName())
            .language(userVO.getLanguageVO().getCode())
            .build();
    }
}
