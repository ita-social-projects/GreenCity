package greencity.mapping.testdata;

import greencity.mapping.Mapper;
import greencity.mapping.testdata.dto.VisitorDto;
import greencity.mapping.testdata.entities.Place;
import greencity.mapping.testdata.entities.Visitor;
import greencity.mapping.testdata.repository.PlaceRepository;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class VisitorMapper implements Mapper<Visitor, VisitorDto> {

    private ModelMapper mapper;
    private PlaceRepository repository;

    @Override
    public Visitor convertToEntity(VisitorDto dto) {
        Visitor visitor = mapper.map(dto, Visitor.class);
        visitor.setPlaces(repository.findPlacesByVisitors(visitor));
        return visitor;
    }

    @Override
    public VisitorDto convertToDto(Visitor entity) {
        VisitorDto dto = mapper.map(entity, VisitorDto.class);
        dto.setPlaceIds(entity.getPlaces().stream().map(Place::getId).collect(Collectors.toList()));
        return dto;
    }
}
