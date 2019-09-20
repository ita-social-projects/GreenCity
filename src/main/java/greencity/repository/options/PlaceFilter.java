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

/**
 * The class implements {@link Specification}. Each constructor
 * takes a {@code DTO} class the type of which determines the further
 * creation of a new {@link Predicate} object.
 *
 * @author Roman Zahouri, Nazar Stasyuk
 */
public class PlaceFilter implements Specification<Place> {
    private FilterPlaceDto filterPlaceDto;
    private MapBoundsDto mapBoundsDto;

    /**
     * The constructor takes {@link FilterPlaceDto} object.
     *
     * @param filterPlaceDto object contains fields to filter by.
     */
    public PlaceFilter(FilterPlaceDto filterPlaceDto) {
        this.filterPlaceDto = filterPlaceDto;
    }

    /**
     * The constructor takes {@link MapBoundsDto} object.
     *
     * @param mapBoundsDto object contains fields to filter by.
     */
    public PlaceFilter(MapBoundsDto mapBoundsDto) {
        this.mapBoundsDto = mapBoundsDto;
    }

    /**
     * {@inheritDoc}
     * Forms a list of {@link Predicate} based on type of the classes
     * initialized in the constructors.
     */
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

    /**
     * Returns a predicate where {@link PlaceStatus} of {@link Place}
     * is equal to {@param status} value.
     *
     * @param r      must not be {@literal null}.
     * @param cb     must not be {@literal null}.
     * @param status of {@link Place} to filter by.
     * @return a {@link Predicate}, may be {@literal null}.
     */
    private Predicate hasStatus(Root<Place> r, CriteriaBuilder cb, PlaceStatus status) {
        if (status == null) {
            status = PlaceStatus.APPROVED;
        }
        return cb.equal(r.get("status"), status);
    }

    /**
     * Returns a predicate where {@link greencity.entity.Location}'s
     * lat and lng are in bounds of {@param bounds}.
     *
     * @param r      must not be {@literal null}.
     * @param cb     must not be {@literal null}.
     * @param bounds dto should contain lat and lng bounds values.
     * @return a {@link Predicate}, may be {@literal null}.
     */
    private Predicate hasPositionInBounds(Root<Place> r, CriteriaBuilder cb, MapBoundsDto bounds) {
        if (bounds == null) {
            return cb.conjunction();
        }
        return cb.and(
            cb.between(r.join("location").get("lat"), bounds.getSouthWestLat(), bounds.getNorthEastLat()),
            cb.between(r.join("location").get("lng"), bounds.getSouthWestLng(), bounds.getNorthEastLng()));
    }

    /**
     * Returns a predicate where {@link Place} has some values defined
     * in the incoming {@link FilterDiscountDto} object.
     *
     * @param r        must not be {@literal null}.
     * @param cb       must not be {@literal null}.
     * @param discount a dto describes information about discount of a {@link Place}.
     * @return a {@link Predicate}, may be {@literal null}.
     */
    private Predicate hasDiscount(Root<Place> r, CriteriaBuilder cb, FilterDiscountDto discount) {
        if (discount == null) {
            return cb.conjunction();
        }
        return cb.and(
            cb.equal(r.join("discounts").join("category").get("name"),
                discount.getCategory().getName()),
            cb.equal(r.join("discounts").join("specification").get("name"),
                discount.getSpecification().getName()),
            cb.between(r.join("discounts").get("value"),
                discount.getDiscountMin(), discount.getDiscountMax()));
    }
}
