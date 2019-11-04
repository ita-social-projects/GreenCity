package greencity.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.PlaceStatus;
import greencity.service.EmailService;
import java.util.*;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;

@RunWith(MockitoJUnitRunner.class)
public class EmailServiceImplTest {
    private EmailService service;
    private User user;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private ITemplateEngine templateEngine;

    @Before
    public void setup() {
        service = new EmailServiceImpl(javaMailSender, templateEngine);
        user = User.builder().firstName("testFirstName").email("testEmail@gmail.com").build();

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
        service.sendVerificationEmail(user, "");
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    public void sendRestoreEmailTest() {
        service.sendRestoreEmail(user, "");
        verify(javaMailSender).createMimeMessage();
    }
}