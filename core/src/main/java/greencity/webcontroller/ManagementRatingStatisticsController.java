package greencity.webcontroller;

import com.softserve.ldm.dto.TableRowsDto;
import com.softserve.ldm.service.ExportToFileService;
import greencity.annotations.ApiPageable;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.ratingstatistics.*;
import greencity.exporter.RatingExcelExporter;
import greencity.service.RatingStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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
    private final RatingExcelExporter ratingExcelExporter;
    private final ExportToFileService exportToFileService;
    private static final DateTimeFormatter FILE_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Returns management page with User rating statistics.
     *
     * @param model ModelAndView that will be configured and returned to user
     * @return model
     * @author Dovganyuk Taras
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
     * Export {@link RatingStatisticsVO} to Excel file.
     *
     * @author Dovganyuk Taras
     */
    @GetMapping("/export")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        String fileName = "user_rating_statistics_" + LocalDate.now().format(FILE_DATE_FMT) + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        List<RatingStatisticsExportDto> stats = ratingStatisticsService.getAllRatingStatistics();

        List<Map<String, String>> rows = stats.stream()
            .map(s -> Map.of(
                "Id", String.valueOf(s.getId()),
                "Event", s.getEvent(),
                "Date", s.getDate().toString(),
                "UserId", String.valueOf(s.getUserId()),
                "User email", s.getUserEmail(),
                "Points changed", String.valueOf(s.getPointsChanged()),
                "Current rating", String.valueOf(s.getCurrentRating())))
            .toList();

        TableRowsDto table = new TableRowsDto("rating_statistics", rows);

        try (InputStream excel = exportToFileService.exportTableDataToExcel(table)) {
            excel.transferTo(response.getOutputStream());
            response.flushBuffer();
        }
    }

    /**
     * Export filtered {@link RatingStatisticsVO} to Excel file.
     *
     * @author Dovganyuk Taras
     */
    @PostMapping(value = "/exportFiltered", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void exportFilteredToExcel(HttpServletResponse response,
        RatingStatisticsViewDto ratingStatisticsViewDto)
        throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";

        String currentDate = LocalDate.now().format(FILE_DATE_FMT);
        String fileName = "user_rating_statistics" + currentDate + ".xlsx";
        String headerValue = "attachment; filename=" + fileName;

        response.setHeader(headerKey, headerValue);

        List<RatingStatisticsDto> ratingStatisticsList =
            ratingStatisticsService
                .getFilteredRatingStatisticsForExcel(ratingStatisticsViewDto);
        ratingExcelExporter.export(response.getOutputStream(), ratingStatisticsList);
    }

    /**
     * Returns management page with User rating statistics with filtered data.
     *
     * @param model                   ModelAndView that will be configured and
     *                                returned to user.
     * @param ratingStatisticsViewDto used for receive parameters for filters from
     *                                UI.
     */
    @ApiPageable(clazz = RatingStatisticsDtoForTables.class)
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String filterData(Model model,
        @Parameter(hidden = true) Pageable pageable,
        RatingStatisticsViewDto ratingStatisticsViewDto) {
        PageableAdvancedDto<RatingStatisticsDtoForTables> pageableDto =
            ratingStatisticsService.getFilteredDataForManagementByPage(pageable,
                ratingStatisticsViewDto);
        model.addAttribute("ratings", pageableDto);
        model.addAttribute("fields", ratingStatisticsViewDto);
        return "core/management_user_rating";
    }
}
