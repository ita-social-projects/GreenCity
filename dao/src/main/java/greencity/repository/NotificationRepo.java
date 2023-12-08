package greencity.repository;

import greencity.entity.Notification;
import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    /**
     * This method should return 3 last unread notifications.
     * 
     * @param targetUserId user, which should return notification
     * @return 3 last Notifications for user, where viewed is false
     */
    List<Notification> findTop3ByTargetUserIdAndViewedFalseOrderByTimeDesc(Long targetUserId);

    /**
     * This method should return page of notifications, sorted by time.
     *
     * @param targetUserId user, which should return notification
     * @param pageable     page of notifications
     * @return 3 last Notifications for user, where viewed is false
     */
    Page<Notification> findByTargetUserId(Long targetUserId, Pageable pageable);

    /**
     * This method should return page of notifications, sorted by time. Returns only
     * if
     *
     * @param targetUserId      user, which should return notification
     * @param projectNames      array of {@link ProjectName} to filter notifications
     * @param notificationTypes array of {@link NotificationType} to filter
     *                          notifications
     * @param pageable          page of notifications
     * @return 3 last Notifications for user, where viewed is false
     */
    Page<Notification> findByTargetUserIdAndProjectNameInAndNotificationTypeInOrderByTimeDesc(Long targetUserId,
        ProjectName[] projectNames, NotificationType[] notificationTypes, Pageable pageable);
}
