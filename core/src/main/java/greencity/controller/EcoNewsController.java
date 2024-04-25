package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.annotations.ImageValidation;
import greencity.annotations.ValidEcoNewsDtoRequest;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.econews.*;
import greencity.dto.tag.TagDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.service.EcoNewsService;
import greencity.service.TagsService;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/econews")
@RequiredArgsConstructor
public class EcoNewsController {
    private final EcoNewsService ecoNewsService;
    private final TagsService tagService;

    /**
     * Method for getting three last eco news.
     *
     * @return list of {@link EcoNewsDto} instances.
     * @author Yuriy Olkhovskyi.
     */
    @Operation(summary = "Get three last eco news.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/newest")
    public ResponseEntity<List<EcoNewsDto>> getThreeLastEcoNews() {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.getThreeLastEcoNews());
    }

    /**
     * Method for creating {@link EcoNewsVO}.
     *
     * @param addEcoNewsDtoRequest - dto for {@link EcoNewsVO} entity.
     * @return dto {@link AddEcoNewsDtoResponse} instance.
     * @author Yuriy Olkhovskyi & Kovaliv Taras.
     */
    @Operation(summary = "Add new eco news.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
            content = @Content(schema = @Schema(implementation = EcoNewsGenericDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EcoNewsGenericDto> save(
        @Parameter(description = SwaggerExampleModel.ADD_ECO_NEWS_REQUEST,
            required = true) @RequestPart @ValidEcoNewsDtoRequest AddEcoNewsDtoRequest addEcoNewsDtoRequest,
        @Parameter(description = "Image of eco news") @ImageValidation @RequestPart(
            required = false) MultipartFile image,
        @Parameter(hidden = true) Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ecoNewsService.saveEcoNews(addEcoNewsDtoRequest, image, principal.getName()));
    }

    /**
     * Method for uploading eco news images.
     *
     * @param images - array of eco news images
     * @return array of images path
     */
    @Operation(summary = "Upload array of images for eco news.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @PostMapping(path = "/uploadImages", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String[]> uploadImages(
        @Parameter(description = "Array of eco news images") MultipartFile[] images) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ecoNewsService.uploadImages(images));
    }

    /**
     * Method for updating {@link EcoNewsVO}.
     *
     * @param updateEcoNewsDto - dto for {@link EcoNewsVO} entity.
     * @return dto {@link EcoNewsDto} instance.
     */
    @Operation(summary = "Update eco news")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = EcoNewsGenericDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @PutMapping(path = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE,
        MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<EcoNewsGenericDto> update(
        @Parameter(description = SwaggerExampleModel.UPDATE_ECO_NEWS,
            required = true) @Valid @RequestPart UpdateEcoNewsDto updateEcoNewsDto,
        @Parameter(description = "Image of eco news") @ImageValidation @RequestPart(
            required = false) MultipartFile image,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK).body(
            ecoNewsService.update(updateEcoNewsDto, image, user));
    }

    /**
     * Method for getting eco news by id.
     *
     * @return {@link EcoNewsDto} instance.
     * @author Kovaliv Taras
     */
    @Operation(summary = "Get eco news by id.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @ApiLocale
    @GetMapping("/{id}")
    public ResponseEntity<EcoNewsDto> getEcoNewsById(@PathVariable Long id,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ecoNewsService.findDtoByIdAndLanguage(id, locale.getLanguage()));
    }

    /**
     * Method for getting eco news by authorised user.
     *
     * @return list of {@link EcoNewsDto} instances.
     * @author Vira Maksymets
     */
    @Operation(summary = "Get eco news by authorised user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("/byUser")
    public ResponseEntity<List<EcoNewsDto>> getEcoNewsByUser(@Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ecoNewsService.getAllPublishedNewsByUser(user));
    }

    /**
     * Method for getting all eco news by page.
     *
     * @return PageableDto of {@link EcoNewsDto} instances.
     * @author Yuriy Olkhovskyi & Kovaliv Taras.
     */
    @Operation(summary = "Find all eco news by page.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST)))
    })
    @GetMapping("")
    @ApiPageable
    public ResponseEntity<PageableAdvancedDto<EcoNewsGenericDto>> findAll(@Parameter(hidden = true) Pageable page,
        @Parameter(description = "Tags to filter (if do not input tags get all)") @RequestParam(
            required = false) List<String> tags,
        @RequestParam(required = false) String title) {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.findByFilters(page, tags, title));
    }

    /**
     * Method for getting all authorised user eco news by page.
     *
     * @return PageableDto of {@link EcoNewsDto} instances.
     * @author Danylo Hlynskyi.
     */
    @Operation(summary = "Find all eco news by page.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST)))
    })
    @GetMapping("/byUserPage")
    @ApiPageable
    public ResponseEntity<PageableAdvancedDto<EcoNewsGenericDto>> getEcoNewsByUserByPage(
        @Parameter(hidden = true) @CurrentUser UserVO user,
        @Parameter(hidden = true) Pageable page) {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.findAllByUser(user, page));
    }

    /**
     * Method for deleting {@link EcoNewsVO} by its id.
     *
     * @param econewsId {@link EcoNewsVO} id which will be deleted.
     * @return id of deleted {@link EcoNewsVO}.
     * @author Yuriy Olkhovskyi.
     */
    @Operation(summary = "Delete eco news.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @DeleteMapping("/{econewsId}")
    public ResponseEntity<Object> delete(@PathVariable Long econewsId,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        ecoNewsService.delete(econewsId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for getting all eco news by tags.
     *
     * @return list of {@link EcoNewsDto} instances.
     * @author Kovaliv Taras.
     */
    @Operation(summary = "Get eco news by tags")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
    })
    @GetMapping("/tags")
    @ApiPageable
    public ResponseEntity<PageableAdvancedDto<EcoNewsGenericDto>> getEcoNews(
        @Parameter(hidden = true) Pageable page,
        @Parameter(description = "Tags to filter (if do not input tags get all)") @RequestParam(
            required = false) List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                ecoNewsService.findGenericAll(page));
        }
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.find(page, tags));
    }

    /**
     * Method for getting three eco news for recommendations widget.
     *
     * @return list of three recommended {@link EcoNewsDto} instances.
     * @author Yurii Zhurakovskyi.
     */
    @Operation(summary = "Get three recommended eco news.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK)
    })
    @GetMapping("/recommended")
    public ResponseEntity<List<EcoNewsDto>> getThreeRecommendedEcoNews(@RequestParam() Long openedEcoNewsId) {
        List<EcoNewsDto> threeRecommendedEcoNews = ecoNewsService.getThreeRecommendedEcoNews(openedEcoNewsId);
        return ResponseEntity.status(HttpStatus.OK).body(threeRecommendedEcoNews);
    }

    /**
     * The method which returns all EcoNews {@link TagVO}s.
     *
     * @return list of {@link String} (tag's names).
     * @author Kovaliv Taras
     */
    @Operation(summary = "Find all eco news tags")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
    })
    @GetMapping("/tags/all")
    @ApiLocale
    public ResponseEntity<List<TagDto>> findAllEcoNewsTags(@Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.findAllEcoNewsTags(locale.getLanguage()));
    }

    /**
     * The method find count of published eco news.
     *
     * @return count of published eco news.
     * @author Mamchuk Orest
     */
    @Operation(summary = "Find count of published eco news")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("/count")
    public ResponseEntity<Long> findAmountOfPublishedNews(@RequestParam Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.getAmountOfPublishedNewsByUserId(userId));
    }

    /**
     * Method to like EcoNews.
     */
    @Operation(summary = "Like eco news")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @PostMapping("/like")
    public void like(@RequestParam("id") Long id, @Parameter(hidden = true) @CurrentUser UserVO user) {
        ecoNewsService.like(user, id);
    }

    /**
     * Method to dislike EcoNews.
     */
    @Operation(description = "Dislike eco news")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @PostMapping("/dislike")
    public void dislike(@RequestParam("id") Long id, @Parameter(hidden = true) @CurrentUser UserVO user) {
        ecoNewsService.dislike(user, id);
    }

    /**
     * Method to get amount of likes by eco news id.
     *
     * @return count of likes for eco news;
     */
    @Operation(description = "Count likes by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/countLikes/{econewsId}")
    public ResponseEntity<Integer> countLikesForEcoNews(@PathVariable Long econewsId) {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.countLikesForEcoNews(econewsId));
    }

    /**
     * Check if user liked news.
     *
     * @return user liked news or not.
     */
    @Operation(summary = "Check if user liked news")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/isLikedByUser")
    public ResponseEntity<Boolean> checkNewsIsLikedByUser(@RequestParam("econewsId") Long econewsId,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.checkNewsIsLikedByUser(econewsId, user));
    }

    /**
     * Method for getting some fields in eco news by id.
     *
     * @return dto {@link EcoNewContentSourceDto}.
     */
    @Operation(summary = "Get content and source in eco news by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(schema = @Schema(implementation = NotFoundException.class)))
    })
    @GetMapping("/contentAndSourceForEcoNews/{id}")
    public ResponseEntity<EcoNewContentSourceDto> getContentAndSourceForEcoNewsById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ecoNewsService.getContentAndSourceForEcoNewsById(id));
    }
}
