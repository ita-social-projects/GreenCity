package greencity.repository.options;

import greencity.entity.Place;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class PlaceOptions {
    public static Specification<Place> isLatGreaterThen(Double lat) {
        return (root, query, cb) -> cb.greaterThan(root.join("location").get("lat"), lat);
    }

    public static Specification<Place> isLatLessThen(Double lat) {
        return (root, query, cb) -> cb.lessThan(root.join("location").get("lat"), lat);
    }

    public static Specification<Place> isLngGreaterThen(Double lng) {
        return (root, query, cb) -> cb.greaterThan(root.join("location").get("lat"), lng);
    }

    public static Specification<Place> isLngLessThen(Double lng) {
        return (root, query, cb) -> cb.lessThan(root.join("location").get("lat"), lng);
    }

    public static Specification<Place> hasEmail(String email) {
        return (root, query, cb) -> cb.equal(root.get("email"), email);
    }
}
