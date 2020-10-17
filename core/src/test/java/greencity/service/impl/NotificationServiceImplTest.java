package greencity.service.impl;

import greencity.ModelUtils;
import greencity.dto.category.CategoryDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.message.SendReportEmailMessage;
import greencity.repository.PlaceRepo;
import greencity.repository.UserRepo;
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

    @Test
    void sendImmediatelyReportTest() {
        EmailNotification emailNotification = EmailNotification.IMMEDIATELY;
        Category category = ModelUtils.getCategory();
        User user = ModelUtils.getUser();
        user.setEmailNotification(emailNotification);

        Place place = ModelUtils.getPlace();
        place.setCategory(category);


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
        EmailNotification emailNotification = EmailNotification.DAILY;
        Category category = ModelUtils.getCategory();
        User user = ModelUtils.getUser();
        user.setEmailNotification(emailNotification);

        Place testPlace1 = ModelUtils.getPlace();
        testPlace1.setCategory(category);
        testPlace1.setId(1L);

        Place testPlace2 = ModelUtils.getPlace();
        testPlace1.setCategory(category);
        testPlace1.setId(2L);

        List<Place> testPlaces = Arrays.asList(testPlace1, testPlace2);

        when(userRepo.findAllByEmailNotification(emailNotification))
                .thenReturn(Collections.singletonList(user));
        when(modelMapper.map(user, PlaceAuthorDto.class))
                .thenReturn(new PlaceAuthorDto(1L, "dto", "email"));
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

        notificationService.sendDailyReport();

        verify(rabbitTemplate, Mockito.times(1))
                .convertAndSend(any(), anyString(), any(SendReportEmailMessage.class));

    }

    @Test
    void sendWeeklyReportTest() {
        EmailNotification emailNotification = EmailNotification.WEEKLY;
        Category category = ModelUtils.getCategory();
        User user = ModelUtils.getUser();
        user.setEmailNotification(emailNotification);

        Place testPlace1 = ModelUtils.getPlace();
        testPlace1.setCategory(category);
        testPlace1.setId(1L);

        Place testPlace2 = ModelUtils.getPlace();
        testPlace1.setCategory(category);
        testPlace1.setId(2L);

        List<Place> testPlaces = Arrays.asList(testPlace1, testPlace2);

        when(userRepo.findAllByEmailNotification(emailNotification))
                .thenReturn(Collections.singletonList(user));
        when(modelMapper.map(user, PlaceAuthorDto.class))
                .thenReturn(new PlaceAuthorDto(1L, "dto", "email"));
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

        notificationService.sendWeeklyReport();

        verify(rabbitTemplate, Mockito.times(1))
                .convertAndSend(any(), anyString(), any(SendReportEmailMessage.class));

    }

    @Test
    void sendMonthlyReportTest() {
        EmailNotification emailNotification = EmailNotification.MONTHLY;
        Category category = ModelUtils.getCategory();
        User user = ModelUtils.getUser();
        user.setEmailNotification(emailNotification);

        Place testPlace1 = ModelUtils.getPlace();
        testPlace1.setCategory(category);
        testPlace1.setId(1L);

        Place testPlace2 = ModelUtils.getPlace();
        testPlace1.setCategory(category);
        testPlace1.setId(2L);

        List<Place> testPlaces = Arrays.asList(testPlace1, testPlace2);

        when(userRepo.findAllByEmailNotification(emailNotification))
                .thenReturn(Collections.singletonList(user));
        when(modelMapper.map(user, PlaceAuthorDto.class))
                .thenReturn(new PlaceAuthorDto(1L, "dto", "email"));
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

        notificationService.sendMonthlyReport();

        verify(rabbitTemplate, Mockito.times(1))
                .convertAndSend(any(), anyString(), any(SendReportEmailMessage.class));
    }
}