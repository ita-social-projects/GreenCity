package greencity.config;

import static greencity.enums.EmailNotification.*;
import static greencity.enums.FactOfDayStatus.*;

import greencity.client.RestClient;
import greencity.constant.CacheConstants;
import greencity.dto.user.UserVO;
import greencity.entity.HabitFactTranslation;
import greencity.entity.User;
import greencity.message.SendHabitNotification;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitFactTranslationRepo;
import greencity.repository.RatingStatisticsRepo;
import greencity.repository.UserRepo;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

/**
 * Config for scheduling.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@Configuration
@EnableScheduling
@AllArgsConstructor
public class ScheduleConfig {
    private final HabitFactTranslationRepo habitFactTranslationRepo;
    private final HabitAssignRepo habitAssignRepo;
    private final RatingStatisticsRepo ratingStatisticsRepo;
    private final RestClient restClient;

    /**
     * Invoke {@link SendHabitNotification} from EmailMessageReceiver to send email
     * letters to each user that hasn't marked any habit during last 3 days.
     *
     * @param users list of potential {@link User} to send notifications.
     */
    private void sendHabitNotificationIfNeed(List<UserVO> users) {
        ZonedDateTime end = ZonedDateTime.now();
        ZonedDateTime start = end.minusDays(3);
        for (UserVO user : users) {
            int count = habitAssignRepo.countMarkedHabitAssignsByUserIdAndPeriod(user.getId(), start, end);
            if (count == 0) {
                restClient.sendHabitNotification(new SendHabitNotification(user.getName(), user.getEmail()));
            }
        }
    }

    /**
     * Every day at 19:00 sends notifications about not marked habits to users with
     * field {@link greencity.enums.EmailNotification} equal to IMMEDIATELY or
     * DAILY.
     */
    @Scheduled(cron = "0 0 19 * * ?", zone = "Europe/Kiev")
    void sendHabitNotificationEveryDay() {
        List<UserVO> users = restClient.findAllByEmailNotification(IMMEDIATELY);
        users.addAll(restClient.findAllByEmailNotification(DAILY));
    }

    /**
     * Every friday at 19:00 sends notifications about not marked habits to users
     * with field {@link greencity.enums.EmailNotification} equal to WEEKLY.
     */
    @Scheduled(cron = "0 0 19 * * FRI", zone = "Europe/Kiev")
    void sendHabitNotificationEveryWeek() {
        List<UserVO> users = restClient.findAllByEmailNotification(WEEKLY);
        sendHabitNotificationIfNeed(users);
    }

    /**
     * On th 25th of every month at 19:00 sends notifications about not marked
     * habits to users with field {@link greencity.enums.EmailNotification} equal to
     * MONTHLY.
     */
    @Scheduled(cron = "0 0 19 25 * ?", zone = "Europe/Kiev")
    void sendHabitNotificationEveryMonth() {
        List<UserVO> users = restClient.findAllByEmailNotification(MONTHLY);
        sendHabitNotificationIfNeed(users);
    }

    /**
     * Once a day randomly chooses new habitfact of day that has not been habitfact
     * of day during this iteration. factOfDay == 0 - wasn't habitfact of day, 1 -
     * is today's habitfact of day, 2 - already was habitfact of day.
     */
    @CacheEvict(value = CacheConstants.HABIT_FACT_OF_DAY_CACHE, allEntries = true)
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Kiev")
    public void chooseNewHabitFactOfDay() {
        List<HabitFactTranslation> list = habitFactTranslationRepo.findRandomHabitFact();
        if (!list.isEmpty()) {
            habitFactTranslationRepo.updateFactOfDayStatus(CURRENT, USED);
        } else {
            habitFactTranslationRepo.updateFactOfDayStatus(USED, POTENTIAL);
            habitFactTranslationRepo.updateFactOfDayStatus(CURRENT, USED);
            list = habitFactTranslationRepo.findRandomHabitFact();
        }
        habitFactTranslationRepo.updateFactOfDayStatusByHabitFactId(CURRENT, list.get(0).getHabitFact().getId());
    }

    /**
     * Clear habitfact of the day cache at 0:00 am every day.
     */
    @CacheEvict(value = CacheConstants.FACT_OF_THE_DAY_CACHE_NAME, allEntries = true)
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Kiev")
    public void chooseNewHabitFactOfTheDay() {
        // Do nothing to clean cache
    }

    /**
     * Every day at 00:00 deletes from the database users that have status
     * 'DEACTIVATED' and last visited the site 2 years ago.
     *
     * @author Vasyl Zhovnir
     **/
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Kiev")
    @Transactional
    public void scheduleDeleteDeactivatedUsers() {
        restClient.scheduleDeleteDeactivatedUsers();
    }

    /**
     * Every day at 00:00 deletes from the table rating_statistics records witch are
     * older than period in application properties.
     *
     * @author Dovganyuk Taras
     **/
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Kiev")
    @Transactional
    public void scheduledDeleteRatingStatisticsOlderThan() {
        ratingStatisticsRepo.scheduledDeleteOlderThan();
    }
}
