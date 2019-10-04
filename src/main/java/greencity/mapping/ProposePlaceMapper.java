package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.place.PlaceAddDto;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.Specification;
import greencity.exception.NotFoundException;
import greencity.exception.NotImplementedMethodException;
import greencity.service.CategoryService;
import greencity.service.SpecificationService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * The class uses other {@code Autowired} mappers to convert {@link Place} entity objects to {@link
 * PlaceAddDto} dto objects and vise versa.
 *
 * @author Kateryna Horokh
 */
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
            Specification specification = specService.findByName(disc.getSpecification().getName())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.SPECIFICATION_NOT_FOUND_BY_NAME
                    + disc.getSpecification().getName()));
            disc.setSpecification(specification);
            disc.setPlace(place);
        });
        return place;
    }

    @Override
    public PlaceAddDto convertToDto(Place entity) {
        throw new NotImplementedMethodException(ErrorMessage.NOT_IMPLEMENTED_METHOD);
    }
}
