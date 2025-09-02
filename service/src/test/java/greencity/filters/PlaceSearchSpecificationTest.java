package greencity.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import greencity.entity.Category;
import greencity.entity.Category_;
import greencity.entity.FavoritePlace;
import greencity.entity.FavoritePlace_;
import greencity.entity.Location;
import greencity.entity.Location_;
import greencity.entity.Place;
import greencity.entity.Place_;
import greencity.entity.User;
import greencity.entity.User_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PlaceSearchSpecificationTest {

    @Mock
    private CriteriaQuery<?> criteriaQueryMock;
    @Mock
    private CriteriaBuilder criteriaBuilderMock;
    @Mock
    private Predicate expected;
    @Mock
    private Root<Place> rootMock;
    @Mock
    private Path<String> stringPathMock;
    @Mock
    private Path<Long> longPathMock;
    @Mock
    private Join<Place, Location> locationJoin;
    @Mock
    private Join<Place, Category> categoryJoin;
    @Mock
    private Join<Place, FavoritePlace> favoritePlaceJoin;
    @Mock
    Join<FavoritePlace, User> favoritePlaceUserJoin;

    private final Long userId = 1L;
    private PlaceSearchSpecification placeSearchSpecification;

    @ParameterizedTest
    @CsvSource({"test,true", "test,false"})
    void toPredicateTest(String place, Boolean isFavorite) {
        List<SearchCriteria> searchCriteriaList = createSearchCriteriaList(place, isFavorite);
        placeSearchSpecification = new PlaceSearchSpecification(searchCriteriaList, userId);

        doReturn(locationJoin).when(rootMock).join(Place_.LOCATION, JoinType.LEFT);
        doReturn(categoryJoin).when(rootMock).join(Place_.CATEGORY, JoinType.LEFT);
        doReturn(favoritePlaceJoin).when(rootMock).join(Place_.FAVORITE_PLACES);
        doReturn(favoritePlaceUserJoin).when(favoritePlaceJoin).join(FavoritePlace_.USER);
        doReturn(stringPathMock).when(rootMock).get(Place_.NAME);
        doReturn(stringPathMock).when(rootMock).get(Place_.DESCRIPTION);
        doReturn(stringPathMock).when(locationJoin).get(Location_.ADDRESS_EN);
        doReturn(stringPathMock).when(locationJoin).get(Location_.ADDRESS_UK);
        doReturn(stringPathMock).when(categoryJoin).get(Category_.NAME_EN);
        doReturn(stringPathMock).when(categoryJoin).get(Category_.NAME_UK);
        doReturn(longPathMock).when(favoritePlaceUserJoin).get(User_.ID);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilderMock.or(any(Predicate[].class))).thenReturn(expected);
        when(criteriaBuilderMock.lower(stringPathMock)).thenReturn(stringPathMock);
        when(criteriaBuilderMock.like(stringPathMock, '%' + place + '%')).thenReturn(expected);
        when(criteriaBuilderMock.equal(longPathMock, userId)).thenReturn(expected);
        when(criteriaBuilderMock.notEqual(longPathMock, userId)).thenReturn(expected);

        Predicate predicate = placeSearchSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).distinct(true);
    }

    @Test
    void toPredicateWithAllEmptyValuesTest() {
        String emptyValue = "";
        List<SearchCriteria> searchCriteriaList = createSearchCriteriaList(emptyValue, null);
        placeSearchSpecification = new PlaceSearchSpecification(searchCriteriaList, userId);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.and(expected, expected)).thenReturn(expected);

        Predicate predicate = placeSearchSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).distinct(true);
    }

    @Test
    void toPredicateWithEmptyCriteriaTest() {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        placeSearchSpecification = new PlaceSearchSpecification(searchCriteriaList, userId);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);

        Predicate predicate = placeSearchSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).distinct(true);
        verify(criteriaBuilderMock, times(1)).conjunction();
    }

    private List<SearchCriteria> createSearchCriteriaList(String places, Boolean isFavorite) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        SpecificationTestUtils.setValue(searchCriteriaList, "places", places);
        SpecificationTestUtils.setValue(searchCriteriaList, "isFavorite", isFavorite);
        return searchCriteriaList;
    }
}
