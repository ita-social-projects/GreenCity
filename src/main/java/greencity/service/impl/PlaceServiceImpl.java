package greencity.service.impl;

import greencity.dto.place.PlaceAddDto;
import greencity.entity.Place;
import greencity.exception.BadIdException;
import greencity.repository.PlaceRepo;
import greencity.service.PlaceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PlaceServiceImpl implements PlaceService {

  private PlaceRepo placeRepo;

  private PlaceService placeService;

  @Override
  public Place save(PlaceAddDto place) {
    return null;
  }

  @Override
  public Place update(Place place) {
    return null;
  }

  @Override
  public Place findByAddress(String address) {
    return placeRepo.findByAddress(address);
  }

  @Override
  public void deleteById(Long id) {
    Place place = findById(id);
    placeRepo.delete(place);
  }

  @Override
  public Place findById(Long id) {
    return placeRepo
        .findById(id)
        .orElseThrow(() -> new BadIdException("No place with this id:" + id));
  }

  @Override
  public List<Place> findAll() {
    return null;
  }
}
