package greencity.mapping;

import greencity.dto.place.PlaceAddDto;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.Specification;
import greencity.service.CategoryService;
import greencity.service.SpecificationService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ProposePlaceMapper implements Mapper<Place, PlaceAddDto> {
    private ModelMapper mapper;
    private CategoryService categoryService;
    private SpecificationService specService;

    @Override
    public Place convertToEntity(PlaceAddDto dto) {
        Category category = categoryService.findByName(dto.getCategory().getName());
        Place place = mapper.map(dto, Place.class);
        place.setCategory(category);
        place.getOpeningHoursList().forEach(h -> h.setPlace(place));
        place.getDiscountValues().forEach(disc -> {
            Specification specification = specService.findByName(disc.getSpecification().getName());
            disc.setSpecification(specification);
            disc.setPlace(place);
        });
        return place;
    }

    @Override
    public PlaceAddDto convertToDto(Place entity) {
        return null;
    }
}
