package greencity.mapping;

import greencity.dto.search.SearchPlacesDto;
import greencity.entity.Category;
import greencity.entity.Place;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import static greencity.ModelUtils.getPlace;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SearchPlacesDtoMapperTest {
    @InjectMocks
    private SearchPlacesDtoMapper searchPlacesDtoMapper;

    @ParameterizedTest
    @ValueSource(strings = {"en", "other"})
    void convertTest(String language) {
        Place place = getPlace();
        LocaleContextHolder.setLocale(Locale.of(language));
        place.setCategory(Category.builder()
            .nameEn("Name")
            .nameUk("Назва")
            .build());
        SearchPlacesDto searchedPlace = SearchPlacesDto.builder()
            .id(1L)
            .name(place.getName())
            .category(place.getCategory().getNameEn())
            .build();

        assertEquals(searchedPlace, searchPlacesDtoMapper.convert(place));
    }

    @Test
    void convertWithUkLanguageTest() {
        Place place = getPlace();
        LocaleContextHolder.setLocale(Locale.of("uk"));
        place.setCategory(Category.builder()
            .nameEn("Name")
            .nameUk("Назва")
            .build());
        SearchPlacesDto searchedPlace = SearchPlacesDto.builder()
            .id(1L)
            .name(place.getName())
            .category(place.getCategory().getNameUk())
            .build();

        assertEquals(searchedPlace, searchPlacesDtoMapper.convert(place));
    }
}
