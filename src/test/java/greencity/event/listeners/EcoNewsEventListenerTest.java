package greencity.event.listeners;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.event.SendNewsEvent;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.service.EmailService;
import greencity.service.NewsSubscriberService;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EcoNewsEventListenerTest {
    @Mock
    private EcoNewsTranslationRepo ecoNewsTranslationRepo;
    @Mock
    private EcoNewsRepo ecoNewsRepo;
    @Mock
    private NewsSubscriberService newsSubscriberService;
    @Spy
    private EmailService emailService;
    @InjectMocks
    private SendNewsEventListener eventListener;

    private SendNewsEvent event = new SendNewsEvent(new Object(), AddEcoNewsDtoResponse.builder()
        .id(1L)
        .imagePath("imagePath")
        .text("text")
        .title("Заголовок")
        .creationDate(ZonedDateTime.now())
        .build());
    private EcoNewsTranslation translation = new EcoNewsTranslation(1L, null, "Title", null);
    private List<NewsSubscriberResponseDto> subscribers = Collections.singletonList(
        new NewsSubscriberResponseDto("email", "token"));

    @Test
    public void sendNewsForSubscriberListenerTest() {
        AddEcoNewsDtoResponse response = AddEcoNewsDtoResponse.builder()
            .id(event.getBody().getId())
            .title(event.getBody().getTitle())
            .imagePath(event.getBody().getImagePath())
            .text(event.getBody().getText())
            .creationDate(event.getBody().getCreationDate())
            .build();
        response.setTitle("Title");

        when(ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(any(EcoNews.class), anyString()))
            .thenReturn(translation);
        when(newsSubscriberService.findAll()).thenReturn(subscribers);
        when(ecoNewsRepo.findById(anyLong())).thenReturn(Optional.of(new EcoNews()));
        doNothing().when(emailService).sendNewNewsForSubscriber(any(), any());

        eventListener.onApplicationEvent(event);

        verify(emailService).sendNewNewsForSubscriber(subscribers, response);
    }

    @Test(expected = NotFoundException.class)
    public void sendNewsForSubscriberListenerTestFailsWithNotFoundException() {
        AddEcoNewsDtoResponse response = AddEcoNewsDtoResponse.builder()
            .id(event.getBody().getId())
            .title(event.getBody().getTitle())
            .imagePath(event.getBody().getImagePath())
            .text(event.getBody().getText())
            .creationDate(event.getBody().getCreationDate())
            .build();
        response.setTitle("Title");

        when(ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(any(EcoNews.class), anyString()))
            .thenReturn(translation);
        when(ecoNewsRepo.findById(anyLong())).thenThrow(NotFoundException.class);

        eventListener.onApplicationEvent(event);
    }
}