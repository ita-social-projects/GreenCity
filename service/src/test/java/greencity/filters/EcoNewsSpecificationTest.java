package greencity.filters;

import static org.mockito.Mockito.*;

import greencity.dto.econews.EcoNewsViewDto;
import greencity.entity.*;
import greencity.entity.localization.TagTranslation;
import greencity.entity.localization.TagTranslation_;

import java.time.ZonedDateTime;
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

@ExtendWith(MockitoExtension.class)
class EcoNewsSpecificationTest {

    @Mock
    private CriteriaBuilder criteriaBuilderMock;

    @Mock
    private CriteriaQuery criteriaQueryMock;

    @Mock
    private Root<EcoNews> ecoNewsRootMock;

    @Mock
    private Predicate predicateMock;

    @Mock
    private Path<Object> pathEcoNewsTitleMock;

    @Mock
    private Path<Object> pathEcoNewsTextMock;

    @Mock
    private Path<Object> pathEcoNewsAuthorMock;

    @Mock
    private Path<Object> pathEcoNewsAuthorNameMock;

    @Mock
    private Path<Object> pathEcoNewsCreationDateMock;

    @Mock
    private Predicate andTitlePredicate;

    @Mock
    private Predicate andTextPredicate;

    @Mock
    private Predicate andAuthorPredicate;

    @Mock
    private Predicate andTagPredicate;

    @Mock
    private Predicate andCreationDatePredicate;

    @Mock
    private ListAttribute<EcoNews, Tag> tags;

    @Mock
    private ListJoin<EcoNews, Tag> tagJoinMock;

    @Mock
    private ListAttribute<Tag, TagTranslation> tagTranslations;

    @Mock
    private ListJoin<Tag, TagTranslation> tagTranslationJoinMock;

    @Mock
    private SingularAttribute<TagTranslation, String> name;

    @Mock
    private Path<String> pathTagTranslationNameMock;

    private EcoNewsSpecification ecoNewsSpecification;

    private List<SearchCriteria> criteriaList;

    @BeforeEach
    void setUp() {
        EcoNewsViewDto ecoNewsViewDto =
            new EcoNewsViewDto("", "anyTitle", "anyAuthor", "weather", "2024-08-20", "", "News", "");

        criteriaList = new ArrayList<>();
        criteriaList.add(
            SearchCriteria.builder()
                .key(EcoNews_.TITLE)
                .type(EcoNews_.TITLE)
                .value(ecoNewsViewDto.getTitle())
                .build());
        criteriaList.add(
            SearchCriteria.builder()
                .key(EcoNews_.AUTHOR)
                .type(EcoNews_.AUTHOR)
                .value(ecoNewsViewDto.getAuthor())
                .build());
        criteriaList.add(
            SearchCriteria.builder()
                .key(EcoNews_.TEXT)
                .type(EcoNews_.TEXT)
                .value(ecoNewsViewDto.getText())
                .build());
        criteriaList.add(
            SearchCriteria.builder()
                .key(EcoNews_.CREATION_DATE)
                .type(EcoNews_.CREATION_DATE)
                .value(ecoNewsViewDto.getStartDate())
                .build());
        criteriaList.add(
            SearchCriteria.builder()
                .key(EcoNews_.TAGS)
                .type(EcoNews_.TAGS)
                .value(ecoNewsViewDto.getTags())
                .build());

        EcoNews_.tags = tags;
        Tag_.tagTranslations = tagTranslations;
        TagTranslation_.name = name;

        ecoNewsSpecification = new EcoNewsSpecification(criteriaList);
    }

    @Test
    void toPredicate() {

        when(criteriaBuilderMock.conjunction()).thenReturn(predicateMock);

        when(ecoNewsRootMock.get(EcoNews_.TITLE)).thenReturn(pathEcoNewsTitleMock);

        when(criteriaBuilderMock.like(ecoNewsRootMock.get(EcoNews_.TITLE),
            "%" + criteriaList.getFirst().getValue() + "%"))
                .thenReturn(andTitlePredicate);

        when(criteriaBuilderMock.and(predicateMock, andTitlePredicate)).thenReturn(andTitlePredicate);

        when(ecoNewsRootMock.get(EcoNews_.AUTHOR)).thenReturn(pathEcoNewsAuthorMock);

        when(pathEcoNewsAuthorMock.get("name")).thenReturn(pathEcoNewsAuthorNameMock);

        when(criteriaBuilderMock
            .like(ecoNewsRootMock.get(EcoNews_.AUTHOR).get("name"), "%" + criteriaList.get(1).getValue() + "%"))
                .thenReturn(andAuthorPredicate);

        when(criteriaBuilderMock.and(andTitlePredicate, andAuthorPredicate)).thenReturn(andAuthorPredicate);

        when(ecoNewsRootMock.get(EcoNews_.TEXT)).thenReturn(pathEcoNewsTextMock);

        when(criteriaBuilderMock.like(ecoNewsRootMock.get(EcoNews_.TEXT), "%" + criteriaList.get(2).getValue() + "%"))
            .thenReturn(andTextPredicate);

        when(criteriaBuilderMock.and(andAuthorPredicate, andTextPredicate)).thenReturn(andTextPredicate);

        when(ecoNewsRootMock.get(EcoNews_.CREATION_DATE)).thenReturn(pathEcoNewsCreationDateMock);

        when(criteriaBuilderMock.between(any(), any(ZonedDateTime.class), any(ZonedDateTime.class))).thenReturn(andCreationDatePredicate);

        when(criteriaBuilderMock.and(andTextPredicate, andCreationDatePredicate)).thenReturn(andCreationDatePredicate);

        when(ecoNewsRootMock.join(EcoNews_.tags)).thenReturn(tagJoinMock);

        when(tagJoinMock.join(Tag_.tagTranslations)).thenReturn(tagTranslationJoinMock);

        when(tagTranslationJoinMock.get(TagTranslation_.name)).thenReturn(pathTagTranslationNameMock);

        when(criteriaBuilderMock
            .like(pathTagTranslationNameMock.as(String.class), "%" + criteriaList.get(4).getValue() + "%"))
                .thenReturn(andTagPredicate);

        when(criteriaBuilderMock.and(List.of(andTagPredicate).toArray(new Predicate[0]))).thenReturn(andTagPredicate);

        when(criteriaBuilderMock.and(andCreationDatePredicate, andTagPredicate)).thenReturn(andTagPredicate);

        ecoNewsSpecification.toPredicate(ecoNewsRootMock, criteriaQueryMock, criteriaBuilderMock);

        verify(ecoNewsRootMock, never()).get(EcoNews_.ID);
        verify(ecoNewsRootMock, never()).get(EcoNews_.IMAGE_PATH);
        verify(ecoNewsRootMock, never()).get(EcoNews_.SOURCE);
        verify(ecoNewsRootMock, never()).get(EcoNews_.HIDDEN);
        verify(ecoNewsRootMock, never()).get("dateRange");
        verify(criteriaBuilderMock).and(predicateMock, andTitlePredicate);
        verify(criteriaBuilderMock).and(andTitlePredicate, andAuthorPredicate);
        verify(criteriaBuilderMock).and(andAuthorPredicate, andTextPredicate);
        verify(criteriaBuilderMock).and(andTextPredicate, andCreationDatePredicate);
        verify(criteriaBuilderMock).and(andCreationDatePredicate, andTagPredicate);
    }
}