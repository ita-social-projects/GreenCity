package greencity.webcontroller;

import greencity.annotations.ApiPageable;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.ratingstatistics.RatingStatisticsDto;
import greencity.dto.ratingstatistics.RatingStatisticsDtoForTables;
import greencity.entity.RatingStatistics;
import greencity.exporter.RatingExcelExporter;
import greencity.filters.RatingStatisticsSpecification;
import greencity.filters.SearchCriteria;
import greencity.service.RatingStatisticsService;
import greencity.service.UserService;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/management/rating")
public class ManagementRatingStatisticsController {
    private RatingStatisticsService ratingStatisticsService;
    private RatingExcelExporter ratingExcelExporter;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private RatingStatisticsSpecification spec;

    /**
     * Constructor.
     */
    @Autowired
    public ManagementRatingStatisticsController(RatingStatisticsService ratingStatisticsService,
                                                RatingExcelExporter ratingExcelExporter,
                                                RatingStatisticsSpecification spec) {
        this.ratingStatisticsService = ratingStatisticsService;
        this.ratingExcelExporter = ratingExcelExporter;
        this.spec = spec;
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
        spec.setAllPredicates(null);
        spec.setSearchCriteria(SearchCriteria.builder().type("").build());
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
     * Export {@link RatingStatistics} to Excel file.
     *
     * @author Dovganyuk Taras
     */
    @GetMapping("/exportFiltered")
    public void exportFilteredToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";

        String currentDate = dateFormat.format(new Date());
        String fileName = "user_rating_statistics" + currentDate + ".xlsx";
        String headerValue = "attachment; filename=" + fileName;

        response.setHeader(headerKey, headerValue);

        List<RatingStatisticsDto> ratingStatisticsList =
            ratingStatisticsService.getFilteredRatingStatisticsForExcel(spec);
        ratingExcelExporter.export(response.getOutputStream(), ratingStatisticsList);
    }

    /**
     * dfg.
     */
    @PostMapping("")
    public String filterData(Model model,
                             @PageableDefault(value = 20) @ApiIgnore Pageable pageable,
                             @RequestParam(value = "id", required = false) String id,
                             @RequestParam(value = "event_name", required = false) String eventName,
                             @RequestParam(value = "user_id", required = false) String userId,
                             @RequestParam(value = "user_email", required = false) String userEmail,
                             @RequestParam(value = "start_date", required = false) String startDate,
                             @RequestParam(value = "end_date", required = false) String endDate) {
        SearchCriteria searchCriteria =
            ratingStatisticsService.buildSearchCriteria(id, eventName, userId, userEmail, startDate, endDate);
        Pageable paging =
            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createDate").descending());
        spec.setSearchCriteria(searchCriteria);
        PageableAdvancedDto<RatingStatisticsDtoForTables> pageableDto =
            ratingStatisticsService.getFilteredDataForManagementByPage(paging, spec);
        model.addAttribute("ratings", pageableDto);
        return "core/management_user_rating";
    }
}
