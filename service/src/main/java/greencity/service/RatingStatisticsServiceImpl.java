package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.ratingstatistics.*;
import greencity.entity.RatingStatistics;
import greencity.entity.RatingStatistics_;
import greencity.filters.RatingStatisticsSpecification;
import greencity.filters.SearchCriteria;
import greencity.repository.RatingStatisticsRepo;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.persistence.criteria.JoinType;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.softserve.ldm.dto.TableRowsDto;
import com.softserve.ldm.service.ExportToFileService;

@AllArgsConstructor
@Service
public class RatingStatisticsServiceImpl implements RatingStatisticsService {
    private RatingStatisticsRepo ratingStatisticsRepo;
    private final ModelMapper modelMapper;
    private final ExportToFileService exportToFileService;

    /**
     * Maps Page of RatingStatistics entities to PageableAdvancedDto of
     * RatingStatisticsDtoForTables.
     *
     * @param ratingStatistics Page of RatingStatistics entities
     * @return PageableAdvancedDto containing mapped RatingStatisticsDtoForTables
     */
    private PageableAdvancedDto<RatingStatisticsDtoForTables> ratingStatisticsDtoMapper(
        Page<RatingStatistics> ratingStatistics) {
        List<RatingStatisticsDto> ratingStatisticsDtos = ratingStatistics.get()
            .map(ratingStat -> modelMapper.map(ratingStat, RatingStatisticsDto.class))
            .toList();
        List<RatingStatisticsDtoForTables> ratingStatisticsDtoForTablesDtos = ratingStatisticsDtos.stream()
            .map(x -> RatingStatisticsDtoForTables.builder()
                .id(x.getId())
                .createDate(x.getCreateDate())
                .eventName(x.getRatingPoints().getName())
                .pointsChanged(x.getPointsChanged())
                .rating(x.getRating())
                .userId(x.getUser().getId())
                .userEmail(x.getUser().getEmail())
                .build())
            .collect(Collectors.toList());

        return new PageableAdvancedDto<>(
            ratingStatisticsDtoForTablesDtos,
            ratingStatistics.getTotalElements(),
            ratingStatistics.getPageable().getPageNumber(),
            ratingStatistics.getTotalPages(),
            ratingStatistics.getNumber(),
            ratingStatistics.hasPrevious(),
            ratingStatistics.hasNext(),
            ratingStatistics.isFirst(),
            ratingStatistics.isLast());
    }

    /**
     * Saves a new RatingStatisticsVO entity to the database.
     *
     * @param ratingStatistics RatingStatisticsVO to save
     * @return saved RatingStatisticsVO
     */
    @Transactional
    @Override
    public RatingStatisticsVO save(RatingStatisticsVO ratingStatistics) {
        RatingStatistics saved = ratingStatisticsRepo.save(modelMapper.map(ratingStatistics, RatingStatistics.class));
        return modelMapper.map(saved, RatingStatisticsVO.class);
    }

    /**
     * Retrieves paginated list of rating statistics for management purposes.
     *
     * @param pageable pageable configuration
     * @return PageableAdvancedDto containing RatingStatisticsDtoForTables
     */
    @Override
    public PageableAdvancedDto<RatingStatisticsDtoForTables> getRatingStatisticsForManagementByPage(Pageable pageable) {
        Page<RatingStatistics> ratingStatistics = ratingStatisticsRepo.findAll(pageable);
        return ratingStatisticsDtoMapper(ratingStatistics);
    }

    /**
     * Retrieves all rating statistics for export.
     *
     * @return list of RatingStatisticsExportDto
     */
    @Override
    public List<RatingStatisticsExportDto> getAllRatingStatistics() {
        return ratingStatisticsRepo.findAllForExport()
            .stream()
            .map(r -> RatingStatisticsExportDto.builder()
                .id(r.getId())
                .event(r.getRatingPoints().getName())
                .date(r.getCreateDate())
                .userId(r.getUser().getId())
                .userEmail(r.getUser().getEmail())
                .pointsChanged((float) r.getPointsChanged())
                .currentRating((float) r.getRating())
                .build())
            .toList();
    }

    /**
     * Exports all rating statistics to an Excel file.
     *
     * @return InputStream representing the Excel file
     */
    @Transactional(readOnly = true)
    @Override
    public InputStream exportStatisticsToExcel() {
        List<RatingStatisticsExportDto> stats = getAllRatingStatistics()
            .stream()
            .sorted(Comparator.comparing(RatingStatisticsExportDto::getDate).reversed())
            .toList();

        List<Map<String, String>> rows = stats.stream()
            .map(s -> {
                Map<String, String> row = new LinkedHashMap<>();
                row.put("ID", String.valueOf(s.getId()));
                row.put("Event Name", s.getEvent());
                row.put("User ID", String.valueOf(s.getUserId()));
                row.put("User Email", s.getUserEmail());
                row.put("Date", s.getDate().toString());
                row.put("Points Changed", String.valueOf(s.getPointsChanged()));
                row.put("Current Rating", String.valueOf(s.getCurrentRating()));
                return row;
            })
            .toList();

        TableRowsDto table = new TableRowsDto("rating_statistics", rows);
        return exportToFileService.exportTableDataToExcel(table);
    }

