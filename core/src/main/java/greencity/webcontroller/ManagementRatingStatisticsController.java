package greencity.webcontroller;

import greencity.annotations.ApiPageable;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.ratingstatistics.RatingStatisticsDto;
import greencity.dto.ratingstatistics.RatingStatisticsDtoForTables;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.entity.RatingStatistics;
import greencity.exporter.RatingExcelExporter;
import greencity.filters.RatingStatisticsSpecification;
import greencity.service.RatingStatisticsService;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/management/rating")
public class ManagementRatingStatisticsController {
    private RatingStatisticsService ratingStatisticsService;
    private RatingExcelExporter ratingExcelExporter;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Constructor.
     */
    @Autowired
    public ManagementRatingStatisticsController(RatingStatisticsService ratingStatisticsService,
                                                RatingExcelExporter ratingExcelExporter,
                                                RatingStatisticsSpecification spec) {
        this.ratingStatisticsService = ratingStatisticsService;
        this.ratingExcelExporter = ratingExcelExporter;
    }

    /**
     * Returns management page with User rating statistics.
     *
     * @param model ModelAndView that will be configured and returned to user
     * @return model
     * @author Dovganyuk Taras
     */
    @ApiPageable
    @ApiOperation(value = "Get management page with facts of the day.")
    @GetMapping("")
    public String getUserRatingStatistics(Model model, @PageableDefault(value = 20) @ApiIgnore Pageable pageable) {
        Pageable paging =
            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createDate").descending());
        PageableAdvancedDto<RatingStatisticsDtoForTables> pageableDto =
            ratingStatisticsService.getRatingStatisticsForManagementByPage(paging);
        model.addAttribute("ratings", pageableDto);
        return "core/management_user_rating";
    }

    /**
     * Export {@link RatingStatistics} to Excel file.
     *
     * @author Dovganyuk Taras
     */
    @GetMapping("/export")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";

        String currentDate = dateFormat.format(new Date());
        String fileName = "user_rating_statistics" + currentDate + ".xlsx";
        String headerValue = "attachment; filename=" + fileName;

        response.setHeader(headerKey, headerValue);

        List<RatingStatisticsDto> ratingStatisticsList = ratingStatisticsService.getAllRatingStatistics();
        ratingExcelExporter.export(response.getOutputStream(), ratingStatisticsList);
    }

    /**
     * Export filtered {@link RatingStatistics} to Excel file.
     *
     * @author Dovganyuk Taras
     */
    @PostMapping(value = "/exportFiltered", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void exportFilteredToExcel(HttpServletResponse response,
                                      RatingStatisticsViewDto ratingStatisticsViewDto)
        throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";

        String currentDate = dateFormat.format(new Date());
        String fileName = "user_rating_statistics" + currentDate + ".xlsx";
        String headerValue = "attachment; filename=" + fileName;

        response.setHeader(headerKey, headerValue);

        List<RatingStatisticsDto> ratingStatisticsList =
            ratingStatisticsService
                .getFilteredRatingStatisticsForExcel(ratingStatisticsService.getSpecification(ratingStatisticsViewDto));
        ratingExcelExporter.export(response.getOutputStream(), ratingStatisticsList);
    }

    /**
     * Returns  management page with User rating statistics with filtered data.
     *
     * @param model                   ModelAndView that will be configured and returned to user.
     * @param ratingStatisticsViewDto used for receive parameters for filters from UI.
     */
    @PostMapping(value = "", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String filterData(Model model,
                             @PageableDefault(value = 20) @ApiIgnore Pageable pageable,
                             RatingStatisticsViewDto ratingStatisticsViewDto) {
        Pageable paging =
            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createDate").descending());

        PageableAdvancedDto<RatingStatisticsDtoForTables> pageableDto =
            ratingStatisticsService.getFilteredDataForManagementByPage(paging,
                ratingStatisticsService.getSpecification(ratingStatisticsViewDto));
        model.addAttribute("ratings", pageableDto);
        model.addAttribute("fields", ratingStatisticsViewDto);
        return "core/management_user_rating";
    }
}
