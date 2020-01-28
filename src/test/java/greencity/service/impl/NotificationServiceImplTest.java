package greencity.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import greencity.GreenCityApplication;
import greencity.dto.user.PlaceAuthorDto;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.PlaceStatus;
import greencity.repository.PlaceRepo;
import greencity.repository.UserRepo;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
@Ignore
public class NotificationServiceImplTest {
    @InjectMocks
    private NotificationServiceImpl notificationService;
    @Mock
    private PlaceRepo placeRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private RabbitTemplate rabbitTemplate;
    private List<Place> testPlaces;
    private EmailNotification emailNotification;

    @Before
    public void setUp() {
        Category testCategory = Category.builder().name("CategoryName").build();
        Place testPlace1 = Place.builder().name("PlaceName1").category(testCategory).build();
        Place testPlace2 = Place.builder().name("PlaceName2").category(testCategory).build();
        testPlaces = Arrays.asList(testPlace1, testPlace2);

        when(placeRepo.findAllByModifiedDateBetweenAndStatus(
            any(LocalDateTime.class),
            any(LocalDateTime.class),
            eq(PlaceStatus.APPROVED)
        )).thenReturn(testPlaces);
    }

    @Test
    public void sendImmediatelyReportTest() {
        emailNotification = EmailNotification.IMMEDIATELY;
        User testUser = User.builder().emailNotification(emailNotification).build();
        when(userRepo.findAllByEmailNotification(emailNotification)).thenReturn(Collections.singletonList(testUser));

        notificationService.sendImmediatelyReport(testPlaces.get(0));
        doNothing().when(rabbitTemplate).convertAndSend((Object) any(), any(), any());
        verify(userRepo).findAllByEmailNotification(emailNotification);
    }

    @Test
    public void sendDailyReportTest() {
        emailNotification = EmailNotification.DAILY;
        User testUser = User.builder().emailNotification(emailNotification).build();
        when(userRepo.findAllByEmailNotification(emailNotification)).thenReturn(Collections.singletonList(testUser));

        notificationService.sendDailyReport();

        verify(placeRepo).findAllByModifiedDateBetweenAndStatus(
            any(LocalDateTime.class),
            any(LocalDateTime.class),
            eq(PlaceStatus.APPROVED));
        verify(userRepo).findAllByEmailNotification(emailNotification);
    }

    @Test
    public void sendWeeklyReportTest() {
        emailNotification = EmailNotification.WEEKLY;
        User testUser = User.builder().emailNotification(emailNotification).build();
        when(userRepo.findAllByEmailNotification(emailNotification)).thenReturn(Collections.singletonList(testUser));

        notificationService.sendWeeklyReport();

        verify(placeRepo).findAllByModifiedDateBetweenAndStatus(
            any(LocalDateTime.class),
            any(LocalDateTime.class),
            eq(PlaceStatus.APPROVED));
        verify(userRepo).findAllByEmailNotification(emailNotification);
    }

    @Test
    public void sendMonthlyReportTest() {
        emailNotification = EmailNotification.MONTHLY;
        User testUser = User.builder().emailNotification(emailNotification).build();
        when(userRepo.findAllByEmailNotification(emailNotification)).thenReturn(Collections.singletonList(testUser));

        notificationService.sendMonthlyReport();

        verify(placeRepo).findAllByModifiedDateBetweenAndStatus(
            any(LocalDateTime.class),
            any(LocalDateTime.class),
            eq(PlaceStatus.APPROVED));
        verify(userRepo).findAllByEmailNotification(emailNotification);
    }
}