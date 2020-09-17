package greencity.service.impl;

import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.entity.NewsSubscriber;
import greencity.exception.exceptions.InvalidUnsubscribeToken;
import greencity.exception.exceptions.NewsSubscriberPresentException;
import greencity.repository.NewsSubscriberRepo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mockit.MockUp;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import static org.powermock.api.mockito.PowerMockito.doNothing;

@ExtendWith(MockitoExtension.class)
class NewsSubscriberServiceImplTest {
    @Mock
    NewsSubscriberRepo newsSubscriberRepo;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    NewsSubscriberServiceImpl newsSubscriberService;

    private final NewsSubscriberRequestDto dto = new NewsSubscriberRequestDto("test@mail.ua");
    private final NewsSubscriber entity = new NewsSubscriber(1L, "test@mail.ua", "token");

    @Test
    void saveTest() {
        new MockUp<UUID>() {
            @mockit.Mock
            UUID randomUUID() {
                return UUID.fromString("e1fd146e-fdb1-4b00-92a5-cc5d1585f899");
            }
        };
        when(modelMapper.map(dto, NewsSubscriber.class)).thenReturn(entity);
        when(modelMapper.map(entity, NewsSubscriberRequestDto.class)).thenReturn(dto);
        when(newsSubscriberRepo.save(entity)).thenReturn(entity);
        assertEquals(dto, newsSubscriberService.save(dto));
    }

    @Test
    void notSavedNewsSubscriberAlreadyExistTest() {
        when(newsSubscriberRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(entity));
        Assertions
            .assertThrows(NewsSubscriberPresentException.class,
                () -> newsSubscriberService.save(dto));

    }

    @Test
    void unsubscribeTest() {
        String email = "test.mail.ua";
        String token = "token";
        doNothing().when(newsSubscriberRepo).delete(any());
        when(newsSubscriberRepo.findByEmail(email)).thenReturn(Optional.of(entity));
        assertEquals(new Long(1), newsSubscriberService.unsubscribe(email, token));
    }

    @Test
    void notFoundSubscriberForUnsubscribeTest() {
        String email = "test.mail.ua";
        String token = "token";
        when(newsSubscriberRepo.findByEmail(email)).thenReturn(Optional.empty());
        Assertions
            .assertThrows(NewsSubscriberPresentException.class,
                () -> newsSubscriberService.unsubscribe(email, token));
    }

    @Test
    void notUnsubscribedInvalidTokenTest() {
        String email = "test.mail.ua";
        String token = "token1";
        when(newsSubscriberRepo.findByEmail(email)).thenReturn(Optional.of(entity));
        Assertions
            .assertThrows(InvalidUnsubscribeToken.class,
                () -> newsSubscriberService.unsubscribe(email, token));
    }

    @Test
    void findAllTest() {
        NewsSubscriber entity = new NewsSubscriber(1L, "test@mail.ua", "token");
        List<NewsSubscriber> entityList = Collections.singletonList(entity);
        List<NewsSubscriberResponseDto> dtoList = Collections.singletonList(
            new NewsSubscriberResponseDto("test@mail.ua", "token"));
        when(newsSubscriberRepo.findAll()).thenReturn(entityList);
        when(modelMapper.map(entity, NewsSubscriberResponseDto.class))
            .thenReturn(new NewsSubscriberResponseDto("test@mail.ua", "token"));
        assertEquals(dtoList, newsSubscriberService.findAll());
    }
}
