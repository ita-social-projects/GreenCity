package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.category.CategoryDto;
import greencity.dto.discount.DiscountValueDto;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.photo.PhotoAddDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.specification.SpecificationNameDto;
import greencity.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class ProposePlaceMapperTest {

    @InjectMocks
    private ProposePlaceMapper proposePlaceMapper;

    @Test
    void convert() {
        OpeningHoursDto openingHours = ModelUtils.getOpeningHoursDto();

        CategoryDto category = CategoryDto.builder()
            .name("category")
            .build();

        PhotoAddDto photo = new PhotoAddDto();
        photo.setName("photo");

        DiscountValueDto discountValue = ModelUtils.getDiscountValueDto();
        discountValue.setSpecification(new SpecificationNameDto("specif"));

        LocationAddressAndGeoDto address = ModelUtils.getLocationAddressAndGeoDto();

        PlaceAddDto placeAddDto = PlaceAddDto.builder()
            .category(category)
            .name("place")
            .photos(Collections.singletonList(photo))
            .discountValues(Collections.singleton(discountValue))
            .location(address)
            .openingHoursList(Collections.singleton(openingHours))
            .build();

        OpeningHours openingHours1 = ModelUtils.getOpeningHours();

        DiscountValue discountValue1 = ModelUtils.getDiscountValue();
        discountValue1.setSpecification(ModelUtils.getSpecification());

        Photo photoTest = ModelUtils.getPhoto();

        Place place = new Place();
        place.setName("place");
        place.setLocation(ModelUtils.getLocation());
        place.setCategory(Category.builder()
            .name("category")
            .build());
        place.setOpeningHoursList(Collections.singleton(openingHours1));
        place.setDiscountValues(Collections.singleton(discountValue1));
        place.setPhotos(Collections.singletonList(photoTest));
        place.getOpeningHoursList().forEach(h -> h.setPlace(place));
        place.getPhotos().forEach(photo11 -> photo11.setUser(place.getAuthor()));

        assertEquals(place, proposePlaceMapper.convert(placeAddDto));
    }
}