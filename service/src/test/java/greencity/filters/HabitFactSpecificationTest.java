package greencity.filters;

import greencity.ModelUtils;
import greencity.dto.habitfact.HabitFactDto;
import greencity.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static greencity.entity.HabitFactTranslation_.content;

@ExtendWith(MockitoExtension.class)
public class HabitFactSpecificationTest {
    @Mock
    Join<HabitFact, Habit> habitFactHabitJoin;
    @Mock
    Root<HabitFact> root;
    @Mock
    Root<HabitFactTranslation> habitFactTranslationRoot;
    @Mock
    CriteriaQuery<Habit> criteriaQuery;
    @Mock
    CriteriaBuilder criteriaBuilder;
    @Mock
    Predicate expected;
    @Mock
    Path<Object> objectPath;
    @Mock
    Path<Long> longPath;
    @Mock
    Path<String> stringPath;
    HabitFactSpecification habitFactSpecification;
    private List<SearchCriteria> searchCriteriaList;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        HabitFactDto habitFactDto = ModelUtils.getHabitFactDto();
        searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(SearchCriteria.builder()
            .key("id")
            .type("id")
            .value(habitFactDto.getId())
            .build());
        searchCriteriaList.add(SearchCriteria.builder()
            .key("habitId")
            .type("habitId")
            .value(habitFactDto.getHabit().getId())
            .build());
        searchCriteriaList.add(SearchCriteria.builder()
            .key("content")
            .type("content")
            .value(habitFactDto.getContent())
            .build());
        habitFactSpecification = new HabitFactSpecification(searchCriteriaList);
    }

    @Test
    void toPredicate() {
        HabitFactDto habitFactDto = ModelUtils.getHabitFactDto();
        SearchCriteria build = SearchCriteria.builder()
            .key("id")
            .type("id")
            .value(habitFactDto.getId())
            .build();
        SearchCriteria build1 = SearchCriteria.builder()
            .key("habitId")
            .type("habitId")
            .value(habitFactDto.getHabit().getId())
            .build();
        SearchCriteria build2 = SearchCriteria.builder()
            .key("content")
            .type("content")
            .value(habitFactDto.getContent())
            .build();
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(root.get(build.getKey())).thenReturn(objectPath);
        when(criteriaBuilder.equal(objectPath, build.getValue())).thenThrow(NumberFormatException.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(criteriaBuilder.disjunction()).thenReturn(expected);
        when(root.join(HabitFact_.habit)).thenReturn(habitFactHabitJoin);
        when(habitFactHabitJoin.get(Habit_.id)).thenReturn(longPath);
        when(criteriaBuilder.equal(longPath, build1.getValue())).thenReturn(expected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);
        when(criteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(habitFactTranslationRoot.get(content)).thenReturn(stringPath);
        when(criteriaBuilder.like(stringPath, "%" + build2.getValue() + "%")).thenReturn(expected);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact).get(HabitFact_.id)).thenReturn(longPath);
        when(root.get(HabitFact_.id)).thenReturn(longPath);
        when(criteriaBuilder.equal(longPath, longPath)).thenReturn(expected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);
        Predicate actual = habitFactSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);
        assertEquals(expected, actual);
    }
}