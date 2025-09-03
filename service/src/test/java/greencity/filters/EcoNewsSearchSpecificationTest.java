package greencity.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import greencity.entity.EcoNews;
import greencity.entity.EcoNews_;
import greencity.entity.User;
import greencity.entity.User_;
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
class EcoNewsSearchSpecificationTest {

    @Mock
    private CriteriaQuery<?> criteriaQueryMock;
    @Mock
    private CriteriaBuilder criteriaBuilderMock;
    @Mock
    private Predicate expected;
    @Mock
    private Root<EcoNews> rootMock;
    @Mock
    private Path<String> stringPathMock;
    @Mock
    private Path<Long> longPathMock;
    @Mock
    private Join<EcoNews, User> followersJoin;

    private final Long userId = 1L;
    private EcoNewsSearchSpecification ecoNewsSearchSpecification;

    @ParameterizedTest
    @CsvSource({
        "text,true",
        "text,false"
    })
    void toPredicateTest(String text, Boolean isFavorite) {
        List<SearchCriteria> searchCriteriaList = createSearchCriteriaList(text, isFavorite);
        ecoNewsSearchSpecification = new EcoNewsSearchSpecification(searchCriteriaList, userId);

        doReturn(stringPathMock).when(rootMock).get(EcoNews_.TITLE);
        doReturn(stringPathMock).when(rootMock).get(EcoNews_.TEXT);
        doReturn(stringPathMock).when(rootMock).get(EcoNews_.SHORT_INFO);
        doReturn(followersJoin).when(rootMock).join(EcoNews_.FOLLOWERS, JoinType.LEFT);
        doReturn(longPathMock).when(followersJoin).get(User_.ID);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilderMock.like(stringPathMock, String.format("%%%s%%", text)))
            .thenReturn(expected);
        when(criteriaBuilderMock.or(expected, expected, expected)).thenReturn(expected);
        when(criteriaBuilderMock.equal(longPathMock, userId)).thenReturn(expected);
        when(criteriaBuilderMock.notEqual(longPathMock, userId)).thenReturn(expected);

        Predicate predicate = ecoNewsSearchSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).distinct(true);
    }

    @Test
    void toPredicateWithAllEmptyValuesTest() {
        String emptyValue = "";
        List<SearchCriteria> searchCriteriaList = createSearchCriteriaList(emptyValue, null);
        ecoNewsSearchSpecification = new EcoNewsSearchSpecification(searchCriteriaList, userId);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.and(expected, expected)).thenReturn(expected);

        Predicate predicate = ecoNewsSearchSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).distinct(true);
    }

    @Test
    void toPredicateWithEmptyCriteriaTest() {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        ecoNewsSearchSpecification = new EcoNewsSearchSpecification(searchCriteriaList, userId);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);

        Predicate predicate = ecoNewsSearchSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).distinct(true);
        verify(criteriaBuilderMock, times(1)).conjunction();
    }

    private List<SearchCriteria> createSearchCriteriaList(String text, Boolean isFavorite) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        SpecificationTestUtils.setValue(searchCriteriaList, "text", text);
        SpecificationTestUtils.setValue(searchCriteriaList, "isFavorite", isFavorite);
        return searchCriteriaList;
    }
}
