package greencity.service;

import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.constant.LogMessage;
import greencity.dto.category.CategoryDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.place.PlaceVO;
import greencity.dto.user.PlaceAuthorDto;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.enums.EmailNotification;
import greencity.enums.PlaceStatus;
import greencity.message.GeneralEmailMessage;
import greencity.message.HabitAssignNotificationMessage;
import greencity.message.SendReportEmailMessage;
import greencity.repository.PlaceRepo;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    private static final ZoneId ZONE_ID = ZoneId.of(AppConstant.UKRAINE_TIMEZONE);
    private final PlaceRepo placeRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final ThreadPoolExecutor emailThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    /**
     * Constructor.
     */
    @Autowired
    public NotificationServiceImpl(PlaceRepo placeRepo,
        ModelMapper modelMapper, RestClient restClient) {
        this.placeRepo = placeRepo;
        this.modelMapper = modelMapper;
        this.restClient = restClient;
    }

    @Override
    public void sendImmediatelyReport(PlaceVO newPlace) {
        log.info(LogMessage.IN_SEND_IMMEDIATELY_REPORT, newPlace.getName());
        EmailNotification emailNotification = EmailNotification.IMMEDIATELY;
        List<PlaceAuthorDto> subscribers = getSubscribers(emailNotification);

        Map<CategoryDto, List<PlaceNotificationDto>> categoriesDtoWithPlacesDtoMap = new HashMap<>();
        CategoryDto map = modelMapper.map(newPlace.getCategory(), CategoryDto.class);
        List<PlaceNotificationDto> placeDtoList =
            Collections.singletonList(modelMapper.map(newPlace, PlaceNotificationDto.class));
        categoriesDtoWithPlacesDtoMap.put(map, placeDtoList);

        restClient.sendReport(new SendReportEmailMessage(subscribers,
            categoriesDtoWithPlacesDtoMap, emailNotification.toString()));
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka && Bogdan Kuzenko
     */
    @Scheduled(cron = "0 0 12 ? * *", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendDailyReport() {
        log.info(LogMessage.IN_SEND_DAILY_REPORT, LocalDateTime.now(ZONE_ID));
        LocalDateTime startDate = LocalDateTime.now(ZONE_ID).minusDays(1);
        sendReport(EmailNotification.DAILY, startDate);
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
        LocalDateTime startDate = LocalDateTime.now(ZONE_ID).minusWeeks(1);
        sendReport(EmailNotification.WEEKLY, startDate);
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
        LocalDateTime startDate = LocalDateTime.now(ZONE_ID).minusMonths(1);
        sendReport(EmailNotification.MONTHLY, startDate);
    }

    /**
     * {@inheritDoc}
     *
     * @author Yurii Midianyi
     */
    @Override
    public void sendEmailNotification(Set<String> usersEmails, String subject, String message) {
        RequestAttributes originalRequestAttributes = RequestContextHolder.getRequestAttributes();
        emailThreadPool.submit(() -> {
            try {
                RequestContextHolder.setRequestAttributes(originalRequestAttributes);
                usersEmails.forEach(attenderEmail -> restClient.sendEmailNotification(
                    GeneralEmailMessage.builder()
                        .email(attenderEmail)
                        .subject(subject)
                        .message(message)
                        .build()));
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    /**
     * {@inheritDoc}
     *
     * @author Yurii Midianyi
     */
    @Override
    public void sendEmailNotification(GeneralEmailMessage generalEmailMessage) {
        RequestAttributes originalRequestAttributes = RequestContextHolder.getRequestAttributes();
        emailThreadPool.submit(() -> {
            try {
                RequestContextHolder.setRequestAttributes(originalRequestAttributes);
                restClient.sendEmailNotification(generalEmailMessage);
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendHabitAssignEmailNotification(HabitAssignNotificationMessage message) {
        RequestAttributes originalRequestAttributes = RequestContextHolder.getRequestAttributes();
        emailThreadPool.submit(() -> {
            try {
                RequestContextHolder.setRequestAttributes(originalRequestAttributes);
                restClient.sendHabitAssignNotification(message);
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    private void sendReport(EmailNotification emailNotification, LocalDateTime startDate) {
        log.info(LogMessage.IN_SEND_REPORT, emailNotification);
        List<PlaceAuthorDto> subscribers = getSubscribers(emailNotification);
        Map<CategoryDto, List<PlaceNotificationDto>> categoriesDtoWithPlacesDtoMap = new HashMap<>();
        LocalDateTime endDate = LocalDateTime.now(ZONE_ID);
        if (!subscribers.isEmpty()) {
            List<Place> places = placeRepo.findAllByModifiedDateBetweenAndStatus(
                startDate, endDate, PlaceStatus.APPROVED);
            categoriesDtoWithPlacesDtoMap = getCategoriesDtoWithPlacesDtoMap(places);
        }
        if (!categoriesDtoWithPlacesDtoMap.isEmpty()) {
            restClient.sendReport(
                new SendReportEmailMessage(subscribers, categoriesDtoWithPlacesDtoMap, emailNotification.toString()));
        }
    }

    private List<PlaceAuthorDto> getSubscribers(EmailNotification emailNotification) {
        log.info(LogMessage.IN_GET_SUBSCRIBERS, emailNotification);
        return restClient.findAllByEmailNotification(emailNotification).stream()
            .map(o -> modelMapper.map(o, PlaceAuthorDto.class))
            .collect(Collectors.toList());
    }

    private Map<CategoryDto, List<PlaceNotificationDto>> getCategoriesDtoWithPlacesDtoMap(List<Place> places) {
        log.info(LogMessage.IN_GET_CATEGORIES_WITH_PLACES_MAP, places.toString());
        List<PlaceNotificationDto> placeDto = places.stream()
            .map(o -> modelMapper.map(o, PlaceNotificationDto.class)).collect(Collectors.toList());
        Map<CategoryDto, List<PlaceNotificationDto>> categoriesWithPlacesMap = new HashMap<>();
        List<CategoryDto> categories = getUniqueCategoriesFromPlaces(places);
        List<PlaceNotificationDto> placesByCategory;
        for (CategoryDto category : categories) {
            placesByCategory = new ArrayList<>();
            for (PlaceNotificationDto place : placeDto) {
                if (place.getCategory().equals(category)) {
                    placesByCategory.add(place);
                }
            }
            categoriesWithPlacesMap.put(category, placesByCategory);
        }
        return categoriesWithPlacesMap;
    }

    private List<CategoryDto> getUniqueCategoriesFromPlaces(List<Place> places) {
        log.info(LogMessage.IN_GET_UNIQUE_CATEGORIES_FROM_PLACES, places);
        List<Category> collect = places.stream()
            .map(Place::getCategory)
            .collect(Collectors.toList()).stream()
            .distinct()
            .collect(Collectors.toList());
        return collect.stream()
            .map(o -> modelMapper.map(o, CategoryDto.class)).collect(Collectors.toList());
    }
}