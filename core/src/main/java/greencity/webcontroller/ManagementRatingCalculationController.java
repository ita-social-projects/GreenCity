package greencity.webcontroller;

import greencity.annotations.ApiPageable;
import greencity.constant.HttpStatuses;
import greencity.dto.ratingstatistics.RatingPointsDto;
import greencity.service.RatingPointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/management/rating/calculation")
@RequiredArgsConstructor
public class ManagementRatingCalculationController {
    private final RatingPointsService ratingPointsService;

    /**
     * Returns the management page displaying user rating statistics.
     *
     * @param model    ModelAndView that will be configured and returned to the user
     * @param pageable Pageable configuration for pagination
     * @return the name of the view
     */
    @ApiPageable
    @Operation(summary = "Retrieve the management page with user rating statistics.")
    @GetMapping
    public String getRatingPoints(Model model,
        @PageableDefault(value = 20) @Parameter(hidden = true) Pageable pageable) {
        model.addAttribute("ratings", ratingPointsService.getAllRatingPointsByPage(pageable));
        return "core/management_rating_calculation";
    }

    /**
     * Deletes the Rating Points entity identified by the specified ID.
     *
     * @param id the ID of the Rating Points to delete
     * @return response entity indicating the result of the delete operation
     */
    @Operation(summary = "Delete Rating Points by ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
        @ApiResponse(responseCode = "404", description = "Rating Points not found.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        ratingPointsService.deleteRatingPoints(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates the Rating Points entity with the provided data.
     *
     * @param ratingPointsDto Data transfer object containing updated information
     * @return response entity with the updated Rating Points
     */
    @Operation(summary = "Update the specified Rating Points.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rating Points updated successfully."),
        @ApiResponse(responseCode = "404", description = "Rating Points not found."),
        @ApiResponse(responseCode = "400", description = "Invalid input data.")
    })
    @PutMapping
    public ResponseEntity<RatingPointsDto> updateRatingPoints(@RequestBody RatingPointsDto ratingPointsDto) {
        RatingPointsDto updatedRatingPoints = ratingPointsService.updateRatingPoints(ratingPointsDto);
        return ResponseEntity.ok(updatedRatingPoints);
    }

    /**
     * Gets a page with deleted Rating Points.
     *
     * @param pageable Pageable configuration for pagination
     * @return response entity containing the paginated list of deleted Rating
     *         Points
     */
    @Operation(summary = "Get deleted Rating Points.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deleted Rating Points retrieved successfully."),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/deleted")
    public String getDeletedRatingPoints(Model model,
        @PageableDefault(value = 20) @Parameter(hidden = true) Pageable pageable) {
        model.addAttribute("ratings", ratingPointsService.getDeletedRatingPoints(pageable));
        return "core/management_rating_deleted";
    }

    /**
     * Restores the deleted Rating Points identified by the specified ID.
     *
     * @param id the ID of the Rating Points to restore
     * @return response entity indicating the result of the restore operation
     */
    @Operation(summary = "Restore deleted Rating Points by ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rating Points restored successfully."),
        @ApiResponse(responseCode = "404", description = "Rating Points not found.")
    })
    @PutMapping("/restore/{id}")
    public ResponseEntity<Object> restoreDeletedRatingPoints(@PathVariable Long id) {
        ratingPointsService.restoreDeletedRatingPoints(id);
        return ResponseEntity.ok().build();
    }
}
