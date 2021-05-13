package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import greencity.service.SocialNetworkImageService;
import greencity.service.SocialNetworkService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/social-networks")
public class SocialNetworkController {

    private final SocialNetworkImageService socialNetworkImageService;
    private final SocialNetworkService socialNetworkService;

    /**
     * Method for finding social network image.
     *
     * @param url social network url.
     * @return {@link SocialNetworkImageVO}
     */
    @ApiOperation(value = "Get social network image")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/image")
    public ResponseEntity<SocialNetworkImageVO> getSocialNetworkImageByUrl(@RequestParam String url) {
        return ResponseEntity.status(HttpStatus.OK).body(socialNetworkImageService.getSocialNetworkImageByUrl(url));
    }

    /**
     * Method for delete social network by id.
     *
     * @param id social network url.
     * @return {@link SocialNetworkImageVO}
     */
    @ApiOperation(value = "Delete social network by id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping("")
    public ResponseEntity<Long> deleteSocialNetwork(@RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(socialNetworkService.delete(id));
    }
}
