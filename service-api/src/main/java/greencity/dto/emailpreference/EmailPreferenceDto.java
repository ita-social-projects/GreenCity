package greencity.dto.emailpreference;

import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;

public record EmailPreferenceDto(
    Long userId,
    EmailPreference emailPreference,
    EmailPreferencePeriodicity emailPreferencePeriodicity) {
}
