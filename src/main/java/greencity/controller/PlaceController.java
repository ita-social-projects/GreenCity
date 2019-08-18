package greencity.controller;

import greencity.dto.place.PlaceAddDto;
import greencity.service.PlaceService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/place")
@AllArgsConstructor
public class PlaceController {

    private PlaceService placeService;

    @PostMapping("/create")
    public void proposeNewPlace(PlaceAddDto dto) {
placeService.save(dto);
    }
}
