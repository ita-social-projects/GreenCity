package greencity.controller;

import greencity.dto.location.LocationByBoundsDto;
import greencity.dto.location.MapBoundsDto;
import greencity.service.LocationService;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/location")
public class LocationController {
  private LocationService locationService;


  @PostMapping("/getListPlaceLocationByMapsBounds")
  public List<LocationByBoundsDto> getListPlaceLocationByMapsBounds(@Valid @RequestBody MapBoundsDto mapBoundsDto) {
    return locationService.findPlacesLocationByMapsBounds(mapBoundsDto);
  }
}
