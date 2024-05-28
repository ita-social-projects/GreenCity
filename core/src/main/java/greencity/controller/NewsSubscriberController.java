package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.service.NewsSubscriberService;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

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
    @Operation(summary = "Get all emails for sending news.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @GetMapping("")
    public ResponseEntity<List<NewsSubscriberResponseDto>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(newsSubscriberService.findAll());
    }

    /**
     * Method saves email for receiving news.
     *
     * @param dto {@link NewsSubscriberResponseDto} object with email address for
     *            receiving news.
     * @return {@link NewsSubscriberResponseDto} object with saving email.
     * @author Bogdan Kuzenko
     */
    @Operation(summary = "Save email in database for receiving news.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = NewsSubscriberRequestDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
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
     * @param email {@link NewsSubscriberResponseDto} object with email address for
     *              deleting.
     * @return id of deleted object.
     */
    @Operation(summary = "Deleting an email from subscribe table in database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/unsubscribe")
    public ResponseEntity<Long> delete(@RequestParam @Valid String email,
        @RequestParam String unsubscribeToken) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(newsSubscriberService.unsubscribe(email, unsubscribeToken));
    }
}
