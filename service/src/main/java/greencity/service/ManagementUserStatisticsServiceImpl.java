package greencity.service;

import greencity.client.UserRemoteClient;
import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserLocationStatisticDto;
import greencity.dto.user.UserRegistrationStatisticDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.enums.DateGranularity;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ManagementUserStatisticsServiceImpl implements ManagementUserStatisticsService {
    private UserRepo userRepo;
    private UserRemoteClient userRemoteClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserRegistrationStatisticDto> getUserRegistrationsByDateRange(LocalDateTime startDate,
        LocalDateTime endDate, DateGranularity granularity) {
        return userRemoteClient.getUserRegistrationsByDateRange(startDate, endDate, granularity);
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
        List<Long> activatedUserIds = userRemoteClient.getActivatedUsersIds();
        return switch (groupBy) {
            case "city" -> userRepo.getUserLocationsDistributionByCity(activatedUserIds);
            case "region" -> userRepo.getUserLocationsDistributionByRegion(activatedUserIds);
            case "country" -> userRepo.getUserLocationsDistributionByCountry(activatedUserIds);
            default -> userRepo.getUserLocationsDistributionByCity(activatedUserIds);
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
