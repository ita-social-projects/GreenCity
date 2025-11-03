package greencity.dto.emailpreference;

import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;

public record EmailPreferenceDto(
    String userEmail,
    EmailPreference emailPreference,
    EmailPreferencePeriodicity emailPreferencePeriodicity) {
}
