package greencity.repository;

import greencity.entity.UserAction;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserActionRepoCustomImplTest {
    @InjectMocks
    UserActionRepoCustomImpl userActionRepoCustom;
    @Spy
    private EntityManager entityManager;
    @Mock
    CriteriaBuilder builder;
    @Mock
    CriteriaQuery<Integer> criteriaQuery;
    @Mock
    TypedQuery<Integer> query;
    @Mock
    Root<UserAction> userActionRoot;
    @Mock
    Predicate equal;
    @Mock
    Path<Object> user;
    @Mock
    Path<Object> x;

    @Test
    void findActionCountAccordToCategory() {
        when(entityManager.getCriteriaBuilder()).thenReturn(builder);
        when(builder.createQuery(Integer.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(UserAction.class)).thenReturn(userActionRoot);
        when(criteriaQuery.select(userActionRoot.get("EcoNewsLikes"))).thenReturn(criteriaQuery);
        when(userActionRoot.get("user")).thenReturn(user);
        when(user.get("id")).thenReturn(x);
        when(builder.equal(x, 1L)).thenReturn(equal);
        when(criteriaQuery.where(equal)).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1);
        assertEquals(1, userActionRepoCustom.findActionCountAccordToCategory("EcoNewsLikes", 1L));
    }
}
