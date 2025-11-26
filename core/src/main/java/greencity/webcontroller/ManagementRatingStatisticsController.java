package greencity.webcontroller;

import greencity.annotations.ApiPageable;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.ratingstatistics.*;
import greencity.service.RatingStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/management/rating")
@RequiredArgsConstructor
public class ManagementRatingStatisticsController {
    private final RatingStatisticsService ratingStatisticsService;
    private static final DateTimeFormatter FILE_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Returns the management page displaying a paginated list of user rating
     * statistics.
     *
     * <p>
     * Retrieves all rating statistics from the service and sorts them by creation
     * date in descending order.
     * </p>
     *
     * @param model    Model object to which the paginated rating statistics will be
     *                 added
     * @param pageable Pageable object for pagination (page number, page size,
     *                 sorting)
     * @return the name of the view template "core/management_user_rating"
     */

    @ApiPageable(clazz = RatingStatisticsDtoForTables.class)
    @Operation(summary = "Get management page with User rating statistics.")
    @GetMapping
    public String getUserRatingStatistics(Model model,
        @Parameter(hidden = true) Pageable pageable) {
        Pageable paging =
            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createDate").descending());
        PageableAdvancedDto<RatingStatisticsDtoForTables> pageableDto =
            ratingStatisticsService.getRatingStatisticsForManagementByPage(paging);
        model.addAttribute("ratings", pageableDto);
        return "core/management_user_rating";
    }

    /**
     * Exports all user rating statistics to an Excel file.
     *
     * <p>
     * The file will be named in the format
     * "user_rating_statistics_YYYY-MM-DD.xlsx". The method sets the appropriate
     * content type and headers for file download.
     * </p>
     *
     * @param response HttpServletResponse to which the Excel file will be written.
     * @throws IOException if an I/O error occurs during writing to the response
     *                     output stream.
     */
    @GetMapping("/export")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = "user_rating_statistics_" + LocalDate.now().format(FILE_DATE_FMT) + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (InputStream excel = ratingStatisticsService.exportStatisticsToExcel()) {
            excel.transferTo(response.getOutputStream());
            response.flushBuffer();
        }
    }

    /**
     * Exports filtered user rating statistics to an Excel file based on filter
     * criteria provided by the user.
     *
     * <p>
     * The file will be named in the format
     * "user_rating_statistics_YYYY-MM-DD.xlsx". The method sets the appropriate
     * content type and headers for file download.
     * </p>
     *
     * @param response                HttpServletResponse to which the Excel file
     *                                will be written.
     * @param ratingStatisticsViewDto DTO containing filter criteria from the UI
     *                                (e.g., user ID, event name, date range).
     * @throws IOException if an I/O error occurs during writing to the response
     *                     output stream.
     */
    @PostMapping(value = "/exportFiltered", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void exportFilteredToExcel(HttpServletResponse response,
        RatingStatisticsViewDto ratingStatisticsViewDto)
        throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = "user_rating_statistics_" + LocalDate.now().format(FILE_DATE_FMT) + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (InputStream excel = ratingStatisticsService.exportFilteredStatisticsToExcel(ratingStatisticsViewDto)) {
            excel.transferTo(response.getOutputStream());
            response.flushBuffer();
        }
    }

    /**
     * Returns the management page with filtered User rating statistics.
     *
     * <p>
     * Applies filter criteria provided by the user and retrieves a paginated,
     * automatically sorted (by creation date in descending order) list of rating
     * statistics. The sorting is applied explicitly to ensure consistent ordering
     * of results regardless of filter usage.
     * </p>
     *
     * <p>
     * The method adds the filtered results to the model, as well as the filter
     * fields themselves so that the user's input persists on the UI after
     * submission.
     * </p>
     *
     * @param model                   Model object to which filtered rating
     *                                statistics and filter fields will be added.
     * @param pageable                Pageable object representing pagination
     *                                settings (page number and size). Sorting is
     *                                ignored and overridden with a predefined
     *                                descending sort on createDate.
     * @param ratingStatisticsViewDto DTO containing filter criteria submitted by
     *                                the user.
     * @return the name of the view template "core/management_user_rating".
     */
    @ApiPageable(clazz = RatingStatisticsDtoForTables.class)
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String filterData(Model model,
        @Parameter(hidden = true) Pageable pageable,
        RatingStatisticsViewDto ratingStatisticsViewDto) {
        Pageable paging = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by("createDate").descending());

        PageableAdvancedDto<RatingStatisticsDtoForTables> pageableDto =
            ratingStatisticsService.getFilteredDataForManagementByPage(
                paging,
                ratingStatisticsViewDto);

        model.addAttribute("ratings", pageableDto);
        model.addAttribute("fields", ratingStatisticsViewDto);

        return "core/management_user_rating";
    }
}
