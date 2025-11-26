package greencity.service;

import com.softserve.ldm.dto.TableRowsDto;
import com.softserve.ldm.service.ExportToFileService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import greencity.ModelUtils;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.ratingstatistics.RatingPointsDto;
import greencity.dto.ratingstatistics.RatingStatisticsDtoForTables;
import greencity.dto.ratingstatistics.RatingStatisticsVO;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.entity.RatingPoints;
import greencity.entity.RatingStatistics;
import greencity.entity.User;
import greencity.filters.RatingStatisticsSpecification;
import greencity.filters.SearchCriteria;
import greencity.repository.RatingStatisticsRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RatingStatisticsServiceImplTest {
    @Mock
    private RatingStatisticsRepo ratingStatisticsRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ExportToFileService exportToFileService;
    @InjectMocks
    private RatingStatisticsServiceImpl ratingStatisticsService;

    private final ZonedDateTime defaultTime = ZonedDateTime.now();

    private final Pageable pageable = PageRequest.of(3, 5);

    private final RatingPoints ratingPoints = RatingPoints.builder()
        .id(1L)
        .name("CREATE_NEWS")
        .points(1)
        .build();

    private final RatingPointsDto ratingPointsDto = RatingPointsDto.builder()
        .id(1L)
        .name("CREATE_NEWS")
        .points(1)
        .build();

    private final RatingStatisticsVO ratingStatisticsVO = RatingStatisticsVO.builder()
        .id(1L)
        .createDate(defaultTime)
        .pointsChanged(1.0)
        .rating(5.0)
        .ratingPoints(ratingPointsDto)
        .build();

    private final RatingStatistics ratingStatistics = RatingStatistics.builder()
        .id(1L)
        .createDate(defaultTime)
        .pointsChanged(1.0)
        .rating(5.0)
        .ratingPoints(ratingPoints)
        .user(ModelUtils.testUser)
        .build();

    private final Page<RatingStatistics> ratingStatisticsPage = Page.empty(pageable);

    private SearchCriteria generateSearchCriteria(String key, String type) {
        return SearchCriteria.builder()
            .key(key)
            .type(type)
            .build();
    }

    @Test
    void save() {
        when(modelMapper.map(ratingStatisticsVO, RatingStatistics.class)).thenReturn(ratingStatistics);
        when(ratingStatisticsRepo.save(ratingStatistics)).thenReturn(ratingStatistics);
        when(modelMapper.map(ratingStatistics, RatingStatisticsVO.class)).thenReturn(ratingStatisticsVO);
        RatingStatisticsVO expected = ratingStatisticsService.save(ratingStatisticsVO);

        verify(ratingStatisticsRepo, times(1)).save(ratingStatistics);
        assertEquals(expected, ratingStatisticsVO);
    }

    @Test
    void getRatingStatisticsForManagementByPage() {
        when(ratingStatisticsRepo.findAll(pageable)).thenReturn(ratingStatisticsPage);
        PageableAdvancedDto<RatingStatisticsDtoForTables> expected = ratingStatisticsService
            .getRatingStatisticsForManagementByPage(pageable);
        PageableAdvancedDto<RatingStatisticsDtoForTables> actual = new PageableAdvancedDto<>(
            Collections.emptyList(), ratingStatisticsPage.getTotalElements(), 3,
            ratingStatisticsPage.getTotalPages(),
            ratingStatisticsPage.getNumber(), ratingStatisticsPage.hasPrevious(),
            ratingStatisticsPage.hasNext(),
            ratingStatisticsPage.isFirst(), ratingStatisticsPage.isLast());

        assertEquals(expected, actual);
    }

    @Test
    void exportStatisticsToExcel() {
        RatingPoints rp = RatingPoints.builder().name("EV").build();
        User user = new User();
        user.setId(44L);
        user.setEmail("u@test.com");

        RatingStatistics rs = RatingStatistics.builder()
            .id(1L)
            .ratingPoints(rp)
            .user(user)
            .createDate(LocalDate.of(2024, 1, 1).atStartOfDay(ZoneId.systemDefault()))
            .pointsChanged(3)
            .rating(20)
            .build();

        when(ratingStatisticsRepo.findAllForExport()).thenReturn(List.of(rs));

        InputStream fakeExcel = new ByteArrayInputStream("excel".getBytes());
        when(exportToFileService.exportTableDataToExcel(any())).thenReturn(fakeExcel);

        InputStream result = ratingStatisticsService.exportStatisticsToExcel();
        assertNotNull(result);

        ArgumentCaptor<TableRowsDto> captor = ArgumentCaptor.forClass(TableRowsDto.class);
        verify(exportToFileService).exportTableDataToExcel(captor.capture());

        TableRowsDto table = captor.getValue();

        assertEquals("rating_statistics", table.tableName());
        assertFalse(table.tableData().isEmpty());

        Map<String, String> row = table.tableData().get(0);

        assertEquals("1", row.get("ID"));
        assertEquals("EV", row.get("Event Name"));
        assertEquals("44", row.get("User ID"));
        assertEquals("u@test.com", row.get("User Email"));
        assertEquals("3.0", row.get("Points Changed"));
        assertEquals("20.0", row.get("Current Rating"));
    }

    @Test
    void exportFilteredStatisticsToExcel() {
        RatingStatisticsViewDto dto = RatingStatisticsViewDto.builder()
            .eventName("AA")
            .build();

        RatingPoints rp = RatingPoints.builder().name("POINT").build();
        User user = new User();
        user.setId(77L);
        user.setEmail("xx@test.com");

        RatingStatistics rs = RatingStatistics.builder()
            .id(5L)
            .ratingPoints(rp)
            .user(user)
            .createDate(LocalDate.of(2023, 12, 1).atStartOfDay(ZoneId.systemDefault()))
            .pointsChanged(11)
            .rating(99)
            .build();

        when(ratingStatisticsRepo.findAll(any(Specification.class))).thenReturn(List.of(rs));

        InputStream fakeExcel = new ByteArrayInputStream("xls".getBytes());
        when(exportToFileService.exportTableDataToExcel(any())).thenReturn(fakeExcel);

        InputStream result = ratingStatisticsService.exportFilteredStatisticsToExcel(dto);
        assertNotNull(result);

        ArgumentCaptor<TableRowsDto> captor = ArgumentCaptor.forClass(TableRowsDto.class);
        verify(exportToFileService).exportTableDataToExcel(captor.capture());

        TableRowsDto table = captor.getValue();

        assertEquals("filtered_rating_statistics", table.tableName());
        assertFalse(table.tableData().isEmpty());

        Map<String, String> row = table.tableData().get(0);

        assertEquals("5", row.get("ID"));
        assertEquals("POINT", row.get("Event Name"));
        assertEquals("77", row.get("User ID"));
        assertEquals("xx@test.com", row.get("User Email"));
        assertEquals("11.0", row.get("Points Changed"));
        assertEquals("99.0", row.get("Current Rating"));
    }

    @Test
    void getFilteredDataForManagementByPage() {
        RatingStatisticsViewDto ratingStatisticsViewDto = RatingStatisticsViewDto.builder()
            .id("").eventName("").userId("").userEmail("").startDate("").endDate("")
            .pointsChanged("").currentRating("").build();

        when(ratingStatisticsRepo.findAll(any(RatingStatisticsSpecification.class), eq(pageable)))
            .thenReturn(ratingStatisticsPage);

        PageableAdvancedDto<RatingStatisticsDtoForTables> expected = ratingStatisticsService
            .getFilteredDataForManagementByPage(pageable, ratingStatisticsViewDto);
        PageableAdvancedDto<RatingStatisticsDtoForTables> actual = new PageableAdvancedDto<>(
            Collections.emptyList(), ratingStatisticsPage.getTotalElements(), 3,
            ratingStatisticsPage.getTotalPages(),
            ratingStatisticsPage.getNumber(), ratingStatisticsPage.hasPrevious(), ratingStatisticsPage.hasNext(),
            ratingStatisticsPage.isFirst(), ratingStatisticsPage.isLast());

        assertEquals(expected, actual);
    }

    @Test
    void buildSearchCriteriaOnlyWithId() {
        RatingStatisticsViewDto ratingStatisticsViewDto = RatingStatisticsViewDto.builder()
            .id("1L").eventName("").userId("").userEmail("").startDate("").endDate("")
            .pointsChanged("").currentRating("").build();
        SearchCriteria searchCriteriaId = generateSearchCriteria("id", "id");
        searchCriteriaId.setValue(ratingStatisticsViewDto.getId());
        List<SearchCriteria> actual = List.of(searchCriteriaId);
        List<SearchCriteria> expected = ratingStatisticsService.buildSearchCriteria(ratingStatisticsViewDto);

        assertEquals(expected, actual);
    }

    @Test
    void buildSearchCriteriaWithIdAndEventNameAndUserIdAndUserEmail() {
        RatingStatisticsViewDto ratingStatisticsViewDto = RatingStatisticsViewDto.builder()
            .id("1L").eventName("Event Name").userId("2L").userEmail("email@gmail.com").startDate("").endDate("")
            .pointsChanged("").currentRating("").build();

        SearchCriteria searchCriteriaId = generateSearchCriteria("id", "id");
        searchCriteriaId.setValue(ratingStatisticsViewDto.getId());
        SearchCriteria searchCriteriaEventName = generateSearchCriteria("ratingPoints", "ratingPoints");
        searchCriteriaEventName.setValue(ratingStatisticsViewDto.getEventName());
        SearchCriteria searchCriteriaUserId = generateSearchCriteria("user", "userId");
        searchCriteriaUserId.setValue(ratingStatisticsViewDto.getUserId());
        SearchCriteria searchCriteriaUserEmail = generateSearchCriteria("user", "userMail");
        searchCriteriaUserEmail.setValue(ratingStatisticsViewDto.getUserEmail());

        List<SearchCriteria> actual = List.of(searchCriteriaId, searchCriteriaEventName,
            searchCriteriaUserId, searchCriteriaUserEmail);
        List<SearchCriteria> expected = ratingStatisticsService.buildSearchCriteria(ratingStatisticsViewDto);

        assertEquals(expected, actual);
    }

    @Test
    void buildSearchCriteriaWithStartDateAndEndDateAndPointsChangedAndCurrentRating() {
        RatingStatisticsViewDto ratingStatisticsViewDto = RatingStatisticsViewDto.builder()
            .id("").eventName("").userId("").userEmail("").startDate("2020-10-17").endDate("2020-10-20")
            .pointsChanged("10").currentRating("8").build();

        SearchCriteria searchCriteriaPointsChanged = generateSearchCriteria("pointsChanged", "pointsChanged");
        searchCriteriaPointsChanged.setValue(ratingStatisticsViewDto.getPointsChanged());
        SearchCriteria searchCriteriaCurrentRating = generateSearchCriteria("rating", "currentRating");
        searchCriteriaCurrentRating.setValue(ratingStatisticsViewDto.getCurrentRating());
        SearchCriteria searchCriteriaDateRange = generateSearchCriteria("createDate", "dateRange");
        searchCriteriaDateRange.setValue(new String[] {ratingStatisticsViewDto.getStartDate(),
            ratingStatisticsViewDto.getEndDate()});

        List<SearchCriteria> actual = List.of(searchCriteriaDateRange, searchCriteriaPointsChanged,
            searchCriteriaCurrentRating);
        List<SearchCriteria> expected = ratingStatisticsService.buildSearchCriteria(ratingStatisticsViewDto);

        assertEquals(expected.size(), actual.size());
        assertEquals(expected.getLast(), actual.getLast());
        assertEquals(expected.get(1), actual.get(1));
    }

    @Test
    void buildSearchCriteriaReturnEmptyList() {
        RatingStatisticsViewDto ratingStatisticsViewDto = RatingStatisticsViewDto.builder()
            .id("").eventName("").userId("").userEmail("").startDate("").endDate("")
            .pointsChanged("").currentRating("").build();

        List<SearchCriteria> expected = ratingStatisticsService.buildSearchCriteria(ratingStatisticsViewDto);

        assertEquals(0, expected.size());
    }
}
