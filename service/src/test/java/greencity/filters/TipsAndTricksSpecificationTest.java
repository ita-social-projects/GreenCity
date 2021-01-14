package greencity.filters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import greencity.dto.tipsandtricks.TipsAndTricksViewDto;
import greencity.entity.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TipsAndTricksSpecificationTest {

    @Mock
    private CriteriaBuilder criteriaBuilderMock;

    @Mock
    private CriteriaQuery criteriaQueryMock;

    @Mock
    private Predicate predicateMock;

    @Mock
    private Root<TipsAndTricks> tipsAndTricksRootMock;

    @Mock
    private Path<Object> pathTipsAndTricksIdMock;

    @Mock
    private Path<Long> pathTipsAndTricksIdLongMock;

    @Mock
    private Path<Object> pathTipsAndTricksCreationDateMock;

    @Mock
    private Path<String> pathTranslationContentMock;

    @Mock
    private Path<TipsAndTricks> pathTipsAndTricksMock;

    @Mock
    private Path<Long> pathTitleTranslationsTipsAndTricksIdMock;

    @Mock
    private Predicate andIdNumericPredicate;

    @Mock
    private Predicate contentPredicate;

    @Mock
    private Predicate idPredicate;

    @Mock
    private Predicate andDataRangePredicate;

    @Mock
    private Predicate andTitleTranslationPredicate;

    @Mock
    private Root<TitleTranslation> titleTranslationRootMock;

    @Mock
    private SingularAttribute<Translation, String> content;

    @Mock
    private SingularAttribute<TipsAndTricks, Long> id;

    @Mock
    private SingularAttribute<TitleTranslation, TipsAndTricks> tipsAndTricks;

    private TipsAndTricksSpecification tipsAndTricksSpecification;

    private List<SearchCriteria> criteriaList;

    @BeforeEach
    void setUp() {
        TipsAndTricksViewDto tipsAndTricksViewDto =
            new TipsAndTricksViewDto("2", "titleTranslation", "", "2021-01-12", "2021-01-13");

        criteriaList = new ArrayList<>();

        criteriaList.add(
            SearchCriteria.builder()
                .key(TipsAndTricks_.ID)
                .type(TipsAndTricks_.ID)
                .value(tipsAndTricksViewDto.getId())
                .build()
        );
        criteriaList.add(
            SearchCriteria.builder()
                .key(TipsAndTricks_.TITLE_TRANSLATIONS)
                .type(TipsAndTricks_.TITLE_TRANSLATIONS)
                .value(tipsAndTricksViewDto.getTitleTranslations())
                .build()
        );
        criteriaList.add(
            SearchCriteria.builder()
                .key("creationDate")
                .type("dateRange")
                .value(new String[] {tipsAndTricksViewDto.getStartDate(), tipsAndTricksViewDto.getEndDate()})
                .build()
        );

        Translation_.content = content;
        TipsAndTricks_.id = id;
        TitleTranslation_.tipsAndTricks = tipsAndTricks;

        tipsAndTricksSpecification = new TipsAndTricksSpecification(criteriaList);
    }

    @Test
    void toPredicate() {

        when(criteriaBuilderMock.conjunction()).thenReturn(predicateMock);

        when(tipsAndTricksRootMock.get(TipsAndTricks_.ID)).thenReturn(pathTipsAndTricksIdMock);

        when(criteriaBuilderMock.equal(pathTipsAndTricksIdMock, criteriaList.get(0).getValue())).thenReturn(andIdNumericPredicate);

        when(criteriaBuilderMock.and(predicateMock, andIdNumericPredicate)).thenReturn(andIdNumericPredicate);

        when(criteriaQueryMock.from(TitleTranslation.class)).thenReturn(titleTranslationRootMock);

        when(titleTranslationRootMock.get(content)).thenReturn(pathTranslationContentMock);

        when(criteriaBuilderMock.like(pathTranslationContentMock, "%" + criteriaList.get(1).getValue() + "%")).thenReturn(contentPredicate);

        when(titleTranslationRootMock.get(TitleTranslation_.tipsAndTricks)).thenReturn(pathTipsAndTricksMock);

        when(pathTipsAndTricksMock.get(TipsAndTricks_.id)).thenReturn(pathTitleTranslationsTipsAndTricksIdMock);

        when(tipsAndTricksRootMock.get(TipsAndTricks_.id)).thenReturn(pathTipsAndTricksIdLongMock);

        when(criteriaBuilderMock.equal(pathTitleTranslationsTipsAndTricksIdMock, pathTipsAndTricksIdLongMock)).thenReturn(idPredicate);

        when(criteriaBuilderMock.and(contentPredicate, idPredicate)).thenReturn(andTitleTranslationPredicate);

        when(criteriaBuilderMock.and(andIdNumericPredicate, andTitleTranslationPredicate)).thenReturn(
            andTitleTranslationPredicate);

        when(tipsAndTricksRootMock.get("creationDate")).thenReturn(pathTipsAndTricksCreationDateMock);

        when(criteriaBuilderMock.between(eq(tipsAndTricksRootMock.get("creationDate")), any(ZonedDateTime.class), any(ZonedDateTime.class))).thenReturn(andDataRangePredicate);

        when(criteriaBuilderMock.and(andTitleTranslationPredicate, andDataRangePredicate)).thenReturn(andDataRangePredicate);

        tipsAndTricksSpecification.toPredicate(tipsAndTricksRootMock, criteriaQueryMock, criteriaBuilderMock);

        verify(tipsAndTricksRootMock, never()).get(TipsAndTricks_.AUTHOR);
        verify(criteriaBuilderMock).and(predicateMock, andIdNumericPredicate);
        verify(criteriaBuilderMock).and(contentPredicate, idPredicate);
        verify(criteriaBuilderMock).and(andIdNumericPredicate, andTitleTranslationPredicate);
        verify(criteriaBuilderMock).and(andTitleTranslationPredicate, andDataRangePredicate);
    }
}