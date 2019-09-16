package greencity.repository.options;

import greencity.dto.filter.FilterDiscountDto;
import greencity.dto.filter.FilterPlaceDto;
import greencity.dto.location.MapBoundsDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class PlaceFilter implements Specification<Place> {
    private FilterPlaceDto filterPlaceDto;
    private MapBoundsDto mapBoundsDto;

    public PlaceFilter(FilterPlaceDto filterPlaceDto) {
        this.filterPlaceDto = filterPlaceDto;
    }

    public PlaceFilter(MapBoundsDto mapBoundsDto) {
        this.mapBoundsDto = mapBoundsDto;
    }

    @Override
    public Predicate toPredicate(Root<Place> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (null != filterPlaceDto) {
            predicates.add(hasStatus(root, criteriaBuilder, filterPlaceDto.getStatus()));
            predicates.add(hasPositionInBounds(root, criteriaBuilder, filterPlaceDto.getMapBoundsDto()));
            predicates.add(hasDiscount(root, criteriaBuilder, filterPlaceDto.getDiscountDto()));
        }

        if (null != mapBoundsDto) {
            predicates.add(hasStatus(root, criteriaBuilder, PlaceStatus.APPROVED));
            predicates.add(hasPositionInBounds(root, criteriaBuilder, mapBoundsDto));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate hasStatus(Root<Place> r, CriteriaBuilder cb, PlaceStatus status) {
        if (status == null) {
            status = PlaceStatus.APPROVED;
        }
        return cb.equal(r.get("status"), status);
    }

    private Predicate hasPositionInBounds(Root<Place> r, CriteriaBuilder cb, MapBoundsDto bounds) {
        if (bounds == null) {
            return cb.conjunction();
        }
        return cb.and(
            cb.between(r.join("location").get("lat"), bounds.getSouthWestLat(), bounds.getNorthEastLat()),
            cb.between(r.join("location").get("lng"), bounds.getSouthWestLng(), bounds.getNorthEastLng()));
    }

    private Predicate hasDiscount(Root<Place> r, CriteriaBuilder cb, FilterDiscountDto disc) {
        if (disc == null) {
            return cb.conjunction();
        }
        return cb.and(
            cb.equal(r.join("discounts").join("category").get("name"),
                disc.getCategory().getName()),
            cb.equal(r.join("discounts").join("specification").get("name"),
                disc.getSpecification().getName()),
            cb.between(r.join("discounts").get("value"),
                disc.getDiscountMin(), disc.getDiscountMax()));
    }
}
