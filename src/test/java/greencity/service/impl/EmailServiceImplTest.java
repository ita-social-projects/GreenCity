package greencity.service.impl;

import static org.mockito.Mockito.*;

import greencity.GreenCityApplication;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.entity.enums.PlaceStatus;
import greencity.service.EmailService;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
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

        verify(javaMailSender, times(1)).createMimeMessage();
    }
}