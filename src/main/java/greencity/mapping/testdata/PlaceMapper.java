package greencity.mapping.testdata;

import greencity.mapping.Mapper;
import greencity.mapping.testdata.dto.PlaceDto;
import greencity.mapping.testdata.entities.Place;
import greencity.mapping.testdata.entities.Visitor;
import greencity.mapping.testdata.repository.VisitorRepository;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PlaceMapper implements Mapper<Place, PlaceDto> {

    private ModelMapper mapper;
    private VisitorRepository repository;

    @Override
    public Place convertToEntity(PlaceDto dto) {
        Place place = mapper.map(dto, Place.class);
        place.setVisitors(repository.findVisitorByPlaces(place));
        return place;
    }

    @Override
    public PlaceDto convertToDto(Place entity) {
        PlaceDto dto = mapper.map(entity, PlaceDto.class);
        dto.setVisitorIds(
                entity.getVisitors().stream().map(Visitor::getId).collect(Collectors.toList()));
        return dto;
    }
}
