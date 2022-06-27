package greencity.mapping;

import greencity.dto.user.UserFilterDtoRequest;
import greencity.entity.Filter;
import greencity.enums.FilterType;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class FilterDtoRequestMapper extends AbstractConverter<UserFilterDtoRequest, Filter> {
    @Override
    protected Filter convert(UserFilterDtoRequest filterUserDto) {
        StringBuilder values = new StringBuilder(filterUserDto.getSearchCriteria());
        values.append(";");
        values.append(filterUserDto.getUserRole());
        values.append(";");
        values.append(filterUserDto.getUserStatus());
        return Filter.builder()
            .name(filterUserDto.getName())
            .type(FilterType.USERS.toString())
            .values(values.toString())
            .build();
    }
}
