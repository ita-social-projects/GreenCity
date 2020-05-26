package greencity.config;

import greencity.entity.FactTranslation;
import greencity.repository.FactTranslationRepo;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import greencity.entity.User;
import greencity.message.SendHabitNotification;
import greencity.repository.HabitRepo;
import greencity.repository.UserRepo;
import java.time.ZonedDateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import static greencity.entity.enums.FactOfDayStatus.USED;
import static greencity.entity.enums.FactOfDayStatus.CURRENT;
import static greencity.entity.enums.FactOfDayStatus.POTENTIAL;
import static greencity.constant.CacheConstants.FACT_OF_DAY_CACHE_NAME;
import static greencity.constant.RabbitConstants.EMAIL_TOPIC_EXCHANGE_NAME;
import static greencity.constant.RabbitConstants.SEND_HABIT_NOTIFICATION_ROUTING_KEY;
import static greencity.entity.enums.EmailNotification.IMMEDIATELY;
import static greencity.entity.enums.EmailNotification.DAILY;
import static greencity.entity.enums.EmailNotification.WEEKLY;
import static greencity.entity.enums.EmailNotification.MONTHLY;



/**
 * Config for scheduling.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@Configuration
@EnableScheduling
@EnableCaching
@AllArgsConstructor
public class ScheduleConfig {
    private final FactTranslationRepo factTranslationRepo;
    private final HabitRepo habitRepo;
    private final RabbitTemplate rabbitTemplate;
    private final UserRepo userRepo;

    /**
     * Invoke {@link sendHabitNotification} from EmailMessageReceiver to send email letters
     * to each user that hasn't marked any habit during last 3 days.
     *
     * @param users     list of potential {@link User} to send notifications.
     */
    private void sendHabitNotificationIfNeed(List<User> users) {
        ZonedDateTime end = ZonedDateTime.now();
        ZonedDateTime start = end.minusDays(3);
        for (User user : users) {
            int count = habitRepo.countMarkedHabitsByUserIdByPeriod(user.getId(), start, end);
            if (count == 0) {
                rabbitTemplate.convertAndSend(
                    EMAIL_TOPIC_EXCHANGE_NAME,
                    SEND_HABIT_NOTIFICATION_ROUTING_KEY,
                    new SendHabitNotification(user.getName(), user.getEmail())
                );
            }
        }
    }

    /**
     * Every day at 19:00 sends notifications about not marked habits to users with field
     * {@link greencity.entity.enums.EmailNotification} equal to IMMEDIATELY or DAILY.
     */
    @Scheduled(cron = "0 0 19 * * *")
    void sendHabitNotificationEveryDay() {
        List<User> users = userRepo.findAllByEmailNotification(IMMEDIATELY);
        users.addAll(userRepo.findAllByEmailNotification(DAILY));
        sendHabitNotificationIfNeed(users);
    }

    /**
     * Every friday at 19:00 sends notifications about not marked habits to users with field
     * {@link greencity.entity.enums.EmailNotification} equal to WEEKLY.
     */
    @Scheduled(cron = "0 0 19 * * FRI")
    void sendHabitNotificationEveryWeek() {
        List<User> users = userRepo.findAllByEmailNotification(WEEKLY);
        sendHabitNotificationIfNeed(users);
    }

    /**
     * On th 25th of every month at 19:00 sends notifications about not marked habits to users with field
     * {@link greencity.entity.enums.EmailNotification} equal to MONTHLY.
     */
    @Scheduled(cron = "0 0 19 25 * *")
    void sendHabitNotificationEveryMonth() {
        List<User> users = userRepo.findAllByEmailNotification(MONTHLY);
        sendHabitNotificationIfNeed(users);

    /**
     * Once a day randomly chooses new fact of day that has not been fact of day during this iteration.
     * factOfDay == 0 - wasn't fact of day, 1 - is today's fact of day, 2 - already was fact of day.
     */
    @CacheEvict(value = FACT_OF_DAY_CACHE_NAME, allEntries = true)
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void chooseNewFactOfTheDay() {
        Optional<List<FactTranslation>> list = factTranslationRepo.findRandomFact();
        if (list.isPresent()) {
            factTranslationRepo.updateFactOfDayStatus(CURRENT, USED);
        } else {
            factTranslationRepo.updateFactOfDayStatus(USED, POTENTIAL);
            factTranslationRepo.updateFactOfDayStatus(CURRENT, USED);
            list = factTranslationRepo.findRandomFact();
        }
        factTranslationRepo.updateFactOfDayStatusByHabitfactId(CURRENT, list.get().get(0).getHabitFact().getId());
    }
}
