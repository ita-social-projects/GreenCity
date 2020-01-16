package greencity.service.impl;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.entity.*;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.PlaceStatus;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.events.SendNewsEvent;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.service.EmailService;
import greencity.service.NewsSubscriberService;
import java.time.ZonedDateTime;
import java.util.*;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;

public class EmailServiceImplTest {
    private EmailService service;
    private User user;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private ITemplateEngine templateEngine;
    @Mock
    private NewsSubscriberService newsSubscriberService;
    @Mock
    private EcoNewsTranslationRepo ecoNewsTranslationRepo;
    @Mock
    private EcoNewsRepo ecoNewsRepo;

    @Before
    public void setup() {
        initMocks(this);
        service = new EmailServiceImpl(javaMailSender, templateEngine,
            "http://localhost:4200", "http://localhost:4200", "http://localhost:8080",
            "test@email.com", newsSubscriberService, ecoNewsTranslationRepo, ecoNewsRepo);
        user = User.builder()
            .id(1L)
            .verifyEmail(new VerifyEmail())
            .firstName("testFirstName")
            .email("testEmail@gmail.com")
            .build();

        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    public void sendChangePlaceStatusEmailTest() {
        Place generatedEntity = Place.builder().author(user).name("TestPlace").status(PlaceStatus.APPROVED).build();
        service.sendChangePlaceStatusEmail(generatedEntity);

        verify(javaMailSender).createMimeMessage();
    }


    @Test
    public void sendAddedNewPlacesReportEmailTest() {
        Category testCategory = Category.builder().name("CategoryName").build();
        Place testPlace1 = Place.builder().name("PlaceName1").category(testCategory).build();
        Place testPlace2 = Place.builder().name("PlaceName2").category(testCategory).build();
        Map<Category, List<Place>> categoriesWithPlacesTest = new HashMap<>();
        categoriesWithPlacesTest.put(testCategory, Arrays.asList(testPlace1, testPlace2));

        service.sendAddedNewPlacesReportEmail(
            Collections.singletonList(user), categoriesWithPlacesTest, EmailNotification.DAILY);

        verify(javaMailSender).createMimeMessage();
    }

    @Test
    public void sendVerificationEmailTest() {
        service.sendVerificationEmail(user);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    public void sendRestoreEmailTest() {
        service.sendRestoreEmail(user, "");
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    public void sendNewsForSubscriberListenerTest() {
        SendNewsEvent event = new SendNewsEvent(new Object(), AddEcoNewsDtoResponse.builder()
            .id(1L)
            .imagePath("imagePath")
            .text("text")
            .title("Заголовок")
            .creationDate(ZonedDateTime.now())
            .build());
        AddEcoNewsDtoResponse response = AddEcoNewsDtoResponse.builder()
            .id(event.getBody().getId())
            .title(event.getBody().getTitle())
            .imagePath(event.getBody().getImagePath())
            .text(event.getBody().getText())
            .creationDate(event.getBody().getCreationDate())
            .build();
        response.setTitle("Title");
        EcoNewsTranslation translation = new EcoNewsTranslation(1L, null, "Title", null);
        List<NewsSubscriberResponseDto> subscribers = Collections.singletonList(
            new NewsSubscriberResponseDto("email", "token"));

        when(ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(any(EcoNews.class), anyString()))
            .thenReturn(translation);
        when(newsSubscriberService.findAll()).thenReturn(subscribers);
        when(ecoNewsRepo.findById(anyLong())).thenReturn(Optional.of(new EcoNews()));

        EmailService emailService = spy(service);

        emailService.sendNewNewsForSubscriberListener(event);
        verify(emailService).sendNewNewsForSubscriber(subscribers, response);
    }
}