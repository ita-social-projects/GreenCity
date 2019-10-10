package greencity.repository.options;

import greencity.constant.AppConstant;
import greencity.dto.filter.FilterDiscountDto;
import greencity.dto.filter.FilterPlaceDto;
import greencity.dto.location.MapBoundsDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    /**
     * The constructor takes {@link FilterPlaceDto} object.
     *
     * @param filterPlaceDto object contains fields to filter by.
     */
    public PlaceFilter(FilterPlaceDto filterPlaceDto) {
        this.filterPlaceDto = filterPlaceDto;
    }

    /**
     * {@inheritDoc}
     * Forms a list of {@link Predicate} based on type of the classes
     * initialized in the constructors.
     */
    @Override
    public Predicate toPredicate(Root<Place> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        query.groupBy(root.get("id"));
        List<Predicate> predicates = new ArrayList<>();
        if (null != filterPlaceDto) {
            predicates.add(hasStatus(root, cb, filterPlaceDto.getStatus()));
            predicates.add(hasPositionInBounds(root, cb, filterPlaceDto.getMapBoundsDto()));
            predicates.add(hasDiscount(root, cb, filterPlaceDto.getDiscountDto()));
            predicates.add(isNowOpen(root, cb, filterPlaceDto.getTime()));
            predicates.add(hasFieldLike(root, cb, filterPlaceDto.getSearchReg(), filterPlaceDto.getStatus()));
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }

    /**
     * Returns a predicate where {@link PlaceStatus} of {@link Place}
     * is equal to {@param status} value.
     *
     * @param r      must not be {@literal null}.
     * @param cb     must not be {@literal null}.
     * @param status of {@link Place} to filter by.
     * @return a {@link Predicate}, may be {@literal null}.
     * @author Roman Zahouri
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
     * @author Roman Zahouri
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
     * Checks if {@link Place} is open at the time described in the {@code currentTime} string argument.
     * The method can throw a {@link DateTimeParseException} if the {@code currentTime} string doesn't
     * match a {@code AppConstant.DATE_FORMAT} format string.
     *
     * @param r           must not be {@literal null}.
     * @param cb          must not be {@literal null}.
     * @param currentTime a string contains current date and time.
     * @author Roman Zahouri
     */
    private Predicate isNowOpen(Root<Place> r, CriteriaBuilder cb, String currentTime) {
        if (null == currentTime) {
            return cb.conjunction();
        }
        LocalDateTime time = LocalDateTime.parse(currentTime, DateTimeFormatter.ofPattern(AppConstant.DATE_FORMAT));
        return cb.and(cb.equal(r.join("openingHoursList").get("weekDay"), time.getDayOfWeek()),
            cb.lessThan(r.join("openingHoursList").get("openTime"), time.toLocalTime()),
            cb.greaterThan(r.join("openingHoursList").get("closeTime"), time.toLocalTime()));
    }

    /**
     * Returns a predicate where {@link Place} has some values defined
     * in the incoming {@link FilterDiscountDto} object.
     *
     * @param r        must not be {@literal null}.
     * @param cb       must not be {@literal null}.
     * @param discount a dto describes information about discount of a {@link Place}.
     * @return a {@link Predicate}, may be {@literal null}.
     * @author Roman Zahouri
     */
    private Predicate hasDiscount(Root<Place> r, CriteriaBuilder cb, FilterDiscountDto discount) {
        if (discount == null) {
            return cb.conjunction();
        }
        return cb.and(
            cb.equal(r.join("discountValues").join("specification").get("name"),
                discount.getSpecification().getName()),
            cb.between(r.join("discountValues").get("value"),
                discount.getDiscountMin(), discount.getDiscountMax()));
    }

    /**
     * Returns a predicate where {@link Place} has some values defined
     * in the incoming {@link FilterDiscountDto} object.
     *
     * @param r  must not be {@literal null}.
     * @param cb must not be {@literal null}.
     * @return a {@link Predicate}, may be {@literal null}.
     * @author Rostyslav Khasanov
     */
    private Predicate hasFieldLike(Root<Place> r, CriteriaBuilder cb, String reg, PlaceStatus status) {
        if (filterPlaceDto.getSearchReg() == null) {
            return cb.conjunction();
        }
        return cb.and(cb.or(
            cb.like(r.join("author").get("email"), reg),
            cb.like(r.join("category").get("name"), reg),
            cb.like(r.get("name"), reg),
            cb.like(r.join("location").get("address"), reg),
            cb.like(r.get("modifiedDate").as(String.class), reg)), cb.equal(r.get("status"), status));
    }
}
