package greencity.filters;

import greencity.ModelUtils;
import greencity.dto.econews.EcoNewsViewDto;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.dto.tag.TagViewDto;
import greencity.entity.EcoNews;
import greencity.entity.RatingStatistics_;
import greencity.entity.Tag;
import java.util.Map;
import org.apache.commons.lang3.function.TriFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.criteria.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MySpecificationTest {
    @Mock
    private Root<Tag> root;
    @Mock
    private Root<EcoNews> newsRoot;
    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    private Predicate expected;
    @Mock
    private Path<Object> objectPath;
    @Mock
    private Expression<String> as;
    @Mock
    private SearchCriteria searchCriteriaEmpty;
    @Mock
    private Map<String, TriFunction<Root<Tag>, CriteriaBuilder, SearchCriteria, Predicate>> predicatesMapping;
    TagSpecification tagSpecification;
    List<SearchCriteria> searchCriteriaList;
    TagViewDto tagViewDto;
    EcoNewsViewDto ecoNewsViewDto;
    EcoNewsSpecification ecoNewsSpecification;
    SearchCriteria searchCriteriaForAll;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        ecoNewsViewDto = ModelUtils.getEcoNewsViewDto();
        tagViewDto = ModelUtils.getTagViewDto();
        searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(searchCriteriaForAll = SearchCriteria.builder()
            .key("id")
            .type("id")
            .value(tagViewDto.getId())
            .build());
        tagSpecification = new TagSpecification(searchCriteriaList);
        ecoNewsSpecification = new EcoNewsSpecification(searchCriteriaList);
    }

    @Test
    void getDataRangePredicate() {
        RatingStatisticsViewDto ratingStatisticsViewDto =
            new RatingStatisticsViewDto("2", "COMMENT_OR_REPLY", "1", "", "2021-01-12", "2021-01-13", "", "50");
        SearchCriteria onlyThisMethod = SearchCriteria.builder()
            .key(RatingStatistics_.CREATE_DATE)
            .type("dateRange")
            .value(new String[] {ratingStatisticsViewDto.getStartDate(), ratingStatisticsViewDto.getEndDate()})
            .build();
        when(newsRoot.get(onlyThisMethod.getKey())).thenReturn(objectPath);
        when(criteriaBuilder.between(any(), any(ZonedDateTime.class), any(ZonedDateTime.class)))
            .thenThrow(DateTimeParseException.class).thenReturn(expected);
        when(criteriaBuilder.disjunction()).thenReturn(expected);
        Predicate actual = ecoNewsSpecification.getDataRangePredicate(newsRoot, criteriaBuilder, onlyThisMethod);
        assertEquals(expected, actual);
    }

    @Test
    void getNumericPredicate() {
        when(root.get(searchCriteriaForAll.getKey())).thenReturn(objectPath);
        when(criteriaBuilder.equal(objectPath, searchCriteriaForAll.getValue()))
            .thenThrow(NumberFormatException.class);
        when(criteriaBuilder.disjunction()).thenReturn(expected);
        Predicate actual = tagSpecification.getNumericPredicate(root, criteriaBuilder, searchCriteriaForAll);
        assertEquals(expected, actual);
    }

    @Test
    void getNumericPredicateEmptyValue() {
        when(searchCriteriaEmpty.getValue()).thenReturn("");
        when(criteriaBuilder.equal(any(), any(String.class)))
            .thenThrow(NumberFormatException.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Predicate actual = tagSpecification.getNumericPredicate(root, criteriaBuilder, searchCriteriaEmpty);
        assertEquals(expected, actual);
    }

    @Test
    void getStringPredicate() {
        when(newsRoot.get(searchCriteriaForAll.getKey())).thenReturn(objectPath);
        when(criteriaBuilder.like(any(), eq("%" + searchCriteriaForAll.getValue() + "%"))).thenReturn(expected);
        Predicate actual = ecoNewsSpecification.getStringPredicate(newsRoot, criteriaBuilder, searchCriteriaForAll);
        assertEquals(expected, actual);
    }

    @Test
    void getStringPredicateEmptyValue() {
        when(searchCriteriaEmpty.getValue()).thenReturn("");
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Predicate actual = ecoNewsSpecification.getStringPredicate(newsRoot, criteriaBuilder, searchCriteriaEmpty);
        assertEquals(expected, actual);
    }

    @Test
    void getStringPredicateNullValue() {
        when(searchCriteriaEmpty.getValue()).thenReturn(null);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Predicate actual = ecoNewsSpecification.getStringPredicate(newsRoot, criteriaBuilder, searchCriteriaEmpty);
        assertEquals(expected, actual);
    }

    @Test
    void getEnumPredicate() {
        when(root.get(searchCriteriaForAll.getKey())).thenReturn(objectPath);
        when(objectPath.as(String.class)).thenReturn(this.as);
        when(criteriaBuilder.like(as, "%" + searchCriteriaForAll.getValue() + "%")).thenReturn(expected);
        Predicate actual = tagSpecification.getEnumPredicate(root, criteriaBuilder, searchCriteriaForAll);
        assertEquals(expected, actual);
    }

    @Test
    void getEnumPredicateEmptyValue() {
        when(searchCriteriaEmpty.getValue()).thenReturn("");
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Predicate actual = tagSpecification.getEnumPredicate(root, criteriaBuilder, searchCriteriaEmpty);
        assertEquals(expected, actual);
    }

    @Test
    void getEnumPredicateNullValue() {
        when(searchCriteriaEmpty.getValue()).thenReturn(null);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Predicate actual = tagSpecification.getEnumPredicate(root, criteriaBuilder, searchCriteriaEmpty);
        assertEquals(expected, actual);
    }

    @Test
    void getBooleanPredicate() {
        when(newsRoot.get(searchCriteriaForAll.getKey())).thenReturn(objectPath);
        String searchCriteriaValue = (String) searchCriteriaForAll.getValue();
        when(criteriaBuilder.equal(any(), eq(Boolean.parseBoolean(searchCriteriaValue)))).thenReturn(expected);
        Predicate actual = ecoNewsSpecification.getBooleanPredicate(newsRoot, criteriaBuilder, searchCriteriaForAll);
        assertEquals(expected, actual);
    }

    @Test
    void getBooleanPredicateEmptyValue() {
        when(searchCriteriaEmpty.getValue()).thenReturn("");
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Predicate actual = ecoNewsSpecification.getBooleanPredicate(newsRoot, criteriaBuilder, searchCriteriaEmpty);
        assertEquals(expected, actual);
    }

    @Test
    void toPredicateFromMap() {
        SearchCriteria sc1 = SearchCriteria.builder()
            .key("id")
            .type("numeric")
            .value("1")
            .build();
        SearchCriteria sc2 = SearchCriteria.builder()
            .key("name")
            .type("string")
            .value("test")
            .build();
        List<SearchCriteria> testList = List.of(sc1, sc2);

        TriFunction<Root<Tag>, CriteriaBuilder, SearchCriteria, Predicate> func1 = mock(TriFunction.class);
        TriFunction<Root<Tag>, CriteriaBuilder, SearchCriteria, Predicate> func2 = mock(TriFunction.class);

        when(predicatesMapping.get("numeric")).thenReturn(func1);
        when(predicatesMapping.get("string")).thenReturn(func2);

        Predicate conj = mock(Predicate.class);
        Predicate p1 = mock(Predicate.class);
        Predicate p2 = mock(Predicate.class);
        Predicate and1 = mock(Predicate.class);
        Predicate and2 = mock(Predicate.class);

        when(criteriaBuilder.conjunction()).thenReturn(conj);
        when(func1.apply(root, criteriaBuilder, sc1)).thenReturn(p1);
        when(func2.apply(root, criteriaBuilder, sc2)).thenReturn(p2);
        when(criteriaBuilder.and(conj, p1)).thenReturn(and1);
        when(criteriaBuilder.and(and1, p2)).thenReturn(and2);

        Predicate actual = tagSpecification.toPredicateFromMap(root, criteriaBuilder, testList, predicatesMapping);

        assertEquals(and2, actual);
    }

    @Test
    void toPredicateFromMapWithUnknownType() {
        SearchCriteria sc1 = SearchCriteria.builder()
            .key("id")
            .type("unknown")
            .value("1")
            .build();
        List<SearchCriteria> testList = List.of(sc1);

        when(predicatesMapping.get("unknown")).thenReturn(null);

        Predicate conj = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conj);

        Predicate actual = tagSpecification.toPredicateFromMap(root, criteriaBuilder, testList, predicatesMapping);

        assertEquals(conj, actual);
    }

    @Test
    void toPredicateFromMapWithNullOrEmptyList() {
        Predicate conj = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conj);

        Predicate actual = tagSpecification.toPredicateFromMap(root, criteriaBuilder, null,
            predicatesMapping);

        assertEquals(conj, actual);

        actual = tagSpecification.toPredicateFromMap(root, criteriaBuilder, List.of(), predicatesMapping);

        assertEquals(conj, actual);
    }

    @Test
    void toPredicateFromMapWithNullOrEmptyPredicatesMap() {
        SearchCriteria sc1 = SearchCriteria.builder()
            .key("id")
            .type("unknown")
            .value("1")
            .build();
        List<SearchCriteria> testList = List.of(sc1);

        Predicate conj = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conj);

        Predicate actual = tagSpecification.toPredicateFromMap(root, criteriaBuilder, testList, null);

        assertEquals(conj, actual);

        actual = tagSpecification.toPredicateFromMap(root, criteriaBuilder, testList, Map.of());

        assertEquals(conj, actual);
    }
}