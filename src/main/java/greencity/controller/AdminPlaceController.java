package greencity.controller;

import greencity.dto.place.AdminPlaceDto;
import greencity.entity.enums.PlaceStatus;
import greencity.service.PlaceService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/places")
@AllArgsConstructor
public class AdminPlaceController {

    private PlaceService placeService;

    @PostMapping
    public ResponseEntity<?> getPlacesByStatus(@RequestParam String status) {
        PlaceStatus placeStatus = PlaceStatus.valueOf(status.toUpperCase());
        List<AdminPlaceDto> places = placeService.getPlacesByStatus(placeStatus);
        return ResponseEntity.ok(places);
    }
}
