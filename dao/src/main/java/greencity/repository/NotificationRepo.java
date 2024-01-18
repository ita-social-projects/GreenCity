package greencity.repository;

import greencity.entity.Notification;
import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    /**
     * This method should return 3 last unread notifications.
     *
     * @param targetUserId user, which should return notifications
     * @return 3 last Notifications for user, where viewed is false
     */
    List<Notification> findTop3ByTargetUserIdAndViewedFalseOrderByTimeDesc(Long targetUserId);

    /**
     * This method should return page of notifications.
     *
     * @param targetUserId user, which should return notification
     * @param pageable     page of notifications
     * @return Notifications for user, where viewed
     */
    Page<Notification> findByTargetUserId(Long targetUserId, Pageable pageable);

    /**
     * This method should return page of notifications, sorted by time and filtered.
     *
     * @param targetUserId      user, which should return notification
     * @param projectNames      array of {@link ProjectName} to filter notifications
     * @param notificationTypes array of {@link NotificationType} to filter
     *                          notifications
     * @param pageable          page of notifications
     * @return last Notifications for user, where viewed is false
     */
    Page<Notification> findByTargetUserIdAndProjectNameInAndNotificationTypeInOrderByTimeDesc(Long targetUserId,
        ProjectName[] projectNames, NotificationType[] notificationTypes, Pageable pageable);

    /**
     * This method return unread notification.
     *
     * @param targetUserId user, which should return notification
     * @return Notification for user, where viewed is false
     */
    Optional<Notification> findNotificationByTargetUserIdAndViewedIsFalse(Long targetUserId);

    /**
     * This method is used to check is notification of specific type is present.
     *
     * @param targetUserId     user, which should return Notification
     * @param notificationType Type of the notification
     * @return List of {@link Notification} of specific type
     */
    List<Notification> findByTargetUserIdAndNotificationTypeAndViewedIsFalse(Long targetUserId,
        NotificationType notificationType);

    /**
     * Changes {@link Notification} `viewed` as true.
     *
     * @param notificationId to change
     */
    @Transactional
    @Modifying
    @Query("UPDATE Notification n SET n.viewed = true WHERE n.id = :notificationId")
    void markNotificationAsViewed(Long notificationId);

    /**
     * method to get single {@link Notification}.
     *
     * @param notificationId id of searched Notification
     * @param targetUserId   id of user
     * @return {@link Notification}
     */
    Notification findByIdAndTargetUserId(Long notificationId, Long targetUserId);

    /**
     * This method is used to check is notification of specific type is present.
     *
     * @param targetUserId     user, which should return Notification
     * @param notificationType Type of the notification
     * @param eventCommentId   id of EventComment
     * @return List of {@link Notification} of specific type
     */
    Notification findByTargetUserIdAndNotificationTypeAndEventCommentId(Long targetUserId,
        NotificationType notificationType, Long eventCommentId);


    /**
     * This method is used to check is notification of specific type is present.
     *
     * @param targetUserId     user, which should return Notification
     * @param notificationType Type of the notification
     * @param ecoNewsCommentId  id of EcoNewsComment
     * @return List of {@link Notification} of specific type
     */
    Notification findByTargetUserIdAndNotificationTypeAndEcoNewsCommentId(Long targetUserId,
                                                                        NotificationType notificationType, Long ecoNewsCommentId);

    void deleteAllByEventCommentId(Long eventCommentId);

    void deleteAllByEcoNewsCommentId(Long ecoNewsCommentId);
}
