package greencity.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import greencity.dto.category.CategoryDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.entity.enums.EmailNotification;
import greencity.service.EmailService;
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
    private PlaceAuthorDto placeAuthorDto;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private ITemplateEngine templateEngine;

    @Before
    public void setup() {
        initMocks(this);
        service = new EmailServiceImpl(javaMailSender, templateEngine,
            "http://localhost:4200", "http://localhost:4200", "http://localhost:8080",
            "test@email.com");
        user = User.builder()
            .id(1L)
            .verifyEmail(new VerifyEmail())
            .firstName("testFirstName")
            .email("testEmail@gmail.com")
            .build();
        placeAuthorDto = PlaceAuthorDto.builder()
            .id(1L)
            .email("testEmail@gmail.com")
            .firstName("testName")
            .lastName("testLastName")
            .build();

        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    public void sendChangePlaceStatusEmailTest() {
        String authorFirstName = "test author first name";
        String placeName = "test place name";
        String placeStatus = "test place status";
        String authorEmail = "test author email";
        service.sendChangePlaceStatusEmail(authorFirstName, placeName, placeStatus, authorEmail);

        verify(javaMailSender).createMimeMessage();
    }


    @Test
    public void sendAddedNewPlacesReportEmailTest() {
        CategoryDto testCategory = CategoryDto.builder().name("CategoryName").build();
        PlaceNotificationDto testPlace1 = PlaceNotificationDto.builder().name("PlaceName1").category(testCategory).build();
        PlaceNotificationDto testPlace2 = PlaceNotificationDto.builder().name("PlaceName2").category(testCategory).build();
        Map<CategoryDto, List<PlaceNotificationDto>> categoriesWithPlacesTest = new HashMap<>();
        categoriesWithPlacesTest.put(testCategory, Arrays.asList(testPlace1, testPlace2));

        service.sendAddedNewPlacesReportEmail(
            Collections.singletonList(placeAuthorDto), categoriesWithPlacesTest, EmailNotification.DAILY.toString());

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
}