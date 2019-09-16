package greencity.repository.options;

import greencity.dto.filter.FilterDiscountDto;
import greencity.dto.filter.FilterPlaceDto;
import greencity.dto.location.MapBoundsDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class PlaceFilter {
    public static Specification<Place> getPredicates(FilterPlaceDto filterDto) {
        log.error(filterDto.toString());
        Specification<Place> predicates = Specification.where(Specification.where(hasStatus(filterDto.getStatus())));

        MapBoundsDto bounds = filterDto.getMapBoundsDto();
        if (null != bounds) {
            predicates = Specification
                .where(isLatBetween(bounds.getSouthWestLat(), bounds.getNorthEastLat()))
                .and(isLngBetween(bounds.getSouthWestLng(), bounds.getNorthEastLng()));
        }

        FilterDiscountDto discount = filterDto.getDiscountDto();
        if (null != discount) {
            predicates = Specification.where(hasDiscountBetween(discount.getDiscountMin(), discount.getDiscountMax()))
                .and(hasCategory(discount.getCategory().getName()))
                .and(hasSpecification(discount.getSpecification().getName()))
                .and(predicates);
        }
        return predicates;
    }

    private static Specification<Place> isLatBetween(Double minLat, Double maxLat) {
        return (root, query, cb) -> cb.between(root.join("location").get("lat"), minLat, maxLat);
    }

    private static Specification<Place> isLngBetween(Double minLng, Double maxLng) {
        return (root, query, cb) -> cb.between(root.join("location").get("lng"), minLng, maxLng);
    }

    private static Specification<Place> hasDiscountBetween(int discountMin, int discountMax) {
        return (root, query, cb) -> cb.between(root.join("discounts").get("value"), discountMin, discountMax);
    }

    private static Specification<Place> hasStatus(PlaceStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private static Specification<Place> hasCategory(String category) {
        return (root, query, cb) -> cb.equal(root.join("discounts")
            .join("category").get("name"), category);
    }

    private static Specification<Place> hasSpecification(String specification) {
        return (root, query, cb) -> cb.equal(root.join("discounts")
            .join("specification").get("name"), specification);
    }
}
