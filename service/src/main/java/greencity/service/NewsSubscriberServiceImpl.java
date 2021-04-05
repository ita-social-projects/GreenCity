package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.entity.NewsSubscriber;
import greencity.exception.exceptions.*;
import greencity.repository.NewsSubscriberRepo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class NewsSubscriberServiceImpl implements NewsSubscriberService {
    private final NewsSubscriberRepo newsSubscriberRepo;
    private final ModelMapper modelMapper;

    /**
     * Constructor with parameters.
     */
    @Autowired
    public NewsSubscriberServiceImpl(NewsSubscriberRepo newsSubscriberRepo, ModelMapper modelMapper) {
        this.newsSubscriberRepo = newsSubscriberRepo;
        this.modelMapper = modelMapper;
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public NewsSubscriberRequestDto save(NewsSubscriberRequestDto dto) {
        if (findByEmail(dto.getEmail()).isPresent()) {
            log.error(ErrorMessage.NEWS_SUBSCRIBER_EXIST);
            throw new NewsSubscriberPresentException(ErrorMessage.NEWS_SUBSCRIBER_EXIST);
        }
        NewsSubscriber entity = modelMapper.map(dto, NewsSubscriber.class);
        entity.setUnsubscribeToken(UUID.randomUUID().toString());
        return modelMapper.map(newsSubscriberRepo.save(entity), NewsSubscriberRequestDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    public Long unsubscribe(String email, String unsubscribeToken) {
        NewsSubscriber newsSubscriber = findByEmail(email)
            .orElseThrow(
                () -> new NotFoundException(ErrorMessage.NEWS_SUBSCRIBER_BY_EMAIL_NOT_FOUND + email));
        if (newsSubscriber.getUnsubscribeToken().equals(unsubscribeToken)) {
            newsSubscriberRepo.delete(newsSubscriber);
        } else {
            throw new InvalidUnsubscribeToken(ErrorMessage.INVALID_UNSUBSCRIBE_TOKEN);
        }
        return newsSubscriber.getId();
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Override
    public List<NewsSubscriberResponseDto> findAll() {
        return newsSubscriberRepo.findAll().stream()
            .map(el -> modelMapper.map(el, NewsSubscriberResponseDto.class))
            .collect(Collectors.toList());
    }

    /**
     * Method for finding one NewsSubscriber by email.
     *
     * @param email String email.
     * @return optional of {@link NewsSubscriber}
     */
    private Optional<NewsSubscriber> findByEmail(String email) {
        return newsSubscriberRepo.findByEmail(email);
    }
}
