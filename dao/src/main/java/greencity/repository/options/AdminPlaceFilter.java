package greencity.repository.options;

import greencity.dto.place.FilterAdminPlaceDto;
import greencity.entity.Place;
import greencity.enums.PlaceStatus;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class AdminPlaceFilter implements Specification<Place> {
    private final transient FilterAdminPlaceDto filterDto;

    @Override
    public Predicate toPredicate(Root<Place> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        query.groupBy(root.get("id"));
        List<Predicate> predicates = new ArrayList<>();

        if (filterDto != null) {
            addPredicate(predicates, hasId(root, cb, filterDto.getId()));
            addPredicate(predicates, hasName(root, cb, filterDto.getName()));
            addPredicate(predicates, hasStatus(root, cb, filterDto.getStatus()));
            addPredicate(predicates, hasAuthor(root, cb, filterDto.getAuthor()));
            addPredicate(predicates, hasAddress(root, cb, filterDto.getAddress()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void addPredicate(List<Predicate> predicates, Predicate predicate) {
        if (predicate != null) {
            predicates.add(predicate);
        }
    }

    private Predicate hasId(Root<Place> r, CriteriaBuilder cb, String id) {
        if (id == null || id.isEmpty()) {
            return cb.conjunction();
        }
        return cb.equal(r.get("id"), id);
    }

    private Predicate hasName(Root<Place> r, CriteriaBuilder cb, String name) {
        if (name == null || name.isEmpty()) {
            return cb.conjunction();
        }
        return cb.like(r.get("name"), "%" + name + "%");
    }

    private Predicate hasStatus(Root<Place> r, CriteriaBuilder cb, String status) {
        if (status == null || status.isEmpty()) {
            return cb.conjunction();
        }
        return cb.equal(r.get("status"), PlaceStatus.valueOf(status.toUpperCase()));
    }

    private Predicate hasAuthor(Root<Place> r, CriteriaBuilder cb, String author) {
        if (author == null || author.isEmpty()) {
            return cb.conjunction();
        }
        return cb.like(r.join("author").get("email"), "%" + author + "%");
    }

    private Predicate hasAddress(Root<Place> r, CriteriaBuilder cb, String address) {
        if (address == null || address.isEmpty()) {
            return cb.conjunction();
        }
        return cb.like(r.join("location").get("address"), "%" + address + "%");
    }
}
