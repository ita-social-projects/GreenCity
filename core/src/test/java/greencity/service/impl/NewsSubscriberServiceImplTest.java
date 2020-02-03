package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.entity.NewsSubscriber;
import greencity.exception.exceptions.InvalidUnsubscribeToken;
import greencity.exception.exceptions.NewsSubscriberPresentException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.NewsSubscriberRepo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class NewsSubscriberServiceImplTest {
    @Mock
    NewsSubscriberRepo newsSubscriberRepo;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    NewsSubscriberServiceImpl newsSubscriberService;

    private UUID uuid;

    @Before
    public void setUp() {
        uuid = mock(UUID.class);
    }

    private final NewsSubscriberRequestDto dto = new NewsSubscriberRequestDto("test@mail.ua");
    private final NewsSubscriber entity = new NewsSubscriber(1L, "test@mail.ua", "token");

    @Test
    public void saveTest() {
        mockStatic(UUID.class);
        given(UUID.randomUUID()).willReturn(uuid);
        given(uuid.toString()).willReturn("token1");
        when(modelMapper.map(dto, NewsSubscriber.class)).thenReturn(entity);
        when(modelMapper.map(entity, NewsSubscriberRequestDto.class)).thenReturn(dto);
        when(newsSubscriberRepo.save(entity)).thenReturn(entity);
        assertEquals(dto, newsSubscriberService.save(dto));
    }

    @Test(expected = NewsSubscriberPresentException.class)
    public void notSavedNewsSubscriberAlreadyExistTest() {
        when(newsSubscriberRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(entity));
        assertEquals(dto, newsSubscriberService.save(dto));
    }

    @Test
    public void unsubscribeTest() {
        String email = "test.mail.ua";
        String token = "token";
        doNothing().when(newsSubscriberRepo).delete(any());
        when(newsSubscriberRepo.findByEmail(email)).thenReturn(Optional.of(entity));
        assertEquals(new Long(1), newsSubscriberService.unsubscribe(email, token));
    }

    @Test(expected = NewsSubscriberPresentException.class)
    public void notFoundSubscriberForUnsubscribeTest() {
        String email = "test.mail.ua";
        String token = "token";
        when(newsSubscriberRepo.findByEmail(email)).thenReturn(Optional.empty());
        assertEquals(new Long(1), newsSubscriberService.unsubscribe(email, token));
    }

    @Test(expected = InvalidUnsubscribeToken.class)
    public void notUnsubscribedInvalidTokenTest() {
        String email = "test.mail.ua";
        String token = "token1";
        when(newsSubscriberRepo.findByEmail(email)).thenReturn(Optional.of(entity));
        assertEquals(new Long(1), newsSubscriberService.unsubscribe(email, token));
    }

    @Test
    public void findAllTest() {
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
