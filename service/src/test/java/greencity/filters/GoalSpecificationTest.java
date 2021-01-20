package greencity.filters;

import greencity.dto.goal.GoalDto;
import greencity.entity.Goal;
import greencity.entity.Goal_;
import greencity.entity.Translation;
import static greencity.entity.Translation_.content;
import greencity.entity.localization.GoalTranslation;
import greencity.entity.localization.GoalTranslation_;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class GoalSpecificationTest {

    @Mock
    private CriteriaQuery<?> criteriaQueryMock;
    @Mock
    private CriteriaBuilder criteriaBuilderMock;
    @Mock
    private Predicate expected;
    @Mock
    private Root<GoalTranslation> goalTranslationRootMock;
    @Mock
    private Root<Goal> rootGoalMock;
    @Mock
    private Path<Object> objectPath;
    @Mock
    private Path<String> stringPath;
    @Mock
    private Path<Long> longPathMock;
    @Mock
    private Path<Goal> goalPath;
    @Mock
    private SingularAttribute<Goal,Long> id;
    @Mock
    private SingularAttribute<GoalTranslation,Goal> goal;
    @Mock
    private SingularAttribute<Translation,String> contentAttr;

    private GoalSpecification goalSpecification;

    @Mock
    private List<SearchCriteria> searchCriteriaList;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        GoalDto goalDto = new GoalDto();
        goalDto.setId(1L);
        goalDto.setText("content");
        searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(SearchCriteria.builder()
            .key("id")
            .type("id")
            .value(goalDto.getId())
            .build());
        searchCriteriaList.add(SearchCriteria.builder()
            .key("content")
            .type("content")
            .value(goalDto.getText())
            .build());
        GoalTranslation_.goal = goal;
        Goal_.id = id;
        content = contentAttr;
        goalSpecification = new GoalSpecification(searchCriteriaList);
    }

    @Test
    void toPredicate() {
        GoalDto goalDto = new GoalDto();
        goalDto.setId(1L);
        goalDto.setText("content");
        SearchCriteria buildId = SearchCriteria.builder()
            .key("id")
            .type("id")
            .value(goalDto.getId())
            .build();
        SearchCriteria buildContent = SearchCriteria.builder()
            .key("content")
            .type("content")
            .value(goalDto.getText())
            .build();
        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(rootGoalMock.get(buildId.getKey())).thenReturn(objectPath);
        when(criteriaBuilderMock.equal(objectPath, buildId.getValue())).thenThrow(NumberFormatException.class);
        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.disjunction()).thenReturn(expected);
        when(criteriaQueryMock.from(GoalTranslation.class)).thenReturn(goalTranslationRootMock);
        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(goalTranslationRootMock.get(content)).thenReturn(stringPath);
        when(criteriaBuilderMock.like(stringPath, "%" + buildContent.getValue() + "%")).thenReturn(expected);
        when(goalTranslationRootMock.get(GoalTranslation_.goal)).thenReturn(goalPath);
        when(goalPath.get(Goal_.id)).thenReturn(longPathMock);
        when(rootGoalMock.get(Goal_.id)).thenReturn(longPathMock);
        when(criteriaBuilderMock.equal(longPathMock,longPathMock)).thenReturn(expected);
        when(criteriaBuilderMock.and(expected,expected)).thenReturn(expected);
        Predicate predicate = goalSpecification.toPredicate(rootGoalMock,criteriaQueryMock,criteriaBuilderMock);
        assertEquals(expected,predicate);

    }
}