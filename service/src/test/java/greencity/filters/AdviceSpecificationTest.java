package greencity.filters;

import static org.mockito.Mockito.*;
import greencity.dto.advice.AdviceViewDto;
import greencity.entity.*;
import greencity.entity.localization.AdviceTranslation;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static greencity.entity.localization.AdviceTranslation_.content;

@ExtendWith(MockitoExtension.class)
class AdviceSpecificationTest {

    @Mock
    private CriteriaBuilder criteriaBuilderMock;

    @Mock
    private CriteriaQuery criteriaQueryMock;

    @Mock
    private Root<Advice> adviceRootMock;

    @Mock
    private Path<Object> pathAdviceIdMock;

    @Mock
    private Path<Long> pathHabitIdMock;

    @Mock
    private Join<Advice, Habit> habitJoinMock;

    @Mock
    private ListJoin<Advice, AdviceTranslation> translationJoinMock;

    @Mock
    private Predicate predicateMock;

    @Mock
    private Predicate andIdNumericPredicate;

    @Mock
    private Predicate andHabitIdPredicate;

    @Mock
    private Predicate andTranslationPredicate;

    @Mock
    private SingularAttribute<Advice, Habit> habit;

    @Mock
    private ListAttribute<Advice, AdviceTranslation> translations;

    @Mock
    private SingularAttribute<Habit, Long> id;

    private AdviceSpecification adviceSpecification;

    private List<SearchCriteria> criteriaList;

    @BeforeEach
    void setUp() {
        AdviceViewDto adviceViewDto = new AdviceViewDto("id", "habitId", "");
        criteriaList = new ArrayList<>();
        criteriaList.add(
            SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(adviceViewDto.getId())
                .build());
        criteriaList.add(
            SearchCriteria.builder()
                .key("habitId")
                .type("habitId")
                .value(adviceViewDto.getHabitId())
                .build());
        criteriaList.add(
            SearchCriteria.builder()
                .key("translationContent")
                .type("translationContent")
                .value(adviceViewDto.getTranslationContent())
                .build());

        Advice_.habit = habit;
        Advice_.translations = translations;
        Habit_.id = id;
        adviceSpecification = new AdviceSpecification(criteriaList);
    }

    @Test
    void toPredicate() {

        when(criteriaBuilderMock.conjunction()).thenReturn(predicateMock).thenReturn(andTranslationPredicate);

        when(adviceRootMock.get(criteriaList.getFirst().getKey())).thenReturn(pathAdviceIdMock);

        when(criteriaBuilderMock.equal(pathAdviceIdMock, criteriaList.getFirst().getValue()))
            .thenReturn(andIdNumericPredicate);

        when(criteriaBuilderMock.and(predicateMock, andIdNumericPredicate)).thenReturn(andIdNumericPredicate);

        when(adviceRootMock.join(Advice_.habit)).thenReturn(habitJoinMock);

        when(habitJoinMock.get(Habit_.id)).thenReturn(pathHabitIdMock);

        when(adviceRootMock.join(Advice_.translations)).thenReturn(translationJoinMock);

        when(criteriaBuilderMock.equal(pathHabitIdMock, criteriaList.get(1).getValue()))
            .thenReturn(andHabitIdPredicate);

        when(criteriaBuilderMock.and(andIdNumericPredicate, andHabitIdPredicate)).thenReturn(andHabitIdPredicate);

        when(criteriaBuilderMock.and(andHabitIdPredicate, andTranslationPredicate)).thenReturn(andTranslationPredicate);

        adviceSpecification.toPredicate(adviceRootMock, criteriaQueryMock, criteriaBuilderMock);

        verify(adviceRootMock).get(criteriaList.getFirst().getKey());
        verify(criteriaBuilderMock).and(predicateMock, andIdNumericPredicate);
        verify(criteriaBuilderMock).and(andIdNumericPredicate, andHabitIdPredicate);
        verify(criteriaBuilderMock).and(andHabitIdPredicate, andTranslationPredicate);
        verify(criteriaBuilderMock, never()).like(translationJoinMock.get(content),
            "%" + criteriaList.get(2).getValue() + "%");
    }

}