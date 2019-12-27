package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.newssubscriber.NewsDto;
import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.service.EmailService;
import greencity.service.NewsSubscriberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/newsSubscriber")
public class NewsSubscriberController {
    /**
     * trtr.
     */
    public final NewsSubscriberService newsSubscriberService;
    public final EmailService emailService;

    /**
     * trtr.
     *
     * @param newsSubscriberService {}
     * @param emailService          {}
     */
    @Autowired
    public NewsSubscriberController(NewsSubscriberService newsSubscriberService,
                                    EmailService emailService) {
        this.newsSubscriberService = newsSubscriberService;
        this.emailService = emailService;
    }

    /**
     * trtr.
     *
     * @return
     */
    @ApiOperation(value = "Get all .")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("")
    public ResponseEntity<List<NewsSubscriberRequestDto>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(newsSubscriberService.findAll());
    }

    /**
     * trtr.
     *
     */
    @ApiOperation(value = "create.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })

    @PostMapping("/save")
    public ResponseEntity<NewsSubscriberRequestDto> save(
        @RequestBody NewsSubscriberRequestDto dto) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(newsSubscriberService.save(dto));
    }

    @PostMapping("/send")
    public void send(@RequestBody List<NewsSubscriberRequestDto> subscribers,
                     @RequestBody NewsDto dto,
                     String unsubscribeLink) {
        emailService.sendNewNewsForSubscriber(subscribers, dto, unsubscribeLink);
    }
}
