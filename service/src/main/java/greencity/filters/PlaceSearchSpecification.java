package greencity.filters;

import greencity.entity.Category;
import greencity.entity.Category_;
import greencity.entity.FavoritePlace;
import greencity.entity.FavoritePlace_;
import greencity.entity.Location;
import greencity.entity.Location_;
import greencity.entity.Place;
import greencity.entity.Place_;
import greencity.entity.User;
import greencity.entity.User_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PlaceSearchSpecification implements MySpecification<Place> {
    protected final List<SearchCriteria> searchCriteriaList;
    protected final Long userId;

    private final Map<String, TriFunction<Root<Place>, CriteriaBuilder, SearchCriteria, Predicate>> pred =
        Map.of(
            "places", this::getPlacesLikePredicate,
            "isFavorite", this::getIsFavoritePredicate);

    @Override
    public Predicate toPredicate(@NotNull Root<Place> root,
        @NotNull CriteriaQuery<?> criteriaQuery,
        @NotNull CriteriaBuilder criteriaBuilder) {
        Predicate allPredicates = toPredicateFromMap(root, criteriaBuilder, searchCriteriaList, pred);
        criteriaQuery.distinct(true);
        return allPredicates;
    }

    private Predicate getPlacesLikePredicate(Root<Place> root, CriteriaBuilder criteriaBuilder,
                                        SearchCriteria searchCriteria) {
        String[] places = searchCriteria.getValue().toString().trim().split(" ");
        if (places.length == 0) {
            return criteriaBuilder.conjunction();
        }

        ArrayList<Predicate> placesLikePredicates = new ArrayList<>();
        Join<Place, Location> locationJoin = root.join(Place_.LOCATION, JoinType.LEFT);
        Join<Place, Category> categoryJoin = root.join(Place_.CATEGORY, JoinType.LEFT);
        Arrays.stream(places).forEach(place -> placesLikePredicates.add(
            criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get(Place_.NAME)),
                    "%" + place.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get(Place_.DESCRIPTION)),
                    "%" + place.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(locationJoin.get(Location_.ADDRESS_EN)),
                    "%" + place.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(locationJoin.get(Location_.ADDRESS_UK)),
                    "%" + place.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get(Category_.NAME_EN)),
                    "%" + place.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get(Category_.NAME_UK)),
                    "%" + place.toLowerCase() + "%"))));
        return criteriaBuilder.or(placesLikePredicates.toArray(new Predicate[0]));
    }

    private Predicate getIsFavoritePredicate(Root<Place> root, CriteriaBuilder criteriaBuilder,
                                             SearchCriteria searchCriteria) {
        String isFavoriteString = searchCriteria.getValue().toString().trim();
        if (isFavoriteString.isEmpty()) {
            return criteriaBuilder.conjunction();
        }

        boolean isFavorite = Boolean.parseBoolean(isFavoriteString);
        Join<FavoritePlace, User> favoritePlaceUserJoin = root.join(Place_.FAVORITE_PLACES).join(FavoritePlace_.USER);
        if (Boolean.TRUE.equals(isFavorite)) {
            return criteriaBuilder.equal(favoritePlaceUserJoin.get(User_.ID), userId);
        } else {
            return criteriaBuilder.notEqual(favoritePlaceUserJoin.get(User_.ID), userId);
        }
    }
}
