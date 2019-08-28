package greencity.service;

import greencity.dto.openingHours.OpeningHoursDto;
import greencity.entity.OpeningHours;

import java.util.List;

public interface OpeningHoursService {

    OpeningHours save(OpeningHours openingHours);

    OpeningHours update(OpeningHours openingHours);

    OpeningHours findById(Long id);

    List<OpeningHours> findAll();

    void deleteById(Long id);
}
