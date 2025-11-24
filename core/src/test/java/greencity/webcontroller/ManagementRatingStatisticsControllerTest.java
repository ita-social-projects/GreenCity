package greencity.webcontroller;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.ratingstatistics.RatingStatisticsDtoForTables;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManagementRatingStatisticsControllerTest {

    private static final String managementRatingStatisticsLink = "/management/rating";

    private MockMvc mockMvc;

    @Mock
    private RatingStatisticsService ratingStatisticsService;

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
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createDate"));
        List<RatingStatisticsDtoForTables> list = Collections.singletonList(new RatingStatisticsDtoForTables());
        PageableAdvancedDto<RatingStatisticsDtoForTables> pageableDto = new PageableAdvancedDto<>(list,
            3, 0, 3, 1, false, true, true, false);
        when(ratingStatisticsService.getRatingStatisticsForManagementByPage(pageable)).thenReturn(pageableDto);
        this.mockMvc.perform(get(managementRatingStatisticsLink)
            .param("page", "0")
            .param("size", "3"))
            .andExpect(view().name("core/management_user_rating"))
            .andExpect(model().attribute("ratings", pageableDto))
            .andExpect(status().isOk());

        verify(ratingStatisticsService).getRatingStatisticsForManagementByPage(pageable);
    }

    @Test
    void exportToExcelTest() throws Exception {
        byte[] excelBytes = "test_excel_data".getBytes();
        InputStream excelStream = new ByteArrayInputStream(excelBytes);

        when(ratingStatisticsService.exportStatisticsToExcel())
            .thenReturn(excelStream);

        MockHttpServletResponse response = mockMvc.perform(get(managementRatingStatisticsLink + "/export"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", response.getContentType());
        assertTrue(response.getHeader("Content-Disposition").contains("user_rating_statistics_"));
        assertArrayEquals(excelBytes, response.getContentAsByteArray());

        verify(ratingStatisticsService).exportStatisticsToExcel();
    }

    @Test
    void exportFilteredToExcelTest() throws Exception {
        RatingStatisticsViewDto dto = new RatingStatisticsViewDto();
        dto.setId("1");

        byte[] excelBytes = "filtered_excel_data".getBytes();
        InputStream excelStream = new ByteArrayInputStream(excelBytes);

        when(ratingStatisticsService.exportFilteredStatisticsToExcel(any()))
            .thenReturn(excelStream);

        MockHttpServletResponse response = mockMvc.perform(
            post(managementRatingStatisticsLink + "/exportFiltered")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("id", "1"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            response.getContentType());
        assertTrue(response.getHeader("Content-Disposition").contains("user_rating_statistics_"));
        assertArrayEquals(excelBytes, response.getContentAsByteArray());

        verify(ratingStatisticsService).exportFilteredStatisticsToExcel(any());
    }

    @Test
    void filterDataTest() throws Exception {
        RatingStatisticsViewDto ratingStatisticsViewDto = new RatingStatisticsViewDto();

        Pageable paging = PageRequest.of(0, 3, Sort.by("createDate").descending());

        List<RatingStatisticsDtoForTables> list =
            Collections.singletonList(new RatingStatisticsDtoForTables());

        PageableAdvancedDto<RatingStatisticsDtoForTables> pageableDto =
            new PageableAdvancedDto<>(list, 3, 0, 3, 1, false, true, true, false);

        when(ratingStatisticsService.getFilteredDataForManagementByPage(paging, ratingStatisticsViewDto))
            .thenReturn(pageableDto);

        this.mockMvc.perform(post(managementRatingStatisticsLink)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("page", "0")
            .param("size", "3"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("ratings", pageableDto))
            .andExpect(model().attribute("fields", ratingStatisticsViewDto))
            .andExpect(view().name("core/management_user_rating"));

        verify(ratingStatisticsService)
            .getFilteredDataForManagementByPage(paging, ratingStatisticsViewDto);
    }

}
