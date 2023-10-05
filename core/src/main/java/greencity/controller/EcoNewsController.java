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
import greencity.service.FileService;
import greencity.service.TagsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@RestController
@RequestMapping("/econews")
@RequiredArgsConstructor
public class EcoNewsController {
    private final EcoNewsService ecoNewsService;
    private final TagsService tagService;
    private final FileService fileService;

    /**
     * Method for getting three last eco news.
     *
     * @return list of {@link EcoNewsDto} instances.
     * @author Yuriy Olkhovskyi.
     */
    @ApiOperation(value = "Get three last eco news.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
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
    @ApiOperation(value = "Add new eco news.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED,
            response = EcoNewsGenericDto.class),
    })
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<EcoNewsGenericDto> save(
        @ApiParam(value = SwaggerExampleModel.ADD_ECO_NEWS_REQUEST,
            required = true) @RequestPart @ValidEcoNewsDtoRequest AddEcoNewsDtoRequest addEcoNewsDtoRequest,
        @ApiParam(value = "Image of eco news") @ImageValidation @RequestPart(required = false) MultipartFile image,
        @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ecoNewsService.saveEcoNews(addEcoNewsDtoRequest, image, principal.getName()));
    }

    /**
     * Method for uploading eco news images.
     *
     * @param images - array of eco news images
     * @return array of images path
     */
    @ApiOperation(value = "Upload array of images for eco news.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = String.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PostMapping(path = "/uploadImages", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String[]> uploadImages(
        @ApiParam(value = "Array of eco news images") MultipartFile[] images) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ecoNewsService.uploadImages(images));
    }

    /**
     * Method for updating {@link EcoNewsVO}.
     *
     * @param updateEcoNewsDto - dto for {@link EcoNewsVO} entity.
     * @return dto {@link EcoNewsDto} instance.
     */
    @ApiOperation(value = "Update eco news")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = EcoNewsGenericDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })

    @PutMapping(path = "/update", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE,
        MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<EcoNewsGenericDto> update(
        @ApiParam(value = SwaggerExampleModel.UPDATE_ECO_NEWS,
            required = true) @Valid @RequestPart UpdateEcoNewsDto updateEcoNewsDto,
        @ApiParam(value = "Image of eco news") @ImageValidation @RequestPart(
            required = false) MultipartFile image,
        @ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK).body(
            ecoNewsService.update(updateEcoNewsDto, image, user));
    }

    /**
     * Method for getting eco news by id.
     *
     * @return {@link EcoNewsDto} instance.
     * @author Kovaliv Taras
     */
    @ApiOperation(value = "Get eco news by id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("/{id}")
    public ResponseEntity<EcoNewsDto> getEcoNewsById(@PathVariable Long id,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ecoNewsService.findDtoByIdAndLanguage(id, locale.getLanguage()));
    }

    /**
     * Method for getting eco news by authorised user.
     *
     * @return list of {@link EcoNewsDto} instances.
     * @author Vira Maksymets
     */
    @ApiOperation(value = "Get eco news by authorised user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/byUser")
    public ResponseEntity<List<EcoNewsDto>> getEcoNewsByUser(@ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ecoNewsService.getAllPublishedNewsByUser(user));
    }

    /**
     * Method for getting all eco news by page.
     *
     * @return PageableDto of {@link EcoNewsDto} instances.
     * @author Yuriy Olkhovskyi & Kovaliv Taras.
     */
    @ApiOperation(value = "Find all eco news by page.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("")
    @ApiPageable
    public ResponseEntity<PageableAdvancedDto<EcoNewsGenericDto>> findAll(@ApiIgnore Pageable page) {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.findGenericAll(page));
    }

    /**
     * Method for getting all authorised user eco news by page.
     *
     * @return PageableDto of {@link EcoNewsDto} instances.
     * @author Danylo Hlynskyi.
     */
    @ApiOperation(value = "Find all eco news by page.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/byUserPage")
    @ApiPageable
    public ResponseEntity<PageableAdvancedDto<EcoNewsGenericDto>> getEcoNewsByUserByPage(
        @ApiIgnore @CurrentUser UserVO user,
        @ApiIgnore Pageable page) {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.findAllByUser(user, page));
    }

    /**
     * Method for deleting {@link EcoNewsVO} by its id.
     *
     * @param econewsId {@link EcoNewsVO} id which will be deleted.
     * @return id of deleted {@link EcoNewsVO}.
     * @author Yuriy Olkhovskyi.
     */
    @ApiOperation(value = "Delete eco news.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{econewsId}")
    public ResponseEntity<Object> delete(@PathVariable Long econewsId, @ApiIgnore @CurrentUser UserVO user) {
        ecoNewsService.delete(econewsId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for getting all eco news by tags.
     *
     * @return list of {@link EcoNewsDto} instances.
     * @author Kovaliv Taras.
     */
    @ApiOperation(value = "Get eco news by tags")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
    })
    @GetMapping("/tags")
    @ApiPageable
    public ResponseEntity<PageableAdvancedDto<EcoNewsGenericDto>> getEcoNews(
        @ApiIgnore Pageable page,
        @ApiParam(value = "Tags to filter (if do not input tags get all)") @RequestParam(
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
    @ApiOperation(value = "Get three recommended eco news.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK)
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
    @ApiOperation(value = "Find all eco news tags")
    @GetMapping("/tags/all")
    @ApiLocale
    public ResponseEntity<List<TagDto>> findAllEcoNewsTags(@ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.findAllEcoNewsTags(locale.getLanguage()));
    }

    /**
     * The method find count of published eco news.
     *
     * @return count of published eco news.
     * @author Mamchuk Orest
     */
    @ApiOperation(value = "Find count of published eco news")
    @GetMapping("/count")
    public ResponseEntity<Long> findAmountOfPublishedNews(@RequestParam Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.getAmountOfPublishedNewsByUserId(userId));
    }

    /**
     * Method to like EcoNews.
     */
    @ApiOperation(value = "Like eco news")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/like")
    public void like(@RequestParam("id") Long id, @ApiIgnore @CurrentUser UserVO user) {
        ecoNewsService.like(user, id);
    }

    /**
     * Method to dislike EcoNews.
     */
    @ApiOperation(value = "Dislike eco news")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("/dislike")
    public void dislike(@RequestParam("id") Long id, @ApiIgnore @CurrentUser UserVO user) {
        ecoNewsService.dislike(user, id);
    }

    /**
     * Method to get amount of likes by eco news id.
     *
     * @return count of likes for eco news;
     */
    @ApiOperation(value = "Count likes by id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
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
    @ApiOperation(value = "Check if user liked news")
    @GetMapping("/isLikedByUser")
    public ResponseEntity<Boolean> checkNewsIsLikedByUser(@RequestParam("econewsId") Long econewsId,
        @ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.checkNewsIsLikedByUser(econewsId, user));
    }

    /**
     * Method for getting some fields in eco news by id.
     *
     * @return dto {@link EcoNewContentSourceDto}.
     */
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND, response = NotFoundException.class)
    })
    @GetMapping("/contentAndSourceForEcoNews/{id}")
    public ResponseEntity<EcoNewContentSourceDto> getContentAndSourceForEcoNewsById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ecoNewsService.getContentAndSourceForEcoNewsById(id));
    }
}
