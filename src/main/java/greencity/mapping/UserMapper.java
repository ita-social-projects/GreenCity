package greencity.mapping;

import greencity.dto.user.PlaceAuthorDto;
import greencity.entities.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * The class converts {@link User} entity objects to {@link PlaceAuthorDto} dto objects and vise
 * versa.
 */
@AllArgsConstructor
@Component
public class UserMapper implements Mapper<User, PlaceAuthorDto> {

    private ModelMapper mapper;

    @Override
    public User convertToEntity(PlaceAuthorDto dto) {
        return null;
    }

    @Override
    public PlaceAuthorDto convertToDto(User entity) throws IllegalArgumentException {
        return mapper.map(entity, PlaceAuthorDto.class);
    }
}
