package greencity.service.impl;

import greencity.constant.AppConstant;
import greencity.constant.LogMessage;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.PlaceStatus;
import greencity.repository.PlaceRepo;
import greencity.repository.UserRepo;
import greencity.service.EmailService;
import greencity.service.NotificationService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private static final ZoneId ZONE_ID = ZoneId.of(AppConstant.UKRAINE_TIMEZONE);
    private final UserRepo userRepo;
    private final PlaceRepo placeRepo;
    private final EmailService emailService;
    private LocalDateTime startDate;
    private List<User> subscribers;
    private Map<Category, List<Place>> categoriesWithPlacesMap = new HashMap<>();

    @Override
    public void sendImmediatelyReport(Place newPlace) {
        log.info(LogMessage.IN_SEND_IMMEDIATELY_REPORT, new Place());
        EmailNotification emailNotification = EmailNotification.IMMEDIATELY;
        subscribers = getSubscribers(emailNotification);
        categoriesWithPlacesMap = new HashMap<>();
        categoriesWithPlacesMap.put(newPlace.getCategory(), Collections.singletonList(newPlace));

        emailService.sendAddedNewPlacesReportEmail(subscribers, categoriesWithPlacesMap, emailNotification);
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Scheduled(cron = "0 0 12 ? * *", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendDailyReport() {
        log.info(LogMessage.IN_SEND_DAILY_REPORT, LocalDateTime.now(ZONE_ID));
        startDate = LocalDateTime.now(ZONE_ID).minusDays(1);
        sendReport(EmailNotification.DAILY);
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Scheduled(cron = "0 0 12 ? * MON", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendWeeklyReport() {
        log.info(LogMessage.IN_SEND_WEEKLY_REPORT, LocalDateTime.now(ZONE_ID));
        startDate = LocalDateTime.now(ZONE_ID).minusWeeks(1);
        sendReport(EmailNotification.WEEKLY);
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Scheduled(cron = "0 0 12 1 * ?", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendMonthlyReport() {
        log.info(LogMessage.IN_SEND_MONTHLY_REPORT, LocalDateTime.now(ZONE_ID));
        startDate = LocalDateTime.now(ZONE_ID).minusMonths(1);
        sendReport(EmailNotification.MONTHLY);
    }

    private void sendReport(EmailNotification emailNotification) {
        log.info(LogMessage.IN_SEND_REPORT, emailNotification);
        LocalDateTime endDate = LocalDateTime.now(ZONE_ID);
        subscribers = getSubscribers(emailNotification);
        if (!subscribers.isEmpty()) {
            List<Place> places = placeRepo.findAllByModifiedDateBetweenAndStatus(
                startDate, endDate, PlaceStatus.APPROVED);
            categoriesWithPlacesMap = getCategoriesWithPlacesMap(places);
        }
        if (!categoriesWithPlacesMap.isEmpty()) {
            emailService.sendAddedNewPlacesReportEmail(
                subscribers,
                categoriesWithPlacesMap,
                emailNotification
            );
        }
    }

    private List<User> getSubscribers(EmailNotification emailNotification) {
        log.info(LogMessage.IN_GET_SUBSCRIBERS, emailNotification);
        subscribers = userRepo.findAllByEmailNotification(emailNotification);
        return subscribers;
    }

    private Map<Category, List<Place>> getCategoriesWithPlacesMap(List<Place> places) {
        log.info(LogMessage.IN_GET_CATEGORIES_WITH_PLACES_MAP, places);
        categoriesWithPlacesMap = new HashMap<>();
        List<Category> categories = getUniqueCategoriesFromPlaces(places);
        List<Place> placesByCategory;
        for (Category category : categories) {
            placesByCategory = new ArrayList<>();
            for (Place place : places) {
                if (place.getCategory().equals(category)) {
                    placesByCategory.add(place);
                }
            }
            categoriesWithPlacesMap.put(category, placesByCategory);
        }
        return categoriesWithPlacesMap;
    }

    private List<Category> getUniqueCategoriesFromPlaces(List<Place> places) {
        log.info(LogMessage.IN_GET_UNIQUE_CATEGORIES_FROM_PLACES, places);
        return places.stream()
            .map(Place::getCategory)
            .collect(Collectors.toList()).stream()
            .distinct()
            .collect(Collectors.toList());
    }
}
