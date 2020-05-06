package greencity.service.impl;

import greencity.ModelUtils;
import greencity.dto.category.CategoryDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.message.SendReportEmailMessage;
import greencity.repository.PlaceRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private PlaceRepo placeRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepo userRepo;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private EmailNotification emailNotification;
    private Category category = ModelUtils.getCategory();
    private User user = ModelUtils.getUser();

    @BeforeEach
    void setUp() {
        Place testPlace1 = ModelUtils.getPlace();
        testPlace1.setCategory(category);
        testPlace1.setId(1L);

        Place testPlace2 = ModelUtils.getPlace();
        testPlace1.setCategory(category);
        testPlace1.setId(2L);

        List<Place> testPlaces = Arrays.asList(testPlace1, testPlace2);

        when(placeRepo.findAllByModifiedDateBetweenAndStatus(any(LocalDateTime.class), any(LocalDateTime.class), any()))
                .thenReturn(testPlaces);
        when(modelMapper.map(testPlace1, PlaceNotificationDto.class))
                .thenReturn(new PlaceNotificationDto("name", new CategoryDto("category")));
        when(modelMapper.map(testPlace2, PlaceNotificationDto.class))
                .thenReturn(new PlaceNotificationDto("name1", new CategoryDto("category1")));
        when(modelMapper.map(testPlace1.getCategory(), CategoryDto.class))
                .thenReturn(new CategoryDto("category"));
        when(modelMapper.map(testPlace2.getCategory(), CategoryDto.class))
                .thenReturn(new CategoryDto("category1"));
    }

    @Test
    void sendImmediatelyReportTest() {
        emailNotification = EmailNotification.IMMEDIATELY;

        Place place = ModelUtils.getPlace();
        place.setCategory(category);

        user.setEmailNotification(emailNotification);

        when(userRepo.findAllByEmailNotification(emailNotification))
                .thenReturn(Collections.singletonList(user));
        when(modelMapper.map(user, PlaceAuthorDto.class))
                .thenReturn(new PlaceAuthorDto(1L, "dto", "email"));
        when(modelMapper.map(place.getCategory(), CategoryDto.class))
                .thenReturn(new CategoryDto("category"));
        when(modelMapper.map(place, PlaceNotificationDto.class))
                .thenReturn(new PlaceNotificationDto("name", new CategoryDto("category")));

        notificationService.sendImmediatelyReport(place);

        verify(rabbitTemplate, Mockito.times(1))
                .convertAndSend(any(), anyString(), any(SendReportEmailMessage.class));
    }

    @Test
    void sendDailyReportTest() {
        emailNotification = EmailNotification.DAILY;

        user.setEmailNotification(emailNotification);

        when(userRepo.findAllByEmailNotification(emailNotification))
                .thenReturn(Collections.singletonList(user));
        when(modelMapper.map(user, PlaceAuthorDto.class))
                .thenReturn(new PlaceAuthorDto(1L, "dto", "email"));

        notificationService.sendDailyReport();

        verify(rabbitTemplate, Mockito.times(1))
                .convertAndSend(any(), anyString(), any(SendReportEmailMessage.class));

    }

    @Test
    void sendWeeklyReportTest() {
        emailNotification = EmailNotification.WEEKLY;

        user.setEmailNotification(emailNotification);

        when(userRepo.findAllByEmailNotification(emailNotification))
                .thenReturn(Collections.singletonList(user));
        when(modelMapper.map(user, PlaceAuthorDto.class))
                .thenReturn(new PlaceAuthorDto(1L, "dto", "email"));

        notificationService.sendWeeklyReport();

        verify(rabbitTemplate, Mockito.times(1))
                .convertAndSend(any(), anyString(), any(SendReportEmailMessage.class));

    }

    @Test
    void sendMonthlyReportTest() {
        emailNotification = EmailNotification.MONTHLY;

        user.setEmailNotification(emailNotification);

        when(userRepo.findAllByEmailNotification(emailNotification))
                .thenReturn(Collections.singletonList(user));
        when(modelMapper.map(user, PlaceAuthorDto.class))
                .thenReturn(new PlaceAuthorDto(1L, "dto", "email"));

        notificationService.sendMonthlyReport();

        verify(rabbitTemplate, Mockito.times(1))
                .convertAndSend(any(), anyString(), any(SendReportEmailMessage.class));
    }
}