package greencity.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DateTimeService {
    /**
     * Get current date and time in zone.
     *
     * @param zoneId - zone id.
     * @return LocalDateTime object.
     * @author Nazar Vladyka.
     */
    public static LocalDateTime getDateTime(String zoneId) {
        log.info("in getDateTime(String zoneId), get time in timezone - {}", zoneId);

        return LocalDateTime.now(ZoneId.of(zoneId)).withSecond(0).withNano(0);
    }
}
