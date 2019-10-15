package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.place.PlaceAddDto;
import greencity.entity.*;
import greencity.exception.BadRequestException;
import greencity.exception.NotFoundException;
import greencity.exception.NotImplementedMethodException;
import greencity.service.CategoryService;
import greencity.service.LocationService;
import greencity.service.PhotoService;
import greencity.service.SpecificationService;
import java.util.List;
import java.util.Set;
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
    private PhotoService photoService;
    private LocationService locationService;

    @Override
    public Place convertToEntity(PlaceAddDto dto) {
        Category category = categoryService.findByName(dto.getCategory().getName());
        checkLocationValues(dto.getLocation());
        checkInputTime(dto.getOpeningHoursList());
        Place place = mapper.map(dto, Place.class);
        place.setCategory(category);
        place.getOpeningHoursList().forEach(h -> h.setPlace(place));
        saveDiscountValuesWithPlace(place.getDiscountValues(), place);
        savePhotosWithPlace(place.getPhotos(), place);

        return place;
    }

    private void checkLocationValues(LocationAddressAndGeoDto dto) {
        if (locationService.findByLatAndLng(dto.getLat(), dto.getLng()).isPresent()) {
            throw new BadRequestException(ErrorMessage.LOCATION_IS_PRESENT);
        }
    }

    private void savePhotosWithPlace(List<Photo> photos, Place place) {
        photos.forEach(photo -> {
            if (photoService.findByName(photo.getName()).isPresent()) {
                throw new BadRequestException(ErrorMessage.PHOTO_IS_PRESENT);
            }
            photo.setPlace(place);
        });
    }

    private void saveDiscountValuesWithPlace(Set<DiscountValue> discountValues, Place place) {
        discountValues.forEach(disc -> {
            Specification specification = specService.findByName(disc.getSpecification().getName())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.SPECIFICATION_NOT_FOUND_BY_NAME
                    + disc.getSpecification().getName()));
            disc.setSpecification(specification);
            disc.setPlace(place);
        });
    }

    private void checkInputTime(Set<OpeningHoursDto> dto) {
        dto.forEach(hours -> {
            if (hours.getOpenTime().getHour() > hours.getCloseTime().getHour()) {
                throw new BadRequestException(ErrorMessage.CLOSE_TIME_LATE_THAN_OPEN_TIME);
            }
            if (hours.getBreakTime() != null) {
                if (hours.getBreakTime().getStartTime().getHour() < hours.getOpenTime().getHour()
                    || hours.getBreakTime().getEndTime().getHour() > hours.getCloseTime().getHour()) {
                    throw new BadRequestException(ErrorMessage.WRONG_BREAK_TIME);
                }
            }
        });
    }

    @Override
    public PlaceAddDto convertToDto(Place entity) {
        throw new NotImplementedMethodException(ErrorMessage.NOT_IMPLEMENTED_METHOD);
    }
}
