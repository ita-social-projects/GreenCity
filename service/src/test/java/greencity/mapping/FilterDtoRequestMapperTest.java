package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserFilterDtoRequest;
import greencity.entity.Filter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class FilterDtoRequestMapperTest {
    @InjectMocks
    FilterDtoRequestMapper filterDtoRequestMapper;

    @Test
    void convertTest() {
        Filter filter = ModelUtils.getFilter();
        String values = filter.getValues();
        String[] criteria = values.split(";");

        UserFilterDtoRequest userFilterDtoRequest = new UserFilterDtoRequest(filter.getName(), criteria[0],
            criteria[2], criteria[1]);

        Filter expected = Filter.builder()
            .name(filter.getName())
            .type(filter.getType())
            .values(filter.getValues())
            .build();

        assertEquals(expected, filterDtoRequestMapper.convert(userFilterDtoRequest));
    }

}
