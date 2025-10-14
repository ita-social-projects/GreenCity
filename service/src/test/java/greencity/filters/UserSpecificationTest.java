package greencity.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import greencity.dto.user.UserManagementDto;
import greencity.entity.User;
import greencity.enums.UserStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserSpecificationTest {
    @Mock
    private Root<User> root;
    @Mock
    private CriteriaQuery<User> criteriaQuery;
    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    private Predicate expected;
    @Mock
    private Path<Object> objectPathExpected;
    @Mock
    private Expression<String> as;
    List<SearchCriteria> searchCriteriaList;
    UserSpecification userSpecification;

    @BeforeEach
    void init() {
        searchCriteriaList = new ArrayList<>();
        UserManagementDto userViewDto = UserManagementDto.builder()
            .id(1L)
            .name("test")
            .email("test@ukr.net")
            .status(UserStatus.ACTIVATED)
            .build();
        searchCriteriaList.add(SearchCriteria.builder()
            .key("id")
            .type("id")
            .value(userViewDto.getId())
            .build());
        searchCriteriaList.add(SearchCriteria.builder()
            .key("name")
            .type("name")
            .value(userViewDto.getName())
            .build());
        searchCriteriaList.add(SearchCriteria.builder()
            .key("email")
            .type("email")
            .value(userViewDto.getEmail())
            .build());
        searchCriteriaList.add(SearchCriteria.builder()
            .key("status")
            .type("status")
            .value(userViewDto.getStatus())
            .build());
        userSpecification = new UserSpecification(searchCriteriaList);
    }

    @Test
    void toPredicate() {
        when(criteriaBuilder.like(any(), any(String.class))).thenReturn(expected);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(root.get(searchCriteriaList.get(3).getKey())).thenReturn(objectPathExpected);
        when(objectPathExpected.as(String.class)).thenReturn(as);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);
        Predicate actual = userSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);
        assertEquals(expected, actual);
    }
}