package greencity.service;

import greencity.annotations.CheckEmailPreference;
import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.constant.LogMessage;
import greencity.dto.category.CategoryDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.place.PlaceVO;
import greencity.dto.user.PlaceAuthorDto;
import greencity.entity.Notification;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.EmailPreference;
import greencity.enums.NotificationType;
import greencity.enums.PlaceStatus;
import greencity.message.GeneralEmailMessage;
import greencity.message.HabitAssignNotificationMessage;
import greencity.message.ScheduledEmailMessage;
import greencity.message.SendReportEmailMessage;
import greencity.message.UserTaggedInCommentMessage;
import greencity.repository.NotificationRepo;
import greencity.repository.PlaceRepo;
import greencity.repository.UserNotificationPreferenceRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private static final ZoneId ZONE_ID = ZoneId.of(AppConstant.UKRAINE_TIMEZONE);
    private final PlaceRepo placeRepo;
    private final NotificationRepo notificationRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final ThreadPoolExecutor emailThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    private final String emailLanguage = Locale.ENGLISH.getLanguage();
    private final UserNotificationPreferenceRepo userNotificationPreferenceRepo;
    @Value("${client.address}")
    private String clientAddress;

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
     * @author Dmytro Dmytruk
     */
    @Scheduled(cron = "0 0 10,19 * * *", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendLikeScheduledEmail() {
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, LocalDateTime.now(ZONE_ID), NotificationType.ECONEWS_COMMENT_LIKE);
        sendScheduledNotifications(NotificationType.ECONEWS_COMMENT_LIKE, EmailPreference.LIKES);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, LocalDateTime.now(ZONE_ID), NotificationType.ECONEWS_LIKE);
        sendScheduledNotifications(NotificationType.ECONEWS_LIKE, EmailPreference.LIKES);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, LocalDateTime.now(ZONE_ID), NotificationType.EVENT_COMMENT_LIKE);
        sendScheduledNotifications(NotificationType.EVENT_COMMENT_LIKE, EmailPreference.LIKES);
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dmytruk
     */
    @Scheduled(cron = "0 0 11,20 * * *", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendCommentScheduledEmail() {
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, LocalDateTime.now(ZONE_ID), NotificationType.ECONEWS_COMMENT);
        sendScheduledNotifications(NotificationType.ECONEWS_COMMENT, EmailPreference.COMMENTS);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, LocalDateTime.now(ZONE_ID), NotificationType.EVENT_COMMENT);
        sendScheduledNotifications(NotificationType.EVENT_COMMENT, EmailPreference.COMMENTS);
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dmytruk
     */
    @Scheduled(cron = "0 0 12,21 * * *", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendCommentReplyScheduledEmail() {
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, LocalDateTime.now(ZONE_ID),
            NotificationType.ECONEWS_COMMENT_REPLY);
        sendScheduledNotifications(NotificationType.ECONEWS_COMMENT_REPLY, EmailPreference.COMMENTS);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, LocalDateTime.now(ZONE_ID), NotificationType.EVENT_COMMENT_REPLY);
        sendScheduledNotifications(NotificationType.EVENT_COMMENT_REPLY, EmailPreference.COMMENTS);
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dmytruk
     */
    @Scheduled(cron = "0 0 13,22 * * *", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendFriendRequestScheduledEmail() {
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, LocalDateTime.now(ZONE_ID),
            NotificationType.FRIEND_REQUEST_RECEIVED);
        sendScheduledNotifications(NotificationType.FRIEND_REQUEST_RECEIVED, EmailPreference.INVITES);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, LocalDateTime.now(ZONE_ID),
            NotificationType.FRIEND_REQUEST_ACCEPTED);
        sendScheduledNotifications(NotificationType.FRIEND_REQUEST_ACCEPTED, EmailPreference.INVITES);
    }

    private void sendScheduledNotifications(NotificationType type, EmailPreference emailPreference) {
        RequestAttributes originalRequestAttributes = RequestContextHolder.getRequestAttributes();
        emailThreadPool.submit(() -> {
            try {
                List<Notification> notifications = notificationRepo.findAllByNotificationTypeAndViewedIsFalse(type);
                if (!notifications.isEmpty()) {
                    RequestContextHolder.setRequestAttributes(originalRequestAttributes);
                    notifications.stream()
                        .filter(n -> !userNotificationPreferenceRepo
                            .existsByUserIdAndEmailPreference(n.getTargetUser().getId(), emailPreference))
                        .forEach(notification -> {
                            ScheduledEmailMessage message = createScheduledEmailMessage(notification, emailLanguage);
                            restClient.sendScheduledEmailNotification(message);
                        });
                }
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
    @CheckEmailPreference(EmailPreference.SYSTEM)
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

    @CheckEmailPreference(EmailPreference.LIKES)
    public void sendEmailNotificationLikes(GeneralEmailMessage generalEmailMessage) {
        sendEmailNotification(generalEmailMessage);
    }

    @CheckEmailPreference(EmailPreference.COMMENTS)
    public void sendEmailNotificationComments(GeneralEmailMessage generalEmailMessage) {
        sendEmailNotification(generalEmailMessage);
    }

    @CheckEmailPreference(EmailPreference.INVITES)
    public void sendEmailNotificationInvites(GeneralEmailMessage generalEmailMessage) {
        sendEmailNotification(generalEmailMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckEmailPreference(EmailPreference.SYSTEM)
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

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dmytruk
     */
    @Override
    @CheckEmailPreference(EmailPreference.COMMENTS)
    public void sendUsersTaggedInCommentEmailNotification(UserTaggedInCommentMessage message) {
        RequestAttributes originalRequestAttributes = RequestContextHolder.getRequestAttributes();
        emailThreadPool.submit(() -> {
            try {
                RequestContextHolder.setRequestAttributes(originalRequestAttributes);
                restClient.sendUserTaggedInCommentNotification(message);
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
            .map(o -> modelMapper.map(o, PlaceNotificationDto.class))
            .toList();
        Map<CategoryDto, List<PlaceNotificationDto>> categoriesWithPlacesMap = new HashMap<>();
        List<CategoryDto> categories = getUniqueCategoriesFromPlaces(places);
        List<PlaceNotificationDto> placesByCategory = new ArrayList<>();
        for (CategoryDto category : categories) {
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
        return places.stream()
            .map(Place::getCategory)
            .distinct()
            .map(o -> modelMapper.map(o, CategoryDto.class))
            .toList();
    }

    private ScheduledEmailMessage createScheduledEmailMessage(Notification notification, String language) {
        ResourceBundle bundle = ResourceBundle.getBundle("notification", Locale.forLanguageTag(language),
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT));
        String subject = bundle.getString(notification.getNotificationType() + "_TITLE");
        String bodyTemplate = bundle.getString(notification.getNotificationType().toString());
        String actionUserText;
        int size = notification.getActionUsers().size();
        if (size > 1) {
            actionUserText = size + " " + bundle.getString("USERS");
        } else if (size == 1) {
            User actionUser = notification.getActionUsers().getFirst();
            actionUserText = actionUser != null ? actionUser.getName() : "";
        } else {
            actionUserText = "";
        }
        String customMessage = notification.getCustomMessage() != null ? notification.getCustomMessage() : "";
        String secondMessage = notification.getSecondMessage() != null ? notification.getSecondMessage() : "";
        String body = bodyTemplate
            .replace("{user}", actionUserText)
            .replace("{message}", customMessage)
            .replace("{secondMessage}", secondMessage);

        return ScheduledEmailMessage.builder()
            .email(notification.getTargetUser().getEmail())
            .username(notification.getTargetUser().getName())
            .baseLink(createBaseLink(notification))
            .subject(subject)
            .body(body)
            .language(language)
            .build();
    }

    private String createBaseLink(Notification notification) {
        return clientAddress + "/#/profile/" + notification.getTargetUser().getId() + "/notifications";
    }
}