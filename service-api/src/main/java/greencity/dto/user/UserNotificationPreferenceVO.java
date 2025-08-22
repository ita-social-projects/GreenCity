package greencity.dto.user;

import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserNotificationPreferenceVO {
    private Long id;

    private UserVO user;

    private EmailPreference emailPreference;

    private EmailPreferencePeriodicity periodicity;
}
