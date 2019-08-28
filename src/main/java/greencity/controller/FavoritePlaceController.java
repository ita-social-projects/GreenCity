package greencity.controller;

import greencity.dto.favoritePlace.FavoritePlaceDto;
import greencity.dto.favoritePlace.FavoritePlaceUpdateDto;
import greencity.service.FavoritePlaceService;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/favorite_place")
@AllArgsConstructor
public class FavoritePlaceController {
    private final FavoritePlaceService favoritePlaceService;

    @PostMapping
    public ResponseEntity<FavoritePlaceDto> save(@Valid @RequestBody FavoritePlaceDto favoritePlaceDto) {
        return new ResponseEntity(favoritePlaceService.save(favoritePlaceDto), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<FavoritePlaceDto> update(@Valid @RequestBody FavoritePlaceUpdateDto favoritePlaceUpdateDto) {
        return new ResponseEntity(favoritePlaceService.update(favoritePlaceUpdateDto), HttpStatus.OK);
    }

//    @PostMapping("/findById")
//    public ResponseEntity<FavoritePlaceDto> findById(@RequestParam Long id) {
//        return new ResponseEntity(favoritePlaceService.findById(id), HttpStatus.OK);
//    }

    @GetMapping
    public ResponseEntity<List<FavoritePlaceDto>>findAllByUserEmail(@Valid @RequestParam String email) {
        return new ResponseEntity<>(favoritePlaceService.findAllByUserEmail(email), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteByPlaceIdAndUserEmail(@Valid @RequestBody FavoritePlaceDto favoritePlaceDto) {
        favoritePlaceService.deleteByPlaceIdAndUserEmail(favoritePlaceDto);
        return new ResponseEntity(HttpStatus.OK);

    }
}


