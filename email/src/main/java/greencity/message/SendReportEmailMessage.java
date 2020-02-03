package greencity.message;

import greencity.dto.category.CategoryDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.user.PlaceAuthorDto;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class SendReportEmailMessage implements Serializable {
    private List<PlaceAuthorDto> subscribers;
    private Map<CategoryDto, List<PlaceNotificationDto>> categoriesDtoWithPlacesDtoMap;
    private String emailNotification;
}
