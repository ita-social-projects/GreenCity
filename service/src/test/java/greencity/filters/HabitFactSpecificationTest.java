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

import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitFactSpecificationTest {
    @Mock
    private Join<HabitFact, Habit> habitFactHabitJoin;
    @Mock
    private Root<HabitFact> root;
    @Mock
    private Root<HabitFactTranslation> habitFactTranslationRoot;
    @Mock
    private CriteriaQuery<Habit> criteriaQuery;
    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    private Predicate expected;
    @Mock
    private Path<Object> objectPath;
    @Mock
    private Path<Long> longPath;
    @Mock
    private Path<String> stringPath;
    @Mock
    private Path<HabitFact> pathHabitFact;
    @Mock
    private SingularAttribute<HabitFact, Habit> habit;
    @Mock
    private SingularAttribute<Habit, Long> id;
    @Mock
    private SingularAttribute<HabitFactTranslation, HabitFact> habitFact;
    @Mock
    private SingularAttribute<HabitFact, Long> habitId;
    @Mock
    private SingularAttribute<Translation, String> content;
    HabitFactSpecification habitFactSpecification;
    private List<SearchCriteria> searchCriteriaList;

    @BeforeEach
    void init() {
        HabitFactTranslation_.content = content;
        HabitFactTranslation_.habitFact = habitFact;
        HabitFact_.id = habitId;
        HabitFact_.habit = habit;
        Habit_.id = id;
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
        when(habitFactTranslationRoot.get(HabitFactTranslation_.content)).thenReturn(stringPath);
        when(criteriaBuilder.like(stringPath, "%" + build2.getValue() + "%")).thenReturn(expected);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact)).thenReturn(pathHabitFact);
        when(pathHabitFact.get(HabitFact_.id)).thenReturn(longPath);
        when(root.get(HabitFact_.id)).thenReturn(longPath);
        when(criteriaBuilder.equal(longPath, longPath)).thenReturn(expected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);
        Predicate actual = habitFactSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);
        assertEquals(expected, actual);
    }
}