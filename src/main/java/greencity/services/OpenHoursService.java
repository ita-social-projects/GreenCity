package greencity.services;

import greencity.entities.OpeningHours;
import java.util.List;

public interface OpenHoursService {
    List<OpeningHours> getOpenHours(Long placeId);
}
