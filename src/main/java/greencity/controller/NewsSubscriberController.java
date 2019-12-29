package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.service.NewsSubscriberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/newsSubscriber")
public class NewsSubscriberController {
    public final NewsSubscriberService newsSubscriberService;

    /**
     * Constructor with parameters.
     *
     * @author Bogdan Kuzenko
     */
    @Autowired
    public NewsSubscriberController(NewsSubscriberService newsSubscriberService) {
        this.newsSubscriberService = newsSubscriberService;
    }

    /**
     * Method returns all news subscriber.
     *
     * @return list of {@link NewsSubscriberResponseDto}
     * @author Bogdan Kuzenko
     */
    @ApiOperation(value = "Get all emails for sending news.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("")
    public ResponseEntity<List<NewsSubscriberResponseDto>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(newsSubscriberService.findAll());
    }

    /**
     * Method saves email for receiving news.
     *
     * @param dto {@link NewsSubscriberResponseDto} object with email address for receiving news.
     * @return {@link NewsSubscriberResponseDto} object with saving email.
     * @author Bogdan Kuzenko
     */
    @ApiOperation(value = "Save email in database for receiving news.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("")
    public ResponseEntity<NewsSubscriberRequestDto> save(
        @RequestBody @Valid NewsSubscriberRequestDto dto) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(newsSubscriberService.save(dto));
    }

    /**
     * Method for delete subscriber email from database.
     *
     * @param email {@link NewsSubscriberResponseDto} object with email address for deleting.
     * @return id of deleted object.
     */
    @ApiOperation(value = "Deleting an email from subscribe table in database.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/unsubscribe")
    public ResponseEntity<Long> delete(@RequestParam @Valid String email,
                                       @RequestParam String unsubscribeToken) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(newsSubscriberService.unsubscribe(email, unsubscribeToken));
    }
}
