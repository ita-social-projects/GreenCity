package greencity.service.impl;

import greencity.dto.place.PlaceAddDto;
import greencity.entity.Category;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class PlaceServiceImpl implements PlaceService {

    private PlaceRepo placeRepo;

    private CategoryService categoryService;

    private LocationService locationService;

    private OpeningHoursService openingHoursService;

    private UserService userService;

    @Transactional
    @Override
    public Place save(PlaceAddDto dto) {
        log.info("In save place method");

        Category category = createCategoryByName(dto);

        Place place =
                placeRepo.save(
                        Place.builder()
                                .name(dto.getName())
                                .category(
                                        categoryService.findById(
                                                dto.getCategoryId() == null
                                                        ? category.getId()
                                                        : dto.getCategoryId()))
                                .author(userService.findById(dto.getAuthorId()))
                                .status(dto.getPlaceStatus())
                                .build());

        saveOpeningHoursWithPlace(dto, place);

        saveLocationWithPlace(dto, place);

        return place;
    }

    private void saveLocationWithPlace(PlaceAddDto dto, Place place) {
        Location location =
                locationService.save(
                        Location.builder()
                                .lat(dto.getLocation().getLat())
                                .lng(dto.getLocation().getLng())
                                .address(dto.getAddress())
                                .place(place)
                                .build());
    }

    private void saveOpeningHoursWithPlace(PlaceAddDto dto, Place place) {
        dto.getOpeningHoursDtoList()
                .forEach(
                        openingHoursDto -> {
                            OpeningHours openingHours =
                                    OpeningHours.builder()
                                            .openTime(openingHoursDto.getOpenTime())
                                            .closeTime(openingHoursDto.getCloseTime())
                                            .weekDays(openingHoursDto.getWeekDays())
                                            .place(place)
                                            .build();
                            openingHoursService.save(openingHours);
                        });
    }

    private Category createCategoryByName(PlaceAddDto dto) {
        Category category = new Category();

        if (dto.getCategoryId() == null) {
            category = categoryService.save(Category.builder().name(dto.getCategoryName()).build());
        }
        return category;
    }

    @Override
    public Boolean deleteById(Long id) {
        log.info("In deleteById() place method.");
        Place place = findById(id);
        placeRepo.delete(place);
        log.info("This place was deleted.");
        return true;
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

    @Override
    public Place update(Place place) {
        return null;
    }
}
