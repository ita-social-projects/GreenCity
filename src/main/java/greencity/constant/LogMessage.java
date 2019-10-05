package greencity.constant;

public class LogMessage {
    public static final String IN_SAVE = "in save(), entity: {}";
    public static final String IN_FIND_BY_ID = "in findById(), id: {}";
    public static final String IN_UPDATE = "in update(), updated entity: {}";
    public static final String IN_DELETE_BY_ID = "in deleteById(), id: {}";

    public static final String IN_FIND_ALL = "in findAll()";
    public static final String IN_FIND_BY_PLACE_ID = "in findById(), placeId: {}";
    public static final String IN_FIND_ID_BY_EMAIL = "in findIdByEmail(), email: {}";
    public static final String IN_DELETE_BY_PLACE_ID_AND_USER_EMAIL = "in deleteByPlaceIdAndUserEmail(), place id: {} and status: {} ";
    public static final String IN_UPDATE_PLACE_STATUS = "in updateStatus(), place id: {} and status: {}";
    public static final String IN_UPDATE_PLACE_STATUSES = "in updateStatuses(), updated statuses: {}";
    public static final String IN_BULK_DELETE = "in bulkDelete(), ids: {}";
    public static final String PLACE_STATUS_NOT_DIFFERENT = "the place with id: {} already has status: {}";
    public static final String IN_AVERAGE_RATE = "in averageRate(), id: {}";
    public static final String IN_EXISTS_BY_ID = "in existsById(), id: {}";
    public static final String IN_GET_ACCESS_PLACE_AS_FAVORITE_PLACE = "in getAccessPlaceAsFavoritePlace(), placeId: {}";
    public static final String SET_PLACE_TO_OPENING_HOURS = "in setPlaceToOpeningHours(Place place) - {}";
    public static final String SET_PLACE_TO_LOCATION = "in setPlaceToLocation(Place place) - {}";
    public static final String CREATE_CATEGORY_BY_NAME = "in createCategoryByName(String name) - {}";
    public static final String IN_GET_FAVORITE_PLACE_WITH_LOCATION = "in getFavoritePlaceWithLocation(), place id: {} and email: {}";
    public static final String IN_GET_FAVORITE_PLACE_WITH_PLACE_ID = "in getFavoritePlaceWithPlaceId(), email: {}";
    public static final String SET_PLACE_TO_DISCOUNTS = "in setToDiscountPlaceAndCategoty()";
    public static final String IN_UPDATE_DISCOUNT_FOR_PLACE = "in updateDiscountForUpdatedPlace()";
    public static final String IN_UPDATE_OPENING_HOURS_FOR_PLACE = "in updateOpeningHoursForUpdatedPlace()";
    public static final String IN_SEND_CHANGE_PLACE_STATUS_EMAIL = "in sendChangePlaceStatusEmail(), place: {}";
    public static final String IN_SEND_EMAIL = "in sendEmail(), receiver: {}, subject: {}, content: {}";
    public static final String IN_CREATE_TEMPLATE_NAME = "in createEmailTemplate(), vars: {}, templateName: {}";
}