package greencity.service;

import greencity.client.UserRemoteClient;
import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserLocationStatisticDto;
import greencity.dto.user.UserRegistrationStatisticDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.enums.DateGranularity;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManagementUserStatisticsServiceImplTest {
    @Mock
    UserRepo userRepo;

    @Mock
    UserService userService;

    @Mock
    UserRemoteClient userRemoteClient;

    @InjectMocks
    ManagementUserStatisticsServiceImpl managementUserStatisticsServiceImpl;

    @Test
    void testGetUserRegistrationsByDateRange() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        DateGranularity dateGranularity = DateGranularity.DAY;

        List<UserRegistrationStatisticDto> expectedResult = mock(List.class);

        when(userRemoteClient.getUserRegistrationsByDateRange(startDate, endDate, dateGranularity))
            .thenReturn(expectedResult);

        List<UserRegistrationStatisticDto> actualResult =
            managementUserStatisticsServiceImpl.getUserRegistrationsByDateRange(startDate, endDate, dateGranularity);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testGetUserRolesDistribution() {
        List<UserRoleStatisticDto> mockResult = List.of(new UserRoleStatisticDto(Role.ROLE_USER, 100L));
        when(userRemoteClient.getUserRolesDistribution()).thenReturn(mockResult);

        List<UserRoleStatisticDto> result = managementUserStatisticsServiceImpl.getUserRolesDistribution();

        assertEquals(mockResult, result);
        verify(userRemoteClient, times(1)).getUserRolesDistribution();
    }

    @Test
    void testGetUserStatusesDistribution() {
        List<UserStatusStatisticDto> mockResult = List.of(new UserStatusStatisticDto(UserStatus.ACTIVATED, 200L));
        when(userRepo.getUserStatusesDistribution()).thenReturn(mockResult);

        List<UserStatusStatisticDto> result = managementUserStatisticsServiceImpl.getUserStatusesDistribution();

        assertEquals(mockResult, result);
        verify(userRepo, times(1)).getUserStatusesDistribution();
    }

    @Test
    void testGetUserEmailPreferencesDistribution() {
        List<UserEmailPreferencesStatisticDto> mockResult =
            List.of(new UserEmailPreferencesStatisticDto(EmailPreference.LIKES, EmailPreferencePeriodicity.DAILY, 25L));
        when(userRemoteClient.getUserEmailPreferencesDistribution()).thenReturn(mockResult);

        List<UserEmailPreferencesStatisticDto> result =
            managementUserStatisticsServiceImpl.getUserEmailPreferencesDistribution();

        assertEquals(mockResult, result);
        verify(userRemoteClient, times(1)).getUserEmailPreferencesDistribution();
    }

    @Test
    void testGetUserLocationsDistributionCity() {
        List<UserLocationStatisticDto> mockResult = List.of(new UserLocationStatisticDto("City", 50L));
        when(userRepo.getUserLocationsDistributionByCity()).thenReturn(mockResult);

        List<UserLocationStatisticDto> result =
            managementUserStatisticsServiceImpl.getUserLocationsDistribution("city");

        assertEquals(mockResult, result);
        verify(userRepo).getUserLocationsDistributionByCity();
    }

    @Test
    void testGetUserLocationsDistributionRegion() {
        List<UserLocationStatisticDto> mockResult = List.of(new UserLocationStatisticDto("Dnipropetrovsk", 30L));
        when(userRepo.getUserLocationsDistributionByRegion()).thenReturn(mockResult);

        List<UserLocationStatisticDto> result =
            managementUserStatisticsServiceImpl.getUserLocationsDistribution("region");

        assertEquals(mockResult, result);
        verify(userRepo).getUserLocationsDistributionByRegion();
    }

    @Test
    void testGetUserLocationsDistributionCountry() {
        List<UserLocationStatisticDto> mockResult = List.of(new UserLocationStatisticDto("Ukraine", 70L));
        when(userRepo.getUserLocationsDistributionByCountry()).thenReturn(mockResult);

        List<UserLocationStatisticDto> result =
            managementUserStatisticsServiceImpl.getUserLocationsDistribution("country");

        assertEquals(mockResult, result);
        verify(userRepo).getUserLocationsDistributionByCountry();
    }
}