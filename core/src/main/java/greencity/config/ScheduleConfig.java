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

import static greencity.entity.enums.FactOfDayStatus.USED;
import static greencity.entity.enums.FactOfDayStatus.CURRENT;
import static greencity.entity.enums.FactOfDayStatus.POTENTIAL;
import static greencity.constant.CacheConstants.FACT_OF_DAY_CACHE_NAME;

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
