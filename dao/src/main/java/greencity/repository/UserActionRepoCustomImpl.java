package greencity.repository;

import greencity.entity.UserAction;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;

@Repository
public class UserActionRepoCustomImpl implements UserActionRepoCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Integer findActionCountAccordToCategory(String category, Long userId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = criteriaBuilder.createQuery(Integer.class);
        Root<UserAction> userActionRoot = query.from(UserAction.class);
        query.select(userActionRoot.get(category))
            .where(criteriaBuilder.equal(userActionRoot.get("user").get("id"), userId));
        return entityManager.createQuery(query).getSingleResult();
    }
}
