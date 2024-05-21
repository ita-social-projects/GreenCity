package greencity.service;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.dto.category.CategoryDto;
import greencity.dto.category.CategoryVO;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.place.PlaceVO;
import greencity.dto.user.PlaceAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.enums.EmailNotification;
import greencity.message.GeneralEmailMessage;
import greencity.message.HabitAssignNotificationMessage;
import greencity.message.SendReportEmailMessage;
import greencity.repository.PlaceRepo;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private PlaceRepo placeRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RestClient restClient;

    @Test
    void sendImmediatelyReportTest() {
        EmailNotification emailNotification = EmailNotification.IMMEDIATELY;
        CategoryVO category = CategoryVO.builder()
            .id(12L)
            .name("category")
            .build();

        UserVO userVO = ModelUtils.getUserVO();
        userVO.setEmailNotification(emailNotification);

        PlaceVO place = new PlaceVO();
//        place.setLocationId(1L);
        place.setId(1L);
        place.setName("Forum");
        place.setDescription("Shopping center");
        place.setPhone("0322 489 850");
        place.setEmail("forum_lviv@gmail.com");
        place.setModifiedDate(ZonedDateTime.now());
        place.setCategory(category);

        when(restClient.findAllByEmailNotification(emailNotification))
            .thenReturn(Collections.singletonList(userVO));
        when(modelMapper.map(userVO, PlaceAuthorDto.class))
            .thenReturn(new PlaceAuthorDto(1L, "dto", "email"));
        when(modelMapper.map(place.getCategory(), CategoryDto.class))
            .thenReturn(new CategoryDto("category", "test", null));
        when(modelMapper.map(place, PlaceNotificationDto.class))
            .thenReturn(new PlaceNotificationDto("name", new CategoryDto("category", "test", null)));

        notificationService.sendImmediatelyReport(place);

        verify(restClient, Mockito.times(1))
            .sendReport(any(SendReportEmailMessage.class));
    }

    @Test
    void sendDailyReportTest() {
        EmailNotification emailNotification = EmailNotification.DAILY;
        Category category = ModelUtils.getCategory();
        UserVO userVO = ModelUtils.getUserVO();
        userVO.setEmailNotification(emailNotification);

        Place testPlace1 = ModelUtils.getPlace();
        testPlace1.setCategory(category);
        testPlace1.setId(1L);

        Place testPlace2 = ModelUtils.getPlace();
        testPlace1.setCategory(category);
        testPlace1.setId(2L);

        List<Place> testPlaces = Arrays.asList(testPlace1, testPlace2);

        when(restClient.findAllByEmailNotification(emailNotification))
            .thenReturn(Collections.singletonList(userVO));
        when(modelMapper.map(userVO, PlaceAuthorDto.class))
            .thenReturn(new PlaceAuthorDto(1L, "dto", "email"));
        when(placeRepo.findAllByModifiedDateBetweenAndStatus(any(LocalDateTime.class), any(LocalDateTime.class), any()))
            .thenReturn(testPlaces);
        when(modelMapper.map(testPlace1, PlaceNotificationDto.class))
            .thenReturn(new PlaceNotificationDto("name", new CategoryDto("category", "test", null)));
        when(modelMapper.map(testPlace2, PlaceNotificationDto.class))
            .thenReturn(new PlaceNotificationDto("name1", new CategoryDto("category1", "test", null)));
        when(modelMapper.map(testPlace1.getCategory(), CategoryDto.class))
            .thenReturn(new CategoryDto("category", "test", null));
        when(modelMapper.map(testPlace2.getCategory(), CategoryDto.class))
            .thenReturn(new CategoryDto("category1", "test", null));

        notificationService.sendDailyReport();

        verify(restClient, Mockito.times(1))
            .sendReport(any(SendReportEmailMessage.class));

    }

    @Test
    void sendWeeklyReportTest() {
        EmailNotification emailNotification = EmailNotification.WEEKLY;
        Category category = ModelUtils.getCategory();
        UserVO userVO = ModelUtils.getUserVO();
        userVO.setEmailNotification(emailNotification);

        Place testPlace1 = ModelUtils.getPlace();
        testPlace1.setCategory(category);
        testPlace1.setId(1L);

        Place testPlace2 = ModelUtils.getPlace();
        testPlace1.setCategory(category);
        testPlace1.setId(2L);

        List<Place> testPlaces = Arrays.asList(testPlace1, testPlace2);

        when(restClient.findAllByEmailNotification(emailNotification))
            .thenReturn(Collections.singletonList(userVO));
        when(modelMapper.map(userVO, PlaceAuthorDto.class))
            .thenReturn(new PlaceAuthorDto(1L, "dto", "email"));
        when(placeRepo.findAllByModifiedDateBetweenAndStatus(any(LocalDateTime.class), any(LocalDateTime.class), any()))
            .thenReturn(testPlaces);
        when(modelMapper.map(testPlace1, PlaceNotificationDto.class))
            .thenReturn(new PlaceNotificationDto("name", new CategoryDto("category", "test", null)));
        when(modelMapper.map(testPlace2, PlaceNotificationDto.class))
            .thenReturn(new PlaceNotificationDto("name1", new CategoryDto("category1", "test", null)));
        when(modelMapper.map(testPlace1.getCategory(), CategoryDto.class))
            .thenReturn(new CategoryDto("category", "test", null));
        when(modelMapper.map(testPlace2.getCategory(), CategoryDto.class))
            .thenReturn(new CategoryDto("category1", "test", null));

        notificationService.sendWeeklyReport();

        verify(restClient, Mockito.times(1))
            .sendReport(any(SendReportEmailMessage.class));

    }

    @Test
    void sendMonthlyReportTest() {
        EmailNotification emailNotification = EmailNotification.MONTHLY;
        Category category = ModelUtils.getCategory();
        UserVO userVO = ModelUtils.getUserVO();
        userVO.setEmailNotification(emailNotification);

        Place testPlace1 = ModelUtils.getPlace();
        testPlace1.setCategory(category);
        testPlace1.setId(1L);

        Place testPlace2 = ModelUtils.getPlace();
        testPlace1.setCategory(category);
        testPlace1.setId(2L);

        List<Place> testPlaces = Arrays.asList(testPlace1, testPlace2);

        when(restClient.findAllByEmailNotification(emailNotification))
            .thenReturn(Collections.singletonList(userVO));
        when(modelMapper.map(userVO, PlaceAuthorDto.class))
            .thenReturn(new PlaceAuthorDto(1L, "dto", "email"));
        when(placeRepo.findAllByModifiedDateBetweenAndStatus(any(LocalDateTime.class), any(LocalDateTime.class), any()))
            .thenReturn(testPlaces);
        when(modelMapper.map(testPlace1, PlaceNotificationDto.class))
            .thenReturn(new PlaceNotificationDto("name", new CategoryDto("category", "test", null)));
        when(modelMapper.map(testPlace2, PlaceNotificationDto.class))
            .thenReturn(new PlaceNotificationDto("name1", new CategoryDto("category1", "test", null)));
        when(modelMapper.map(testPlace1.getCategory(), CategoryDto.class))
            .thenReturn(new CategoryDto("category", "test", null));
        when(modelMapper.map(testPlace2.getCategory(), CategoryDto.class))
            .thenReturn(new CategoryDto("category1", "test", null));

        notificationService.sendMonthlyReport();

        verify(restClient, Mockito.times(1))
            .sendReport(any(SendReportEmailMessage.class));
    }

    @Test
    void sendEmailNotificationToManyUsersTest() {
        Set<String> setOfEmails = new HashSet<>(Arrays.asList("test1@gmail.com", "test2@gmail.com", "test3@gmail.com"));
        String subject = "new notification";
        String message = "check your email box";
        ArgumentCaptor<GeneralEmailMessage> emailMessageCaptor = ArgumentCaptor.forClass(GeneralEmailMessage.class);
        notificationService.sendEmailNotification(setOfEmails, subject, message);
        await().atMost(5, SECONDS)
            .untilAsserted(() -> verify(restClient, times(3)).sendEmailNotification(emailMessageCaptor.capture()));
        List<GeneralEmailMessage> capturedEmailMessages = emailMessageCaptor.getAllValues();
        assertFalse(capturedEmailMessages.isEmpty());
        for (GeneralEmailMessage capturedEmailMessage : capturedEmailMessages) {
            assertTrue(setOfEmails.contains(capturedEmailMessage.getEmail()));
            assertEquals(subject, capturedEmailMessage.getSubject());
            assertEquals(message, capturedEmailMessage.getMessage());
        }
    }

    @Test
    void sendEmailNotificationToOneUserTest() {
        String email = "test1@gmail.com";
        String subject = "new notification";
        String message = "check your email box";
        GeneralEmailMessage generalEmailMessage = new GeneralEmailMessage(email, subject, message);
        ArgumentCaptor<GeneralEmailMessage> emailMessageCaptor = ArgumentCaptor.forClass(GeneralEmailMessage.class);
        notificationService.sendEmailNotification(generalEmailMessage);
        await().atMost(5, SECONDS)
            .untilAsserted(() -> verify(restClient).sendEmailNotification(emailMessageCaptor.capture()));
        GeneralEmailMessage capturedEmailMessage = emailMessageCaptor.getValue();
        assertEquals(email, capturedEmailMessage.getEmail());
        assertEquals(subject, capturedEmailMessage.getSubject());
        assertEquals(message, capturedEmailMessage.getMessage());
    }

    @Test
    void sendHabitAssignEmailNotification() {
        HabitAssignNotificationMessage message = new HabitAssignNotificationMessage(
            "sender", "receiver", "receiver@example.com", "habit", "en", 123L);
        ArgumentCaptor<HabitAssignNotificationMessage> emailMessageCaptor =
            ArgumentCaptor.forClass(HabitAssignNotificationMessage.class);
        notificationService.sendHabitAssignEmailNotification(message);
        await().atMost(5, SECONDS)
            .untilAsserted(() -> verify(restClient).sendHabitAssignNotification(emailMessageCaptor.capture()));
        HabitAssignNotificationMessage capturedMessage = emailMessageCaptor.getValue();
        assertEquals(message.getReceiverEmail(), capturedMessage.getReceiverEmail());
        assertEquals(message.getHabitAssignId(), capturedMessage.getHabitAssignId());
        assertEquals(message.getHabitName(), capturedMessage.getHabitName());
        assertEquals(message.getLanguage(), capturedMessage.getLanguage());
        assertEquals(message.getSenderName(), capturedMessage.getSenderName());
    }
}
