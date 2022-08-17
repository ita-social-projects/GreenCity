package greencity.mapping;

import greencity.dto.user.UserFilterDtoResponse;
import greencity.entity.Filter;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class FilterDtoResponseMapper extends AbstractConverter<Filter, UserFilterDtoResponse> {
    @Override
    protected UserFilterDtoResponse convert(Filter filter) {
        String values = filter.getValues();
        String[] criterias = values.split(";");
        return UserFilterDtoResponse
            .builder()
            .id(filter.getId())
            .name(filter.getName())
            .searchCriteria(criterias[0])
            .userRole(criterias[1])
            .userStatus(criterias[2])
            .build();
    }
}
