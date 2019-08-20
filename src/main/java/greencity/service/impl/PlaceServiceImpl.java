package greencity.service.impl;

import greencity.dto.place.PlaceAddDto;
import greencity.entity.Location;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.exception.BadIdException;
import greencity.exception.BadPlaceRequestException;
import greencity.repository.PlaceRepo;
import greencity.service.*;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class PlaceServiceImpl implements PlaceService {

    private PlaceRepo placeRepo;

    private CategoryService categoryService;

    private LocationService locationService;

    private OpeningHoursService openingHoursService;

    private UserService userService;

    @Override
    public Place save(PlaceAddDto dto) {
        log.info("In save place method");
        Place byAddress = placeRepo.findByAddress(dto.getAddress());
        if (byAddress != null) {
            throw new BadPlaceRequestException("Place by this address already exist.");
        }

        Place place =
                placeRepo.save(
                        Place.builder()
                                .name(dto.getName())
                                .address(dto.getAddress())
                                .category(categoryService.findById(dto.getCategoryId()))
                                .author(userService.findById(dto.getAuthorId()))
                                .status(dto.getPlaceStatus())
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

        Location location =
                locationService.save(
                        Location.builder()
                                .lat(dto.getLocationDto().getLat())
                                .lng(dto.getLocationDto().getLng())
                                .place(place)
                                .build());

        return place;
    }

    @Override
    public Place update(Place place) {
        return null;
    }

    @Override
    public Place findByAddress(String address) {
        log.info("In findByAddress() place method.");
        return placeRepo.findByAddress(address);
    }

    @Override
    public void deleteById(Long id) {
        log.info("In deleteById() place method.");
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
        log.info("In findAll() place method.");
        return placeRepo.findAll();
    }
}