    /**
     * Exports filtered rating statistics to an Excel file based on filter criteria.
     *
     * @param dto filter criteria DTO
     * @return InputStream representing the Excel file
     */
    @Override
    @Transactional(readOnly = true)
    public InputStream exportFilteredStatisticsToExcel(RatingStatisticsViewDto dto) {
        List<RatingStatistics> list = ratingStatisticsRepo.findAll(
            getSpecificationWithFetch(dto));
        list = list.stream()
            .sorted(Comparator.comparing(RatingStatistics::getCreateDate).reversed()) // ← добавлено
            .toList();

        List<Map<String, String>> rows = list.stream()
            .map(r -> {
                Map<String, String> row = new LinkedHashMap<>();
                row.put("ID", String.valueOf(r.getId()));
                row.put("Event Name", r.getRatingPoints().getName());
                row.put("User ID", String.valueOf(r.getUser().getId()));
                row.put("User Email", r.getUser().getEmail());
                row.put("Date", r.getCreateDate().toString());
                row.put("Points Changed", String.valueOf(r.getPointsChanged()));
                row.put("Current Rating", String.valueOf(r.getRating()));
                return row;
            })
            .toList();

        return exportToFileService.exportTableDataToExcel(
            new TableRowsDto("filtered_rating_statistics", rows));
    }

    /**
     * Retrieves paginated and filtered rating statistics for management.
     *
     * @param pageable                pageable configuration
     * @param ratingStatisticsViewDto filter DTO
     * @return PageableAdvancedDto containing RatingStatisticsDtoForTables
     */
    @Override
    public PageableAdvancedDto<RatingStatisticsDtoForTables> getFilteredDataForManagementByPage(
        Pageable pageable, RatingStatisticsViewDto ratingStatisticsViewDto) {
        Page<RatingStatistics> ratingStatistics =
            ratingStatisticsRepo.findAll(getSpecification(ratingStatisticsViewDto), pageable);
        return ratingStatisticsDtoMapper(ratingStatistics);
    }

    /**
     * Builds a list of search criteria based on the provided filter DTO.
     *
     * @param ratingStatisticsViewDto DTO with filter parameters
     * @return list of SearchCriteria
     */
    public List<SearchCriteria> buildSearchCriteria(RatingStatisticsViewDto ratingStatisticsViewDto) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        SearchCriteria searchCriteria;
        if (!ratingStatisticsViewDto.getId().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key(RatingStatistics_.ID)
                .type(RatingStatistics_.ID)
                .value(ratingStatisticsViewDto.getId())
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!ratingStatisticsViewDto.getEventName().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key(RatingStatistics_.RATING_POINTS)
                .type(RatingStatistics_.RATING_POINTS)
                .value(ratingStatisticsViewDto.getEventName())
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!ratingStatisticsViewDto.getUserId().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key(RatingStatistics_.USER)
                .type("userId")
                .value(ratingStatisticsViewDto.getUserId())
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!ratingStatisticsViewDto.getUserEmail().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key(RatingStatistics_.USER)
                .type("userMail")
                .value(ratingStatisticsViewDto.getUserEmail())
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!ratingStatisticsViewDto.getStartDate().isEmpty() && !ratingStatisticsViewDto.getEndDate().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key(RatingStatistics_.CREATE_DATE)
                .type("dateRange")
                .value(new String[] {ratingStatisticsViewDto.getStartDate(), ratingStatisticsViewDto.getEndDate()})
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!ratingStatisticsViewDto.getPointsChanged().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key(RatingStatistics_.POINTS_CHANGED)
                .type(RatingStatistics_.POINTS_CHANGED)
                .value(ratingStatisticsViewDto.getPointsChanged())
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!ratingStatisticsViewDto.getCurrentRating().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key(RatingStatistics_.RATING)
                .type("currentRating")
                .value(ratingStatisticsViewDto.getCurrentRating())
                .build();
            criteriaList.add(searchCriteria);
        }
        return criteriaList;
    }

    /**
     * Builds RatingStatisticsSpecification from filter DTO.
     *
     * @param ratingStatisticsViewDto DTO with filter parameters
     * @return RatingStatisticsSpecification
     */
    private RatingStatisticsSpecification getSpecification(RatingStatisticsViewDto ratingStatisticsViewDto) {
        List<SearchCriteria> searchCriteria = buildSearchCriteria(ratingStatisticsViewDto);
        return new RatingStatisticsSpecification(searchCriteria);
    }

    /**
     * Builds a JPA Specification with fetch joins for user and ratingPoints.
     *
     * @param dto filter DTO
     * @return Specification for querying RatingStatistics with fetch
     */
    private Specification<RatingStatistics> getSpecificationWithFetch(RatingStatisticsViewDto dto) {
        return (root, query, cb) -> {
            root.fetch("user", JoinType.LEFT);
            root.fetch("ratingPoints", JoinType.LEFT);
            assert query != null;
            query.distinct(true);

            return getSpecification(dto).toPredicate(root, query, cb);
        };
    }
}
