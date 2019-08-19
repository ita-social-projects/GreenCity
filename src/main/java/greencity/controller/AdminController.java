package greencity.controller;

import greencity.dto.place.PlaceStatusDto;
import greencity.services.OpeningHoursService;
import greencity.services.PlaceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private final PlaceService placeService;
    private final OpeningHoursService openingHoursService;

    @PostMapping(value = "/proposed")
    @ResponseBody
    public ResponseEntity changePlaceStatus(@Validated @RequestBody PlaceStatusDto placeStatusDto) {
        placeService.updateStatus(placeStatusDto.getId(), placeStatusDto.getStatus());

        return new ResponseEntity(HttpStatus.OK);
    }
}
