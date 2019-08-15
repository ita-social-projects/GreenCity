package greencity;

import greencity.mapping.testdata.PlaceMapper;
import greencity.mapping.testdata.dto.PlaceDto;
import greencity.mapping.testdata.entities.Place;
import greencity.mapping.testdata.repository.PlaceRepository;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MapperTest {

    @Autowired
    private PlaceRepository repository;

    @Autowired
    private PlaceMapper mapper;

    @Test
    public void mapToTest() {

        Optional<Place> optional = repository.findById(1);
        Place place = optional.orElse(null);

        PlaceDto dto = mapper.convertToDto(place);
        Assert.assertEquals(place.getId(), dto.getId());
        Assert.assertEquals(place.getTitle(), dto.getTitle());
        Assert.assertEquals(2, dto.getVisitorIds().size());

        System.out.println(dto.toString());

        Place entity = mapper.convertToEntity(dto);
        Assert.assertEquals(place.getId(), entity.getId());
        Assert.assertEquals(place.getTitle(), entity.getTitle());
        Assert.assertEquals(2, entity.getVisitors().size());
    }
}
