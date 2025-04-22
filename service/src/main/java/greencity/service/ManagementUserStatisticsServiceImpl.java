package greencity.service;

import greencity.client.UserRemoteClient;
import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserLocationStatisticDto;
import greencity.dto.user.UserRegistrationStatisticDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.enums.DateGranularity;
import greencity.repository.UserRepo;
import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ManagementUserStatisticsServiceImpl implements ManagementUserStatisticsService {
    private UserRepo userRepo;
    private UserRemoteClient userRemoteClient;

    @Override
    public List<UserRegistrationStatisticDto> getUserRegistrationsByDateRange(LocalDateTime startDate,
        LocalDateTime endDate, DateGranularity granularity) {
        String granularityStr = granularity.toString();
        List<Tuple> results = userRepo.countUsersByRegistrationDateBetween(startDate, endDate, granularityStr);
        return results.stream()
            .map(tuple -> new UserRegistrationStatisticDto(
                ((Timestamp) tuple.get(0)).toLocalDateTime(),
                tuple.get(1, Long.class)))
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserRoleStatisticDto> getUserRolesDistribution() {
        return userRemoteClient.getUserRolesDistribution();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserStatusStatisticDto> getUserStatusesDistribution() {
        return userRemoteClient.getUserStatusesDistribution();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserLocationStatisticDto> getUserLocationsDistribution(String groupBy) {
        return switch (groupBy) {
            case "city" -> userRepo.getUserLocationsDistributionByCity();
            case "region" -> userRepo.getUserLocationsDistributionByRegion();
            case "country" -> userRepo.getUserLocationsDistributionByCountry();
            default -> userRepo.getUserLocationsDistributionByCity();
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserEmailPreferencesStatisticDto> getUserEmailPreferencesDistribution() {
        return userRemoteClient.getUserEmailPreferencesDistribution();
    }
}
