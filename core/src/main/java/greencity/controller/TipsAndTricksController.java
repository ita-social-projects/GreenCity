package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.ImageValidation;
import greencity.annotations.ValidTipsAndTricksDtoRequest;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.entity.TipsAndTricks;
import greencity.service.TipsAndTricksService;
import greencity.service.TipsAndTricksTagsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/tipsandtricks")
public class TipsAndTricksController {
    private final TipsAndTricksService tipsAndTricksService;
    private final TipsAndTricksTagsService tipsAndTricksTagsService;

    /**
     * Method for creating {@link TipsAndTricks}.
     *
     * @param tipsAndTricksDtoRequest - dto for {@link TipsAndTricks} entity.
     * @return dto {@link TipsAndTricksDtoResponse} instance.
     */

    @ApiOperation(value = "Add new tips & tricks.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED,
            response = TipsAndTricksDtoResponse.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<TipsAndTricksDtoResponse> save(
        @ApiParam(value = "Add tips & tricks request", required = true)
        @ImageValidation
        @RequestPart @ValidTipsAndTricksDtoRequest TipsAndTricksDtoRequest tipsAndTricksDtoRequest,
        @ApiParam(value = "Tips & tricks image")
        @RequestPart(required = false) MultipartFile image,
        @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            tipsAndTricksService.save(tipsAndTricksDtoRequest, image, principal.getName()));
    }

    /**
     * Method for getting tips & tricks by id.
     *
     * @return {@link TipsAndTricksDtoResponse} instance.
     */
    @ApiOperation(value = "Get tips & tricks by id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/{id}")
    public ResponseEntity<TipsAndTricksDtoResponse> getTipsAndTricksById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(tipsAndTricksService.findDtoById(id));
    }

    /**
     * Method for getting all tips & tricks by page.
     *
     * @return PageableDto of {@link TipsAndTricksDtoResponse} instances.
     */
    @ApiOperation(value = "Find all tips & tricks by page.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("")
    @ApiPageable
    public ResponseEntity<PageableDto<TipsAndTricksDtoResponse>> findAll(@ApiIgnore Pageable page) {
        return ResponseEntity.status(HttpStatus.OK).body(tipsAndTricksService.findAll(page));
    }

    /**
     * Method for deleting {@link TipsAndTricks} by its id.
     *
     * @param id {@link TipsAndTricks} which will be deleted.
     * @return id of deleted {@link TipsAndTricks}.
     */
    @ApiOperation(value = "Delete tips & tricks.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        tipsAndTricksService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for getting all tips & tricks by tags.
     *
     * @return list of {@link TipsAndTricksDtoResponse} instances.
     */
    @ApiOperation(value = "Get tips & tricks by tags")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/tags")
    @ApiPageable
    public ResponseEntity<PageableDto<TipsAndTricksDtoResponse>> getTipsAndTricks(
        @ApiIgnore Pageable page,
        @ApiParam(value = "Tags to filter (if no tags, get all)")
        @RequestParam(required = false) List<String> tags
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(tipsAndTricksService.find(page, tags));
    }

    /**
     * The method which returns all tips & tricks tags.
     *
     * @return list of {@link String} (tag's names).
     */
    @GetMapping("/tipsandtricksTags")
    public ResponseEntity<List<String>> findAllTipsAndTricksTags() {
        return ResponseEntity.status(HttpStatus.OK).body(tipsAndTricksTagsService.findAll());
    }
}