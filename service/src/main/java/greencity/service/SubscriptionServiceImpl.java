package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.subscription.SubscriptionRequestDto;
import greencity.dto.subscription.SubscriptionResponseDto;
import greencity.entity.Subscription;
import greencity.enums.SubscriptionType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.SubscriptionRepo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepo subscriptionRepo;
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
}
