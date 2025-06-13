package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.ActionDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.notification.EmailNotificationDto;
import greencity.dto.notification.LikeNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.notification.NotificationInviteDto;
import greencity.dto.notification.UbsNotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.InvitationStatus;
import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.HabitAssignRepo;
import greencity.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Stream;
import static greencity.constant.AppConstant.LANGUAGE_CODE_UA;
import static greencity.constant.AppConstant.THREE_OR_MORE_USERS;
import static greencity.constant.AppConstant.TIMES_PLACEHOLDER;
import static greencity.constant.AppConstant.TWO_USERS;
import static greencity.constant.AppConstant.USER_PLACEHOLDER;
import static greencity.utils.NotificationUtils.isMessageLocalizationRequired;
import static greencity.utils.NotificationUtils.localizeMessage;
import static greencity.utils.NotificationUtils.resolveTimesInEnglish;
import static greencity.utils.NotificationUtils.resolveTimesInUkrainian;

/**
 * Implementation of {@link UserNotificationService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserNotificationServiceImpl implements UserNotificationService {
    private static final int NOTIFICATION_SOURCES_COUNT = 2;
    private static final String TOPIC = "/topic/";
    private static final String NOTIFICATION = "/notification";

    private final NotificationRepo notificationRepo;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;
    private final HabitInvitationService habitInvitationService;
    private final NotificationFriendService notificationFriendService;
    private final SimpMessagingTemplate messagingTemplate;
    private final HabitAssignRepo habitAssignRepo;
    private final RestClient restClient;

    private final Comparator<NotificationDto> sortByRecentNotificationsComparator = Comparator
        .comparing(NotificationDto::getTime).reversed();

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<NotificationDto> getNotificationsFiltered(Long userId, Pageable page,
        Principal principal,
        String language, ProjectName projectName, List<NotificationType> notificationTypes, Boolean viewed) {
        if (projectName != null) {
            return projectName == ProjectName.GREENCITY
                ? getNotificationsForUserFromGreenCity(page, userId, language, projectName, notificationTypes,
                    viewed)
                : getNotificationsForUserFromUbs(principal, page, viewed);
        }

        long totalElements = calculateTotalElements(userId, principal, language, notificationTypes, viewed);
        PageableInfo pageableInfo = new PageableInfo(page, totalElements);

        List<NotificationDto> mergedNotifications = isUnreadOnly(viewed)
            ? getUnreadNotifications(userId, principal, language, notificationTypes, pageableInfo.endIndex())
            : loadAndMergeNotifications(userId, principal, language, notificationTypes, viewed, pageableInfo);

        return buildPagedResult(mergedNotifications, pageableInfo);
    }

    /**
     * Calculates the total number of notifications from all sources.
     *
     * @param principal         the authenticated user
     * @param language          the language code for localization
     * @param notificationTypes the types of notifications to filter by
     * @param viewed            whether to filter by viewed status
     * @return the total number of notifications
     */
    private long calculateTotalElements(Long userId, Principal principal, String language, List<NotificationType> notificationTypes,
        Boolean viewed) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(NOTIFICATION_SOURCES_COUNT)) {
            CompletableFuture<Long> greenCityTotalFuture = CompletableFuture.supplyAsync(() -> {
                Pageable tempPageable = PageRequest.of(0, 1);
                return getNotificationsForUserFromGreenCity(tempPageable, userId, language, null, notificationTypes,
                    viewed).getTotalElements();
            }, executorService).exceptionally(throwable -> {
                log.error("Failed to fetch GreenCity notifications: {}", throwable.getMessage());
                return 0L;
            });

            CompletableFuture<Long> ubsTotalFuture = CompletableFuture.supplyAsync(() -> {
                Pageable tempPageable = PageRequest.of(0, 1);
                return getNotificationsForUserFromUbs(principal, tempPageable, viewed).getTotalElements();
            }, executorService).exceptionally(throwable -> {
                log.error("Failed to fetch UBS notifications: {}", throwable.getMessage());
                return 0L;
            });

            return greenCityTotalFuture.join() + ubsTotalFuture.join();
        }
    }

    /**
     * Loads and merges notifications from all sources (GreenCity and UBS).
     *
     * @param principal         the authenticated user
     * @param language          the language code for localization
     * @param notificationTypes the types of notifications to filter by
     * @param viewed            whether to filter by viewed status
     * @param pageableInfo      pagination information
     * @return a list of merged notifications sorted by time (newest first)
     */
    private List<NotificationDto> loadAndMergeNotifications(Long userId, Principal principal, String language,
        List<NotificationType> notificationTypes, Boolean viewed, PageableInfo pageableInfo) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(NOTIFICATION_SOURCES_COUNT)) {
            CompletableFuture<List<NotificationDto>> greenCityFuture = CompletableFuture.supplyAsync(
                () -> loadNotificationsFromSource(
                    pageableInfo.pageSize(),
                    pageableInfo.endIndex(),
                    pageable -> getNotificationsForUserFromGreenCity(pageable, userId, language, null,
                        notificationTypes, viewed)),
                executorService);

            CompletableFuture<List<NotificationDto>> ubsFuture = CompletableFuture.supplyAsync(
                () -> loadNotificationsFromSource(
                    pageableInfo.pageSize(),
                    pageableInfo.endIndex(),
                    pageable -> getNotificationsForUserFromUbs(principal, pageable, viewed)),
                executorService);

            return Stream.concat(greenCityFuture.join().stream(), ubsFuture.join().stream())
                .filter(dto -> dto.getTime() != null)
                .sorted(sortByRecentNotificationsComparator)
                .limit(pageableInfo.endIndex())
                .toList();
        }
    }

    /**
     * Loads notifications from a specific source with pagination.
     *
     * @param pageSize            the size of each page
     * @param endIndex            the maximum number of notifications to load
     * @param notificationFetcher the function to fetch notifications for a given
     *                            page
     * @return a list of notifications from the source
     */
    private List<NotificationDto> loadNotificationsFromSource(int pageSize, int endIndex,
        Function<Pageable, PageableAdvancedDto<NotificationDto>> notificationFetcher) {
        List<NotificationDto> notifications = new ArrayList<>();
        int currentPage = 0;
        Pageable tempPageable = PageRequest.of(currentPage, pageSize);
        PageableAdvancedDto<NotificationDto> page;

        do {
            page = notificationFetcher.apply(tempPageable);
            notifications.addAll(page.getPage());
            currentPage++;
            tempPageable = PageRequest.of(currentPage, pageSize);
        } while (page.isHasNext() && notifications.size() < endIndex);

        return notifications;
    }

    /**
     * Builds a paged result from a list of notifications.
     *
     * @param mergedNotifications the list of notifications to paginate
     * @param pageableInfo        pagination information
     * @return a PageableAdvancedDto containing the paged notifications
     */
    private PageableAdvancedDto<NotificationDto> buildPagedResult(List<NotificationDto> mergedNotifications,
        PageableInfo pageableInfo) {
        List<NotificationDto> pagedNotifications = mergedNotifications.subList(
            Math.min(pageableInfo.startIndex(), mergedNotifications.size()),
            Math.min(pageableInfo.endIndex(), mergedNotifications.size()));

        return PageableAdvancedDto.<NotificationDto>builder()
            .page(pagedNotifications)
            .totalElements(pageableInfo.totalElements())
            .currentPage(pageableInfo.pageNumber())
            .totalPages(pageableInfo.totalPages())
            .number(pageableInfo.pageNumber())
            .hasPrevious(pageableInfo.hasPrevious())
            .hasNext(pageableInfo.hasNext())
            .first(pageableInfo.isFirst())
            .last(pageableInfo.isLast())
            .build();
    }

    private boolean isUnreadOnly(Boolean viewed) {
        return Boolean.FALSE.equals(viewed);
    }

    /**
     * A record to hold pagination information.
     *
     * @param pageSize      the size of each page
     * @param pageNumber    the current page number
     * @param startIndex    the start index of the current page
     * @param endIndex      the end index of the current page
     * @param totalElements the total number of elements
     */
    private record PageableInfo(int pageSize, int pageNumber, int startIndex, int endIndex, long totalElements) {
        public PageableInfo(Pageable page, long totalElements) {
            this(page.getPageSize(), page.getPageNumber(),
                page.getPageNumber() * page.getPageSize(),
                Math.min(page.getPageNumber() * page.getPageSize() + page.getPageSize(), (int) totalElements),
                totalElements);
        }

        public int totalPages() {
            return (int) Math.ceilDiv(totalElements, pageSize);
        }

        public boolean hasPrevious() {
            return pageNumber > 0;
        }

        public boolean hasNext() {
            return (pageNumber + 1) < totalPages();
        }

        public boolean isFirst() {
            return pageNumber == 0;
        }

        public boolean isLast() {
            return !hasNext();
        }
    }

    /**
     * Retrieves unread notifications for a user up to a specified limit.
     *
     * @param userId            the authenticated user id
     * @param principal         the authenticated user
     * @param language          the language code for localization
     * @param notificationTypes the types of notifications to filter by
     * @param limit             the maximum number of notifications to retrieve
     * @return a list of unread notifications sorted by time (newest first)
     */
    private List<NotificationDto> getUnreadNotifications(Long userId, Principal principal, String language,
                                                         List<NotificationType> notificationTypes, int limit) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(NOTIFICATION_SOURCES_COUNT)) {
            CompletableFuture<List<NotificationDto>> greenCityFuture = CompletableFuture.supplyAsync(
                () -> loadNotificationsFromSource(limit, limit,
                    pageable -> getNotificationsForUserFromGreenCity(pageable, userId, language, null,
                        notificationTypes, Boolean.FALSE)),
                executorService);

            CompletableFuture<List<NotificationDto>> ubsFuture = CompletableFuture.supplyAsync(
                () -> loadNotificationsFromSource(limit, limit,
                    pageable -> getNotificationsForUserFromUbs(principal, pageable, Boolean.FALSE)),
                executorService);

            return Stream.concat(greenCityFuture.join().stream(), ubsFuture.join().stream())
                .filter(dto -> dto.getTime() != null)
                .sorted(sortByRecentNotificationsComparator)
                .limit(limit)
                .toList();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notificationSocket(ActionDto user) {
        Long count = notificationRepo.countByTargetUserIdAndViewedIsFalse(user.getUserId());
        messagingTemplate.convertAndSend(TOPIC + user.getUserId() + NOTIFICATION, count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNotificationForAttenders(List<UserVO> attendersList, String message,
        NotificationType notificationType, Long targetId) {
        createNotificationForAttenders(attendersList, message, notificationType, targetId, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNotificationForAttenders(List<UserVO> attendersList, String message,
        NotificationType notificationType, Long targetId, String title) {
        for (UserVO targetUserVO : attendersList) {
            Notification notification =
                buildBasicNotification(notificationType, targetUserVO, targetId, message, title);
            saveAndNotify(notification);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNotification(UserVO targetUser, UserVO actionUser, NotificationType notificationType) {
        Notification notification = Notification.builder()
            .notificationType(notificationType)
            .projectName(ProjectName.GREENCITY)
            .targetUser(modelMapper.map(targetUser, User.class))
            .time(ZonedDateTime.now())
            .actionUsers(new ArrayList<>(List.of(modelMapper.map(actionUser, User.class))))
            .emailSent(false)
            .build();
        saveAndNotify(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNotification(UserVO targetUserVO, UserVO actionUserVO, NotificationType notificationType,
        Long targetId, String customMessage) {
        Notification notification = findExistingNotification(targetUserVO.getId(), notificationType, targetId, null)
            .orElseGet(() -> buildNotification(notificationType, targetUserVO, targetId, customMessage, null, null));
        updateNotificationWithActionUser(notification, actionUserVO, customMessage);
        saveAndNotify(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNotification(UserVO targetUserVO, UserVO actionUserVO, NotificationType notificationType,
        Long targetId, String customMessage, Long secondMessageId, String secondMessageText) {
        Notification notification =
            findExistingNotification(targetUserVO.getId(), notificationType, targetId, secondMessageId)
                .orElseGet(() -> buildNotification(notificationType, targetUserVO, targetId, customMessage,
                    secondMessageId, secondMessageText));
        updateNotificationWithActionUser(notification, actionUserVO, customMessage);
        saveAndNotify(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNotification(UserVO targetUserVO, UserVO actionUserVO, NotificationType notificationType,
        Long targetId, String customMessage, String secondMessageText) {
        Notification notification = findExistingNotification(targetUserVO.getId(), notificationType, targetId, null)
            .orElseGet(() -> buildNotification(notificationType, targetUserVO, targetId, customMessage, null,
                secondMessageText));
        updateNotificationWithActionUser(notification, actionUserVO, customMessage);
        saveAndNotify(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNewNotification(UserVO targetUserVO, NotificationType notificationType, Long targetId,
        String customMessage) {
        createNewNotification(targetUserVO, notificationType, targetId, customMessage, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNewNotification(UserVO targetUserVO, NotificationType notificationType, Long targetId,
        String customMessage, String secondMessage) {
        Notification notification =
            buildBasicNotification(notificationType, targetUserVO, targetId, customMessage, secondMessage);
        saveAndNotify(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNewNotificationForPlaceAdded(List<UserVO> targetUsers, Long targetId, String customMessage,
        String secondMessage) {
        for (UserVO targetUser : targetUsers) {
            Notification notification = Notification.builder()
                .notificationType(NotificationType.PLACE_ADDED)
                .projectName(ProjectName.GREENCITY)
                .targetUser(modelMapper.map(targetUser, User.class))
                .time(ZonedDateTime.now())
                .targetId(targetId)
                .customMessage(customMessage)
                .secondMessage(secondMessage)
                .emailSent(false)
                .build();
            saveAndNotify(notification);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeActionUserFromNotification(UserVO targetUserVO, UserVO actionUserVO, Long targetId,
        NotificationType notificationType) {
        Notification notification = notificationRepo.findNotificationByTargetUserIdAndNotificationTypeAndIdentifier(
            targetUserVO.getId(), notificationType, targetId);
        if (notification != null) {
            if (notification.getActionUsers().size() == 1) {
                notificationRepo.delete(notification);
                return;
            }
            User user = modelMapper.map(actionUserVO, User.class);
            notification.getActionUsers()
                .removeIf(u -> u.getId().equals(user.getId()));
            notificationRepo.save(notification);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteNotification(Long userId, Long notificationId) {
        if (!notificationRepo.existsByIdAndTargetUserId(notificationId, userId)) {
            throw new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND_BY_ID + notificationId);
        }
        notificationRepo.deleteNotificationByIdAndTargetUserId(notificationId, userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unreadNotification(Long notificationId) {
        Notification notification = notificationRepo.findById(notificationId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND_BY_ID + notificationId));
        Long userId = notification.getTargetUser().getId();
        notificationRepo.markNotificationAsNotViewed(notificationId);
        sendNotification(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void viewNotification(Long notificationId) {
        Notification notification = notificationRepo.findById(notificationId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND_BY_ID + notificationId));
        Long userId = notification.getTargetUser().getId();
        notificationRepo.markNotificationAsViewed(notificationId);
        sendNotification(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Override
    public void checkLastDayOfHabitPrimaryDurationToMessage() {
        habitAssignRepo.getHabitAssignsWithLastDayOfPrimaryDurationToMessage()
            .forEach(habitAssign -> {
                UserVO targetUser = modelMapper.map(habitAssign.getUser(), UserVO.class);
                LanguageDTO language = targetUser.getLanguageVO();
                String habitTitle = habitAssign.getHabit()
                    .getHabitTranslations()
                    .stream()
                    .filter(ht -> ht.getLanguageCode().equals(language.getCode()))
                    .toList()
                    .getFirst()
                    .getName();

                createNewNotification(targetUser, NotificationType.HABIT_LAST_DAY_OF_PRIMARY_DURATION,
                    habitAssign.getId(), habitTitle);
            });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createOrUpdateLikeNotification(LikeNotificationDto likeNotificationDto) {
        boolean isCommentLike = NotificationType.isCommentLike(likeNotificationDto.getNotificationType());
        Optional<Notification> baseNotification = isCommentLike
            ? notificationRepo.findByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalseAndSecondMessageId(
                likeNotificationDto.getTargetUserVO().getId(), likeNotificationDto.getNotificationType(),
                likeNotificationDto.getNewsId(), likeNotificationDto.getSecondMessageId())
            : notificationRepo.findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(
                likeNotificationDto.getTargetUserVO().getId(), likeNotificationDto.getNotificationType(),
                likeNotificationDto.getNewsId());

        baseNotification.ifPresentOrElse(
            notification -> handleExistingLikeNotification(notification, likeNotificationDto),
            () -> handleNewLikeNotification(likeNotificationDto));
    }

    private void handleExistingLikeNotification(Notification notification, LikeNotificationDto likeNotificationDto) {
        List<User> actionUsers = notification.getActionUsers();
        actionUsers.removeIf(user -> user.getId().equals(likeNotificationDto.getActionUserVO().getId()));
        if (likeNotificationDto.isLike()) {
            actionUsers.add(modelMapper.map(likeNotificationDto.getActionUserVO(), User.class));
        }

        if (actionUsers.isEmpty()) {
            notificationRepo.delete(notification);
        } else {
            notification.setCustomMessage(likeNotificationDto.getNewsTitle());
            notification.setTime(ZonedDateTime.now());
            notificationRepo.save(notification);
        }
    }

    private void handleNewLikeNotification(LikeNotificationDto likeNotificationDto) {
        if (likeNotificationDto.isLike()) {
            Notification notification = buildNotification(
                likeNotificationDto.getNotificationType(),
                likeNotificationDto.getTargetUserVO(),
                likeNotificationDto.getNewsId(),
                likeNotificationDto.getNewsTitle(),
                likeNotificationDto.getSecondMessageId(),
                likeNotificationDto.getSecondMessageText());
            notification.getActionUsers().add(modelMapper.map(likeNotificationDto.getActionUserVO(), User.class));
            notification.setTime(ZonedDateTime.now());
            notification.setCustomMessage(likeNotificationDto.getNewsTitle());
            saveAndNotify(notification);
        }
    }

    private PageableAdvancedDto<NotificationDto> getNotificationsForUserFromGreenCity(Pageable page,
        Long userId, String language, ProjectName projectName, List<NotificationType> notificationTypes,
        Boolean viewed) {
        Page<Notification> notificationsPage =
            notificationRepo.findNotificationsByFilter(userId, projectName, notificationTypes, viewed, page);
        return buildPageableAdvancedDto(notificationsPage, language);
    }

    private PageableAdvancedDto<NotificationDto> getNotificationsForUserFromUbs(Principal principal, Pageable page,
        Boolean viewed) {
        PageableAdvancedDto<UbsNotificationDto> notificationsFromUbs =
            restClient.findAllNotificationsForUserFromUbs(principal, page);
        List<NotificationDto> mappedNotifications = mapUbsNotifications(notificationsFromUbs.getPage(), viewed);
        return buildUbsPagedResult(mappedNotifications, notificationsFromUbs);
    }

    /**
     * Maps UBS notifications to NotificationDto objects with optional filtering by
     * viewed status.
     *
     * @param ubsNotifications the list of UBS notifications
     * @param viewed           whether to filter by viewed status
     * @return a list of mapped NotificationDto objects
     */
    private List<NotificationDto> mapUbsNotifications(List<UbsNotificationDto> ubsNotifications, Boolean viewed) {
        Stream<NotificationDto> stream = ubsNotifications.stream()
            .map(ubsNotificationDto -> modelMapper.map(ubsNotificationDto, NotificationDto.class));
        if (viewed != null) {
            stream = stream.filter(dto -> viewed.equals(dto.getViewed()));
        }
        return stream.toList();
    }

    /**
     * Builds a paged result from UBS notifications.
     *
     * @param notifications the list of mapped notifications
     * @param sourcePage    the original page from UBS
     * @return a PageableAdvancedDto containing the paged notifications
     */
    private PageableAdvancedDto<NotificationDto> buildUbsPagedResult(List<NotificationDto> notifications,
        PageableAdvancedDto<UbsNotificationDto> sourcePage) {
        long totalElements = notifications.size() == sourcePage.getPage().size()
            ? sourcePage.getTotalElements()
            : notifications.size();

        int totalPages =
            sourcePage.getPage().isEmpty() ? 0 : (int) Math.ceilDiv(totalElements, sourcePage.getPage().size());

        return PageableAdvancedDto.<NotificationDto>builder()
            .page(notifications)
            .totalElements(totalElements)
            .currentPage(sourcePage.getCurrentPage())
            .totalPages(totalPages)
            .number(sourcePage.getNumber())
            .hasPrevious(sourcePage.isHasPrevious())
            .hasNext(sourcePage.isHasNext())
            .first(sourcePage.isFirst())
            .last(sourcePage.isLast())
            .build();
    }

    private PageableAdvancedDto<NotificationDto> buildPageableAdvancedDto(Page<Notification> notifications,
        String language) {
        List<NotificationDto> notificationDtoList = new LinkedList<>();
        for (Notification notification : notifications) {
            notificationDtoList.add(createNotificationDto(notification, language));
        }
        return new PageableAdvancedDto<>(
            notificationDtoList,
            notifications.getTotalElements(),
            notifications.getPageable().getPageNumber(),
            notifications.getTotalPages(),
            notifications.getNumber(),
            notifications.hasPrevious(),
            notifications.hasNext(),
            notifications.isFirst(),
            notifications.isLast());
    }

    /**
     * Method used to create {@link NotificationDto} from {@link Notification},
     * adding localized notification text.
     *
     * @param notification that should be transformed into dto
     * @param language     language code
     * @return mapped and localized {@link NotificationDto}
     */
    private NotificationDto createNotificationDto(Notification notification, String language) {
        NotificationDto dto = modelMapper.map(notification, NotificationDto.class);
        ResourceBundle bundle = ResourceBundle.getBundle("notification", Locale.forLanguageTag(language),
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT));

        if (NotificationType.isInviteOrRequest(notification.getNotificationType())) {
            dto = mapToNotificationInviteDto(dto, notification);
        }

        dto.setTitleText(bundle.getString(dto.getNotificationType() + "_TITLE"));
        setActionUserDetails(dto, notification);
        dto.setBodyText(generateBodyText(notification, bundle, language));

        if (dto.getMessage() != null && isMessageLocalizationRequired(dto.getNotificationType())) {
            dto.setMessage(localizeMessage(dto.getMessage(), bundle));
        }

        return dto;
    }

    /**
     * Sends a new notification to a specified user.
     *
     * @param userId the ID of the user to whom the notification will be sent
     */
    private void sendNotification(Long userId) {
        long count = notificationRepo.countByTargetUserIdAndViewedIsFalse(userId);
        messagingTemplate.convertAndSend(TOPIC + userId + NOTIFICATION, count);
    }

    /**
     * Saves a notification and sends an email and WebSocket notification.
     *
     * @param notification the notification to save and notify
     * @return the saved notification
     */
    private Notification saveAndNotify(Notification notification) {
        Notification savedNotification = notificationRepo.save(notification);
        notificationService.sendEmailNotification(modelMapper.map(savedNotification, EmailNotificationDto.class));
        sendNotification(savedNotification.getTargetUser().getId());
        return savedNotification;
    }

    /**
     * Builds a basic notification without action users.
     *
     * @param notificationType the type of notification
     * @param targetUserVO     the target user
     * @param targetId         the ID of the target entity
     * @param customMessage    the custom message
     * @param secondMessage    the optional second message
     * @return a new Notification object
     */
    private Notification buildBasicNotification(NotificationType notificationType, UserVO targetUserVO,
        Long targetId, String customMessage, String secondMessage) {
        return Notification.builder()
            .notificationType(notificationType)
            .projectName(ProjectName.GREENCITY)
            .targetUser(modelMapper.map(targetUserVO, User.class))
            .time(ZonedDateTime.now())
            .targetId(targetId)
            .customMessage(customMessage)
            .secondMessage(secondMessage)
            .emailSent(false)
            .build();
    }

    private Notification buildNotification(NotificationType notificationType, UserVO targetUserVO, Long targetId,
        String customMessage, Long secondMessageId, String secondMessageText) {
        return Notification.builder()
            .notificationType(notificationType)
            .projectName(ProjectName.GREENCITY)
            .targetUser(modelMapper.map(targetUserVO, User.class))
            .actionUsers(new ArrayList<>())
            .targetId(targetId)
            .customMessage(customMessage)
            .secondMessageId(secondMessageId)
            .secondMessage(secondMessageText)
            .emailSent(false)
            .build();
    }

    /**
     * Finds an existing notification based on the provided criteria.
     *
     * @param userId           the ID of the target user
     * @param notificationType the type of notification
     * @param targetId         the ID of the target entity
     * @param secondMessageId  the ID of the second message (optional)
     * @return an Optional containing the found notification, or empty if none
     *         exists
     */
    private Optional<Notification> findExistingNotification(Long userId, NotificationType notificationType,
        Long targetId, Long secondMessageId) {
        return secondMessageId != null
            ? notificationRepo.findByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalseAndSecondMessageId(
                userId, notificationType, targetId, secondMessageId)
            : notificationRepo.findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(userId,
                notificationType, targetId);
    }

    /**
     * Updates a notification by adding an action user and setting the time and
     * message.
     *
     * @param notification  the notification to update
     * @param actionUserVO  the user who performed the action
     * @param customMessage the custom message to set
     */
    private void updateNotificationWithActionUser(Notification notification, UserVO actionUserVO,
        String customMessage) {
        notification.getActionUsers().add(modelMapper.map(actionUserVO, User.class));
        notification.setTime(ZonedDateTime.now());
        notification.setCustomMessage(customMessage);
    }

    /**
     * Maps a {@link NotificationDto} to a {@link NotificationInviteDto} and assigns
     * the invitation status.
     *
     * @param dto          the base notification DTO to map from.
     * @param notification the notification entity containing additional data.
     * @return a {@link NotificationInviteDto} with an assigned invitation status.
     */
    private NotificationInviteDto mapToNotificationInviteDto(NotificationDto dto, Notification notification) {
        NotificationInviteDto inviteDto = modelMapper.map(dto, NotificationInviteDto.class);
        InvitationStatus status = getInvitationStatus(notification);
        inviteDto.setStatus(status != null ? status.name() : null);
        return inviteDto;
    }

    /**
     * Retrieves the invitation status based on the type of notification.
     *
     * @param notification the notification object for which the status is being
     *                     retrieved.
     * @return the {@link InvitationStatus} corresponding to the notification's
     *         type. If the notification type doesn't match any of the cases,
     *         returns {@code null}.
     */
    private InvitationStatus getInvitationStatus(Notification notification) {
        return switch (notification.getNotificationType()) {
            case FRIEND_REQUEST_RECEIVED -> notificationFriendService.getFriendRequestStatus(
                notification.getTargetUser().getId(),
                notification.getActionUsers().getFirst().getId());
            case HABIT_INVITE -> habitInvitationService.getHabitInvitationStatus(
                notification.getSecondMessageId());
            default -> null;
        };
    }

    /**
     * Extracts unique action users from the notification and assigns their names
     * and IDs to the DTO.
     *
     * @param dto          the notification DTO where user details should be set.
     * @param notification the notification entity containing the action users.
     */
    private void setActionUserDetails(NotificationDto dto, Notification notification) {
        List<User> uniqueUsers = notification.getActionUsers().stream().distinct().toList();
        dto.setActionUserText(uniqueUsers.stream().map(User::getName).toList());
        dto.setActionUserId(uniqueUsers.stream().map(User::getId).toList());
    }

    /**
     * Generates a localized body text for the notification based on the number of
     * unique action users. Replaces placeholders "{user}" and "{times}"
     * dynamically.
     *
     * @param notification the notification entity to generate text for.
     * @param bundle       the resource bundle containing localized text templates.
     * @param language     the language code for localization.
     * @return the formatted body text with appropriate pluralization.
     */
    private String generateBodyText(Notification notification, ResourceBundle bundle, String language) {
        String bodyTextTemplate = bundle.getString(notification.getNotificationType().toString());
        int uniqueUserCount = new HashSet<>(notification.getActionUsers()).size();

        String bodyText = switch (uniqueUserCount) {
            case 1 -> bodyTextTemplate;
            case 2 -> bodyTextTemplate.replace(USER_PLACEHOLDER, bundle.getString(TWO_USERS));
            default -> bodyTextTemplate.replace(USER_PLACEHOLDER, bundle.getString(THREE_OR_MORE_USERS));
        };

        if (bodyText.contains(TIMES_PLACEHOLDER)) {
            int messagesCount = notification.getActionUsers().size();
            String resolvedTimes = "";

            if (uniqueUserCount == 1) {
                resolvedTimes = language.equals(LANGUAGE_CODE_UA)
                    ? resolveTimesInUkrainian(messagesCount)
                    : resolveTimesInEnglish(messagesCount);
            }

            bodyText = bodyText.replace(TIMES_PLACEHOLDER, resolvedTimes);
        }

        return bodyText;
    }
}