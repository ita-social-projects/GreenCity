package greencity.service;

import greencity.entity.OpeningHours;
import java.util.List;

public interface OpeningHoursService {
    List<OpeningHours> findAll();

    OpeningHours findById(Long id);

    OpeningHours save(OpeningHours hours);

    OpeningHours update(Long id, OpeningHours updatedHours);

    void deleteById(Long id);
}
