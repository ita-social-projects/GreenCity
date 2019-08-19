package greencity.service.impl;

import greencity.dto.place.PlaceAddDto;
import greencity.entity.Location;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.exception.BadIdException;
import greencity.exception.BadPlaceRequestException;
import greencity.repository.PlaceRepo;
import greencity.service.CategoryService;
import greencity.service.LocationService;
import greencity.service.OpeningHoursService;
import greencity.service.PlaceService;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class PlaceServiceImpl implements PlaceService {

    private PlaceRepo placeRepo;

    private PlaceService placeService;

    private CategoryService categoryService;

    private LocationService locationService;

    private OpeningHoursService openingHoursService;

    @Override
    public Place save(PlaceAddDto dto) {
        log.info("In save method");
        Place byAddress = placeService.findByAddress(dto.getAddress());
        if (byAddress != null) {
            log.info("Place exists be address.");
            throw new BadPlaceRequestException("Place by this address already exist.");
        }
        log.info("Place ready for saving");
        Location location =
                locationService.save(
                        Location.builder()
                                .lat(dto.getLocationDto().getLat())
                                .lng(dto.getLocationDto().getLng())
                                .build());
        Place place =
                placeRepo.save(
                        Place.builder()
                                .name(dto.getName())
                                .address(dto.getAddress())
                                .category(categoryService.findById(dto.getCategoryId()))
                                .location(location)
                                .placeType(dto.getPlaceType())
                                .status(PlaceStatus.PROPOSED)
                                .build());
        dto.getOpeningHoursDtoList()
                .forEach(
                        openingHoursDto -> {
                            OpeningHours openingHours = new OpeningHours();
                            openingHours.setOpenTime(openingHoursDto.getOpenTime());
                            openingHours.setCloseTime(openingHoursDto.getCloseTime());
                            openingHours.setWeekDays(openingHoursDto.getWeekDays());
                            openingHours.setPlace(place);
                            openingHoursService.save(openingHours);
                        });

        return place;
    }

    @Override
    public Place update(Place place) {
        return null;
    }

    @Override
    public Place findByAddress(String address) {
        log.info("In findByAddress() method.");
        return placeRepo.findByAddress(address);
    }

    @Override
    public void deleteById(Long id) {
        log.info("In deleteById() method.");
        Place place = findById(id);
        placeRepo.delete(place);
        log.info("This place was deleted.");
    }

    @Override
    public Place findById(Long id) {
        log.info("In findById() method.");
        return placeRepo
                .findById(id)
                .orElseThrow(() -> new BadIdException("No place with this id:" + id));
    }

    @Override
    public List<Place> findAll() {
        log.info("In findAll() method.");
        return placeRepo.findAll();
    }
}
