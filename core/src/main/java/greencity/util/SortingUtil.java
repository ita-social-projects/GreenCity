package greencity.util;

import org.springframework.data.domain.Sort;

/**
 * Utility class for handling sorting operations.
 */
public final class SortingUtil {
    private SortingUtil() {
    }

    /**
     * Builds a sorting URL string from Spring Data Sort object.
     *
     * @param sort the Sort object containing sorting information
     * @return formatted sorting URL string, or empty string if no sorting
     */
    public static String buildSortingUrl(Sort sort) {
        if (sort.isEmpty()) {
            return "";
        }

        StringBuilder orderUrl = new StringBuilder();
        boolean isFirstSortProperty = true;

        for (Sort.Order order : sort) {
            if (isFirstSortProperty) {
                orderUrl.append(order.getProperty()).append(",").append(order.getDirection());
                isFirstSortProperty = false;
            } else {
                orderUrl.append("&sort=").append(order.getProperty()).append(",").append(order.getDirection());
            }
        }

        return orderUrl.toString();
    }
}