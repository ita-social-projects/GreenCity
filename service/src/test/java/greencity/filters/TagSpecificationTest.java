package greencity.filters;

import greencity.ModelUtils;
import greencity.dto.tag.TagViewDto;
import greencity.entity.Tag;
import greencity.entity.Tag_;
import greencity.entity.localization.TagTranslation_;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TagSpecificationTest {
    @Mock
    Join<Object, Object> tagTagTranslationJoin;
    @Mock
    Root<Tag> root;
    @Mock
    CriteriaQuery<Tag> criteriaQuery;
    @Mock
    CriteriaBuilder criteriaBuilder;
    @Mock
    Predicate expected;
    @Mock
    Path<Object> objectPath;
    @Mock
    Path<String> stringPath;
    @Mock
    Expression<String> as;
    TagSpecification tagSpecification;
    private List<SearchCriteria> searchCriteriaList;


    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        TagViewDto tagViewDto = ModelUtils.getTagViewDto();
        searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(tagViewDto.getId())
                .build());
        searchCriteriaList.add(SearchCriteria.builder()
                .key("type")
                .type("type")
                .value(tagViewDto.getType())
                .build());
        searchCriteriaList.add(SearchCriteria.builder()
                .key("name")
                .type("name")
                .value(tagViewDto.getName())
                .build());
        tagSpecification = new TagSpecification(searchCriteriaList);
    }

    @Test
    void toPredicate() {
        TagViewDto tagViewDto = ModelUtils.getTagViewDto();
        SearchCriteria build = SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(tagViewDto.getId())
                .build();
        SearchCriteria build1 = SearchCriteria.builder()
                .key("type")
                .type("type")
                .value(tagViewDto.getType())
                .build();
        SearchCriteria build2 = SearchCriteria.builder()
                .key("name")
                .type("name")
                .value(tagViewDto.getName())
                .build();

        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(criteriaQuery.distinct(true)).thenReturn(criteriaQuery);

        when(root.get(build.getKey())).thenReturn(objectPath);
        when(criteriaBuilder.equal(objectPath, build.getValue())).thenThrow(NumberFormatException.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(criteriaBuilder.disjunction()).thenReturn(expected);


        when(root.get(build1.getKey())).thenReturn(objectPath);
        when(objectPath.as(String.class)).thenReturn(this.as);
        when(criteriaBuilder.like(as, "%" + build1.getValue() + "%")).thenReturn(expected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);


        when(root.join(Tag_.TAG_TRANSLATIONS)).thenReturn(tagTagTranslationJoin);
        when(tagTagTranslationJoin.get(TagTranslation_.NAME)).thenReturn(objectPath);


        when(criteriaBuilder.like(stringPath, "%" + build2.getValue() + "%")).thenReturn(expected);
        when(tagTagTranslationJoin.get("name")).thenReturn(objectPath);
        when(objectPath.get(Tag_.ID)).thenReturn(objectPath);
        when(root.get(Tag_.ID)).thenReturn(objectPath);
        when(criteriaBuilder.equal(objectPath, objectPath)).thenReturn(expected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);

        Predicate actual = tagSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);
        assertEquals(expected, actual);
    }
}
