package greencity.services;

import greencity.entities.OpeningHours;
import java.util.List;

public interface OpeningHoursService {
    List<OpeningHours> findAll();

    OpeningHours findById(Long id);

    OpeningHours save(OpeningHours hours);

    OpeningHours update(Long id, OpeningHours updatedHours);

    void delete(OpeningHours hours);
}
