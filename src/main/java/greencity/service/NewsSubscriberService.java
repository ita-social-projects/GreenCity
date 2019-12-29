package greencity.service;

import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.entity.NewsSubscriber;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Provides the interface to manage {@code NewsSubscriber} entity.
 *
 * @author Bogdan Kuzenko
 */
@Service
public interface NewsSubscriberService {
    /**
     * Method saves news subscriber in database.
     *
     * @param dto {@link NewsSubscriberRequestDto} object for saving.
     * @return saved {@link NewsSubscriberRequestDto}
     */
    NewsSubscriberRequestDto save(NewsSubscriberRequestDto dto);

    /**
     * Method for deleted newsSubscriber.
     *
     * @param email subscriber email for deleting.
     * @param unsubscribeToken unsubscribe token for subscriber verification.
     * @return long id of deleted {@link NewsSubscriber}
     */
    Long unsubscribe(String email, String unsubscribeToken);

    /**
     * Method for finding all newsSubscriber.
     *
     * @return list of {@link NewsSubscriberResponseDto}
     */
    List<NewsSubscriberResponseDto> findAll();
}
