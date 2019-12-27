package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import greencity.service.EcoNewsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/econews")
public class EcoNewsController {
    private final EcoNewsService ecoNewsService;

    /**
     * fds.
     *
     * @param ecoNewsService dsada.
     */
    @Autowired
    public EcoNewsController(EcoNewsService ecoNewsService) {
        this.ecoNewsService = ecoNewsService;
    }

    /**
     * dasds.
     */
    @ApiOperation(value = "Add new econews.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = EcoNews.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("")
    public ResponseEntity<AddEcoNewsDtoResponse> save(@RequestBody AddEcoNewsDtoRequest addEcoNewsDtoRequest) {
        AddEcoNewsDtoResponse save = ecoNewsService.save(addEcoNewsDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(save);
    }

    /**
     * dsadas.
     *
     * @return sdaas.
     */
    @ApiOperation(value = "Get three last eco news.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/newest")
    public ResponseEntity<List<EcoNewsDto>> getThreeLastEcoNews() {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.getThreeLastEcoNews());
    }

    /**
     * dsadas.
     *
     * @return sdaas.
     */
    @ApiOperation(value = "Find all eco news.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("")
    public ResponseEntity<List<EcoNewsDto>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.findAll());
    }
}
