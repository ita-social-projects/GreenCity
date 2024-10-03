package greencity.service;

import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.constant.LogMessage;
import greencity.dto.category.CategoryDto;
import greencity.dto.notification.EmailNotificationDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.place.PlaceVO;
import greencity.dto.user.PlaceAuthorDto;
import greencity.entity.Notification;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.NotificationType;
import greencity.enums.PlaceStatus;
import greencity.message.ScheduledEmailMessage;
import greencity.message.SendReportEmailMessage;
import greencity.repository.NotificationRepo;
import greencity.repository.PlaceRepo;
import greencity.repository.UserNotificationPreferenceRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
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
    private final UserNotificationPreferenceRepo userNotificationPreferenceRepo;
    private final UserRepo userRepo;
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
    @Scheduled(cron = "0 0 11,20 * * *", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendLikeScheduledEmail() {
        LocalDateTime now = LocalDateTime.now(ZONE_ID);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.ECONEWS_COMMENT_LIKE);
        sendScheduledNotifications(NotificationType.ECONEWS_COMMENT_LIKE, EmailPreference.LIKES, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.ECONEWS_LIKE);
        sendScheduledNotifications(NotificationType.ECONEWS_LIKE, EmailPreference.LIKES, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.EVENT_COMMENT_LIKE);
        sendScheduledNotifications(NotificationType.EVENT_COMMENT_LIKE, EmailPreference.LIKES, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.HABIT_LIKE);
        sendScheduledNotifications(NotificationType.HABIT_LIKE, EmailPreference.LIKES, now);
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dmytruk
     */
    @Scheduled(cron = "0 0 10,19 * * *", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendCommentScheduledEmail() {
        LocalDateTime now = LocalDateTime.now(ZONE_ID);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.ECONEWS_COMMENT);
        sendScheduledNotifications(NotificationType.ECONEWS_COMMENT, EmailPreference.COMMENTS, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.EVENT_COMMENT);
        sendScheduledNotifications(NotificationType.EVENT_COMMENT, EmailPreference.COMMENTS, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.HABIT_COMMENT);
        sendScheduledNotifications(NotificationType.HABIT_COMMENT, EmailPreference.COMMENTS, now);
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dmytruk
     */
    @Scheduled(cron = "0 0 10,19 * * *", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendCommentReplyScheduledEmail() {
        LocalDateTime now = LocalDateTime.now(ZONE_ID);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.ECONEWS_COMMENT_REPLY);
        sendScheduledNotifications(NotificationType.ECONEWS_COMMENT_REPLY, EmailPreference.COMMENTS, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.EVENT_COMMENT_REPLY);
        sendScheduledNotifications(NotificationType.EVENT_COMMENT_REPLY, EmailPreference.COMMENTS, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.HABIT_COMMENT_REPLY);
        sendScheduledNotifications(NotificationType.HABIT_COMMENT_REPLY, EmailPreference.COMMENTS, now);
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dmytruk
     */
    @Scheduled(cron = "0 0 11,20 * * *", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendFriendRequestScheduledEmail() {
        LocalDateTime now = LocalDateTime.now(ZONE_ID);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.FRIEND_REQUEST_RECEIVED);
        sendScheduledNotifications(NotificationType.FRIEND_REQUEST_RECEIVED, EmailPreference.INVITES, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.FRIEND_REQUEST_ACCEPTED);
        sendScheduledNotifications(NotificationType.FRIEND_REQUEST_ACCEPTED, EmailPreference.INVITES, now);
    }

    /**
     * {@inheritDoc}
     *
     * @author Roman Kasarab
     */
    @Scheduled(cron = "0 0 10,19 * * *", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendTaggedInCommentScheduledEmail() {
        LocalDateTime now = LocalDateTime.now(ZONE_ID);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.EVENT_COMMENT_USER_TAG);
        sendScheduledNotifications(NotificationType.EVENT_COMMENT_USER_TAG, EmailPreference.COMMENTS, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.ECONEWS_COMMENT_USER_TAG);
        sendScheduledNotifications(NotificationType.ECONEWS_COMMENT_USER_TAG, EmailPreference.COMMENTS, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.HABIT_COMMENT_USER_TAG);
        sendScheduledNotifications(NotificationType.HABIT_COMMENT_USER_TAG, EmailPreference.COMMENTS, now);
    }

    /**
     * {@inheritDoc}
     *
     * @author Viktoriia Herchanivska
     */
    @Scheduled(cron = "0 0 11,20 * * *", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendHabitInviteScheduledEmail() {
        LocalDateTime now = LocalDateTime.now(ZONE_ID);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.HABIT_INVITE);
        sendScheduledNotifications(NotificationType.HABIT_INVITE, EmailPreference.INVITES, now);
    }

    /**
     * {@inheritDoc}
     *
     * @author Viktoriia Herchanivska
     */
    @Scheduled(cron = "0 0 9,18 * * *", zone = AppConstant.UKRAINE_TIMEZONE)
    @Override
    public void sendSystemNotificationsScheduledEmail() {
        LocalDateTime now = LocalDateTime.now(ZONE_ID);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.ECONEWS_CREATED);
        sendScheduledNotifications(NotificationType.ECONEWS_CREATED, EmailPreference.SYSTEM, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.EVENT_CREATED);
        sendScheduledNotifications(NotificationType.EVENT_CREATED, EmailPreference.SYSTEM, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.EVENT_CANCELED);
        sendScheduledNotifications(NotificationType.EVENT_CANCELED, EmailPreference.SYSTEM, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.EVENT_NAME_UPDATED);
        sendScheduledNotifications(NotificationType.EVENT_NAME_UPDATED, EmailPreference.SYSTEM, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.EVENT_UPDATED);
        sendScheduledNotifications(NotificationType.EVENT_UPDATED, EmailPreference.SYSTEM, now);
        log.info(LogMessage.IN_SEND_SCHEDULED_EMAIL, now, NotificationType.EVENT_JOINED);
        sendScheduledNotifications(NotificationType.EVENT_JOINED, EmailPreference.SYSTEM, now);
    }

    private void sendScheduledNotifications(NotificationType type, EmailPreference emailPreference, LocalDateTime now) {
        RequestAttributes originalRequestAttributes = RequestContextHolder.getRequestAttributes();
        emailThreadPool.submit(() -> {
            try {
                List<Notification> notifications =
                    notificationRepo.findAllByNotificationByTypeAndViewedIsFalseAndEmailSentIsFalse(type);
                if (!notifications.isEmpty()) {
                    RequestContextHolder.setRequestAttributes(originalRequestAttributes);
                    notifications.stream()
                        .filter(n -> isTimeToSendScheduleNotification(n.getTargetUser().getId(), emailPreference, now))
                        .forEach(notification -> {
                            ScheduledEmailMessage message = createScheduledEmailMessage(notification,
                                notification.getTargetUser().getLanguage().getCode());
                            restClient.sendScheduledEmailNotification(message);
                        });
                }
                notifications = notifications.stream().map(notification -> notification.setEmailSent(true)).toList();
                notificationRepo.saveAll(notifications);
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    private boolean isTimeToSendScheduleNotification(Long userId, EmailPreference emailPreference, LocalDateTime now) {
        boolean timeToSend = userNotificationPreferenceRepo
            .existsByUserIdAndEmailPreferenceAndPeriodicity(userId, emailPreference,
                EmailPreferencePeriodicity.TWICE_A_DAY);
        if (now.getHour() < 12) {
            timeToSend = timeToSend || userNotificationPreferenceRepo
                .existsByUserIdAndEmailPreferenceAndPeriodicity(userId, emailPreference,
                    EmailPreferencePeriodicity.DAILY);
        }
        if (now.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            timeToSend = timeToSend || userNotificationPreferenceRepo
                .existsByUserIdAndEmailPreferenceAndPeriodicity(userId, emailPreference,
                    EmailPreferencePeriodicity.WEEKLY);
        }
        if (now.getDayOfMonth() == 1) {
            timeToSend = timeToSend || userNotificationPreferenceRepo
                .existsByUserIdAndEmailPreferenceAndPeriodicity(userId, emailPreference,
                    EmailPreferencePeriodicity.MONTHLY);
        }
        return timeToSend;
    }

    /**
     * {@inheritDoc}
     *
     * @author Viktoriia Herchanivska
     */
    @Override
    public void sendEmailNotification(EmailNotificationDto notificationDto) {
        Notification notification = modelMapper.map(notificationDto, Notification.class);
        NotificationType type = notification.getNotificationType();
        User user = userRepo.findById(notification.getTargetUser().getId()).get();
        ScheduledEmailMessage message = createScheduledEmailMessage(notification,
            user.getLanguage() != null ? user.getLanguage().getCode() : Locale.getDefault().getLanguage());
        List<NotificationType> likes = List.of(NotificationType.ECONEWS_COMMENT_LIKE,
            NotificationType.ECONEWS_LIKE,
            NotificationType.EVENT_COMMENT_LIKE,
            NotificationType.HABIT_LIKE);
        List<NotificationType> comments = List.of(NotificationType.ECONEWS_COMMENT,
            NotificationType.EVENT_COMMENT,
            NotificationType.HABIT_COMMENT,
            NotificationType.ECONEWS_COMMENT_REPLY,
            NotificationType.EVENT_COMMENT_REPLY,
            NotificationType.HABIT_COMMENT_REPLY,
            NotificationType.ECONEWS_COMMENT_USER_TAG,
            NotificationType.EVENT_COMMENT_USER_TAG,
            NotificationType.HABIT_COMMENT_USER_TAG);
        List<NotificationType> invites = List.of(NotificationType.FRIEND_REQUEST_RECEIVED,
            NotificationType.FRIEND_REQUEST_ACCEPTED,
            NotificationType.HABIT_INVITE);
        List<NotificationType> systems = List.of(NotificationType.ECONEWS_CREATED,
            NotificationType.EVENT_CREATED,
            NotificationType.EVENT_CANCELED,
            NotificationType.EVENT_NAME_UPDATED,
            NotificationType.EVENT_UPDATED,
            NotificationType.EVENT_JOINED);
        try {
            if (likes.contains(type)) {
                restClient.sendEmailNotificationLikes(message);
            } else if (comments.contains(type)) {
                restClient.sendEmailNotificationComments(message);
            } else if (invites.contains(type)) {
                restClient.sendEmailNotificationInvites(message);
            } else if (systems.contains(type)) {
                restClient.sendEmailNotificationSystem(message);
            }
            notification.setEmailSent(true);
            notificationRepo.save(notification);
        } catch (Exception e) {
            log.warn(LogMessage.IN_SEND_SCHEDULED_EMAIL, LocalDateTime.now(), type);
        }
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