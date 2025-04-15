package greencity.service;

import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserLocationStatisticDto;
import greencity.dto.user.UserRegistrationStatisticDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.exception.exceptions.NotFoundException;
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
        LocalDateTime endDate, String granularity) {
        List<Tuple> results = userRepo.countUsersByRegistrationDateBetween(startDate, endDate, granularity);
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
        return userRemoteClient.getUserRolesDistribution()
                .orElseThrow(() -> new NotFoundException());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserStatusStatisticDto> getUserStatusesDistribution() {
        return userRemoteClient.getUserStatusesDistribution()
                .orElseThrow(() -> new NotFoundException());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserLocationStatisticDto> getUserLocationsDistribution(String groupBy) {
        return userRemoteClient.getUserLocationsDistribution(groupBy)
                .orElseThrow(() -> new NotFoundException());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserEmailPreferencesStatisticDto> getUserEmailPreferencesDistribution() {
        return userRemoteClient.getUserEmailPreferencesDistribution()
                .orElseThrow(() -> new NotFoundException());
    }
}
