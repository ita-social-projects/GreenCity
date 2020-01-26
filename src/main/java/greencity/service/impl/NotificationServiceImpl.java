package greencity.service.impl;

import static greencity.constant.RabbitConstants.SEND_REPORT_ROUTING_KEY;

import greencity.constant.AppConstant;
import greencity.constant.LogMessage;
import greencity.dto.category.CategoryDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.PlaceStatus;
import greencity.message.SendReportEmailMessage;
import greencity.repository.PlaceRepo;
import greencity.repository.UserRepo;
import greencity.service.NotificationService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    private static final ZoneId ZONE_ID = ZoneId.of(AppConstant.UKRAINE_TIMEZONE);
    private final UserRepo userRepo;
    private final PlaceRepo placeRepo;
    private final RabbitTemplate rabbitTemplate;
    @Value("${messaging.rabbit.email.topic}")
    private String sendEmailTopic;
    private final ModelMapper modelMapper;


    /**
     * Constructor.
     */
    @Autowired
    public NotificationServiceImpl(UserRepo userRepo, PlaceRepo placeRepo,
                                   RabbitTemplate rabbitTemplate,
                                   ModelMapper modelMapper) {
        this.userRepo = userRepo;
        this.placeRepo = placeRepo;
        this.rabbitTemplate = rabbitTemplate;
        this.modelMapper = modelMapper;
    }

    @Override
    public void sendImmediatelyReport(Place newPlace) {
        log.info(LogMessage.IN_SEND_IMMEDIATELY_REPORT, newPlace.getName());
        EmailNotification emailNotification = EmailNotification.IMMEDIATELY;
        List<PlaceAuthorDto> subscribers = getSubscribers(emailNotification);

        Map<CategoryDto, List<PlaceNotificationDto>> categoriesDtoWithPlacesDtoMap = new HashMap<>();
        CategoryDto map = modelMapper.map(newPlace.getCategory(), CategoryDto.class);
        List<PlaceNotificationDto> placeDtoList =
            Collections.singletonList(modelMapper.map(newPlace, PlaceNotificationDto.class));
        categoriesDtoWithPlacesDtoMap.put(map, placeDtoList);

        List<PlaceAuthorDto> placeAuthorDto = subscribers.stream()
            .map(o -> modelMapper.map(o, PlaceAuthorDto.class))
            .collect(Collectors.toList());


        rabbitTemplate.convertAndSend(sendEmailTopic, SEND_REPORT_ROUTING_KEY,
            new SendReportEmailMessage(placeAuthorDto, categoriesDtoWithPlacesDtoMap, emailNotification.toString()));
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
            rabbitTemplate.convertAndSend(sendEmailTopic, SEND_REPORT_ROUTING_KEY,
                new SendReportEmailMessage(subscribers, categoriesDtoWithPlacesDtoMap, emailNotification.toString()));
        }
    }

    private List<PlaceAuthorDto> getSubscribers(EmailNotification emailNotification) {
        log.info(LogMessage.IN_GET_SUBSCRIBERS, emailNotification);
        return userRepo.findAllByEmailNotification(emailNotification).stream()
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
