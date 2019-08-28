package greencity.controller;

import greencity.dto.place.PlaceAddDto;
import greencity.service.PlaceService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/place")
@AllArgsConstructor
public class PlaceController {

    private PlaceService placeService;

    @PostMapping("/propose")
    public void proposePlace(@Valid @RequestBody PlaceAddDto dto) {
        placeService.save(dto);
    }

}
