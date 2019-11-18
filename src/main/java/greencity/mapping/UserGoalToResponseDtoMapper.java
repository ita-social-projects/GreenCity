package greencity.mapping;

import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.UserGoal;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserGoalToResponseDtoMapper implements MapperToDto<UserGoal, UserGoalResponseDto> {
    private ModelMapper modelMapper;

    @Override
    public UserGoalResponseDto convertToDto(UserGoal entity) {
        UserGoalResponseDto dto = modelMapper.map(entity, UserGoalResponseDto.class);
        dto.setText(entity.getGoal().getText());
        return dto;
    }
}
