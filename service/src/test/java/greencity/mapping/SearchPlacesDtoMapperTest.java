package greencity.mapping;

import greencity.dto.search.SearchPlacesDto;
import greencity.entity.Category;
import greencity.entity.Place;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import static greencity.ModelUtils.getPlace;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SearchPlacesDtoMapperTest {
    @InjectMocks
    private SearchPlacesDtoMapper searchPlacesDtoMapper;

    @Test
    void convertTest() {
        Place place = getPlace();
        place.setCategory(Category.builder()
            .nameEn("Name")
            .nameUk("Назва")
            .build());
        String language = LocaleContextHolder.getLocale().getLanguage();
        SearchPlacesDto searchedPlace = SearchPlacesDto.builder()
            .id(1L)
            .name(place.getName())
            .category(language.equals("uk") ? place.getCategory().getNameUk() : place.getCategory().getNameEn())
            .build();

        assertEquals(searchedPlace, searchPlacesDtoMapper.convert(place));
    }
}
