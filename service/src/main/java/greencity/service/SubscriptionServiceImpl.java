package greencity.service;

import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.econews.InterestingEcoNewsDto;
import greencity.dto.econews.ShortEcoNewsDto;
import greencity.dto.subscription.SubscriptionRequestDto;
import greencity.dto.subscription.SubscriptionResponseDto;
import greencity.dto.user.SubscriberDto;
import greencity.entity.Subscription;
import greencity.enums.SubscriptionType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.SubscriptionRepo;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepo subscriptionRepo;
    private final EcoNewsService ecoNewsService;
    private final UserService userService;
    private final RestClient restClient;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public SubscriptionResponseDto createSubscription(SubscriptionRequestDto subscriptionRequestDto) {
        checkSubscriptionDoesNotExist(subscriptionRequestDto.getEmail(), subscriptionRequestDto.getSubscriptionType());

        Subscription subscription = modelMapper.map(subscriptionRequestDto, Subscription.class);
        return modelMapper.map(subscriptionRepo.save(subscription), SubscriptionResponseDto.class);
    }

    private void checkSubscriptionDoesNotExist(String email, SubscriptionType subscriptionType) {
        if (subscriptionRepo.existsByEmailAndSubscriptionType(email, subscriptionType)) {
            throw new BadRequestException(ErrorMessage.SUBSCRIPTION_EXIST);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSubscription(UUID unsubscribeToken) {
        if (subscriptionRepo.existsByUnsubscribeToken(unsubscribeToken)) {
            subscriptionRepo.deleteByUnsubscribeToken(unsubscribeToken);
        } else {
            throw new NotFoundException(ErrorMessage.UBSCRIPTION_BY_TOKEN_NOT_FOUND);
        }
    }

    /**
     * Method that send content (only eco-news for now) to subscribers.
     */
    @Scheduled(cron = "${cron.sendContentToSubscribers}", zone = AppConstant.UKRAINE_TIMEZONE)
    public void sendContentToSubscribers() {
        List<ShortEcoNewsDto> interestingEcoNews = getInterestingEcoNews();
        if (interestingEcoNews.isEmpty()) {
            return;
        }

        int page = 0;
        Page<Subscription> subscriptions;
        do {
            subscriptions = subscriptionRepo.findAll(PageRequest.of(page, 20));
            if (!subscriptions.isEmpty()) {
                List<SubscriberDto> subscribers = getSubscribers(subscriptions);
                restClient.sendInterestingEcoNews(new InterestingEcoNewsDto(interestingEcoNews, subscribers));
            }
            page++;
        } while (subscriptions.hasNext());
    }

    private List<ShortEcoNewsDto> getInterestingEcoNews() {
        return ecoNewsService.getThreeInterestingEcoNews().stream()
            .map(e -> modelMapper.map(e, ShortEcoNewsDto.class))
            .map(e -> e.setText(cutText(e.getText())))
            .toList();
    }

    private String cutText(String htmlText) {
        String text = htmlText
            .replaceAll("<[^>]*>", "")
            .replaceAll("&[a-zA-Z]+;", " ");

        int maxLength = 300;
        if (text.length() > maxLength) {
            int lastSpaceIndex = text.lastIndexOf(' ', maxLength);
            if (lastSpaceIndex > 0) {
                text = text.substring(0, lastSpaceIndex);
            } else {
                text = text.substring(0, maxLength);
            }
            text += "...";
        }
        return text;
    }

    private List<SubscriberDto> getSubscribers(Page<Subscription> subscriptions) {
        List<String> emails = subscriptions.stream()
            .map(Subscription::getEmail)
            .toList();

        return userService.findByEmails(emails).stream()
            .map(u -> modelMapper.map(u, SubscriberDto.class))
            .map(s -> s.setUnsubscribeToken(findUnsubscribeToken(s.getEmail(), subscriptions)))
            .toList();
    }

    private UUID findUnsubscribeToken(String email, Page<Subscription> subscriptions) {
        return subscriptions.stream()
            .filter(s -> s.getEmail().equals(email))
            .map(Subscription::getUnsubscribeToken)
            .findFirst()
            .orElseThrow();
    }
}
