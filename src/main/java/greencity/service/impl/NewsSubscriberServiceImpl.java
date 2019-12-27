package greencity.service.impl;

import static greencity.constant.ErrorMessage.*;

import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.entity.NewsSubscriber;
import greencity.exception.exceptions.NewsSubscriberPresentException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.NewsSubscriberRepo;
import greencity.service.NewsSubscriberService;
import java.util.List;
import java.util.Optional;
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
            log.error(NEWS_SUBSCRIBER_EXIST);
            throw new NewsSubscriberPresentException(NEWS_SUBSCRIBER_EXIST);
        }
        NewsSubscriber newsSubscriber = Optional.of(newsSubscriberRepo
            .save(modelMapper.map(dto, NewsSubscriber.class)))
            .orElseThrow(() -> new NotSavedException(NEWS_SUBSCRIBER_NOT_SAVED));
        return modelMapper.map(newsSubscriber, NewsSubscriberRequestDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Override
    public Long delete(NewsSubscriberRequestDto dto) {
        NewsSubscriber newsSubscriber = findByEmail(dto.getEmail())
            .orElseThrow(() -> new NewsSubscriberPresentException(NEWS_SUBSCRIBER_BY_EMAIL_NOT_FOUND));
        newsSubscriberRepo.delete(newsSubscriber);
        if (findByEmail(dto.getEmail()).isPresent()) {
            log.error(NEWS_SUBSCRIBER_NOT_DELETED);
            throw new NotDeletedException(NEWS_SUBSCRIBER_NOT_DELETED);
        }
        return newsSubscriber.getId();
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Override
    public List<NewsSubscriberRequestDto> findAll() {
        List<NewsSubscriber> allSubscribers = newsSubscriberRepo.findAll();
        if (allSubscribers.isEmpty()) {
            log.error(NEWS_SUBSCRIBERS_NOT_FOUND);
            throw new NotFoundException(NEWS_SUBSCRIBERS_NOT_FOUND);
        }
        return allSubscribers.stream()
            .map(el -> modelMapper.map(el, NewsSubscriberRequestDto.class))
            .collect(Collectors.toList());
    }

    /**
     * Method for finding one NewsSubscriber by email.
     *
     * @param email email id.
     * @return optional of {@link NewsSubscriber}
     */
    private Optional<NewsSubscriber> findByEmail(String email) {
        return newsSubscriberRepo.findByEmail(email);
    }
}
