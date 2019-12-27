package greencity.service;

import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
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
     * @param email email for deleting.
     * @return long id of deleted {@link NewsSubscriber}
     */
    Long delete(String email);

    /**
     * Method for finding all newsSubscriber.
     *
     * @return list of {@link NewsSubscriberRequestDto}
     */
    List<NewsSubscriberRequestDto> findAll();
}
