package greencity.dto.filter;

import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterNotificationDto {
    private ProjectName[] projectName = {};
    private NotificationType[] notificationType = {};
}
