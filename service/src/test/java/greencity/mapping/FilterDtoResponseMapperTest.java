package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserFilterDtoResponse;
import greencity.entity.Filter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class FilterDtoResponseMapperTest {
    @InjectMocks
    FilterDtoResponseMapper filterDtoResponseMapper;

    @Test
    void convertTest() {
        Filter filter = ModelUtils.getFilter();
        String values = filter.getValues();
        String[] criteria = values.split(";");

        UserFilterDtoResponse expected = new UserFilterDtoResponse(filter.getId(), filter.getName(),
            criteria[0], criteria[2], criteria[1]);

        assertEquals(expected, filterDtoResponseMapper.convert(filter));
    }
}
