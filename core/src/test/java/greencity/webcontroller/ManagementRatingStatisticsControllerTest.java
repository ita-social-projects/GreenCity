package greencity.webcontroller;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.ratingstatistics.RatingStatisticsDto;
import greencity.dto.ratingstatistics.RatingStatisticsDtoForTables;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.entity.RatingStatistics;
import greencity.exporter.RatingExcelExporter;
import greencity.service.RatingStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ManagementRatingStatisticsControllerTest {

    private static final String managementRatingStatisticsLink = "/management/rating";
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private MockMvc mockMvc;

    @Mock
    private RatingStatisticsService ratingStatisticsService;

    @Mock
    private RatingExcelExporter ratingExcelExporter;

    @InjectMocks
    private ManagementRatingStatisticsController managementRatingStatisticsController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(managementRatingStatisticsController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

    }

    @Test
    void getUserRatingStatisticsTest() throws Exception {
        Pageable pageable = PageRequest.of(0,3,new Sort(Sort.Direction.DESC,"createDate"));
        Pageable paging = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by("createDate").descending());
        List<RatingStatisticsDtoForTables> list = Collections.singletonList(new RatingStatisticsDtoForTables());
        PageableAdvancedDto<RatingStatisticsDtoForTables> pageableDto = new PageableAdvancedDto<>(list,
                3,0,3,1,false,true,true,false);
        when(ratingStatisticsService.getRatingStatisticsForManagementByPage(paging)).thenReturn(pageableDto);
        this.mockMvc.perform(get(managementRatingStatisticsLink)
                .param("page","0")
                .param("size","3"))
                .andExpect(view().name("core/management_user_rating"))
                .andExpect(model().attribute("ratings",pageableDto))
                .andExpect(status().isOk());

        verify(ratingStatisticsService).getRatingStatisticsForManagementByPage(pageable);
    }

    @Test
    void exportToExcelTest() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                "user_rating_statistics" + dateFormat.format(new Date()) + ".xlsx");
        List<RatingStatisticsDto> list = Collections.singletonList(new RatingStatisticsDto());
        when(ratingStatisticsService.getAllRatingStatistics()).thenReturn(list);
        this.mockMvc.perform(get(managementRatingStatisticsLink + "/export"))
                .andExpect(status().isOk());
        verify(ratingExcelExporter,never()).export(response.getOutputStream(),list);
    }

    @Test
    void exportFilteredToExcelTest() throws Exception {
        RatingStatisticsViewDto ratingStatisticsViewDto = new RatingStatisticsViewDto();
        HttpServletResponse response = mock(HttpServletResponse.class);
        List<RatingStatisticsDto> list = Collections.singletonList(new RatingStatisticsDto());
        when(ratingStatisticsService.getFilteredRatingStatisticsForExcel(ratingStatisticsViewDto)).thenReturn(list);
        this.mockMvc.perform(post(managementRatingStatisticsLink + "/exportFiltered/")
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition","attachment; filename=user_rating_statistics" + dateFormat.format(new Date()) + ".xlsx")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());
        verify(ratingExcelExporter,never()).export(response.getOutputStream(),list);
    }

    @Test
    void filterDataTest() throws Exception {
        this.mockMvc.perform(post(managementRatingStatisticsLink))
                .andExpect(status().isOk());
    }

}
