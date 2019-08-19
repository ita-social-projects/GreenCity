package greencity.service.impl;

import greencity.dto.openingHours.OpeningHoursDto;
import greencity.entity.OpeningHours;
import greencity.exception.BadIdException;
import greencity.repository.OpeningHoursRepo;
import greencity.service.OpeningHoursService;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OpeningHoursServiceImpl implements OpeningHoursService {

    private OpeningHoursRepo openingHoursRepo;

    @Override
    public OpeningHours save(OpeningHours openingHours) {
        return openingHoursRepo.save(openingHours);
    }

    @Override
    public OpeningHours update(OpeningHours location) {
        return null;
    }

    @Override
    public OpeningHours findById(Long id) {
        return openingHoursRepo
                .findById(id)
                .orElseThrow(() -> new BadIdException("No location with this id: " + id));
    }

    @Override
    public List<OpeningHours> findAll() {
        return openingHoursRepo.findAll();
    }

    @Override
    public void deleteById(Long id) {
        OpeningHours openingHours = findById(id);
        openingHoursRepo.delete(openingHours);
    }
}
