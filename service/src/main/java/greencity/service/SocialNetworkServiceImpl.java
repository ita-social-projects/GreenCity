package greencity.service;

import greencity.repository.SocialNetworkRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialNetworkServiceImpl implements SocialNetworkService {
    public final SocialNetworkRepo socialNetworkRepo;

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public Long delete(Long userId) {
        socialNetworkRepo.deleteById(userId);
        return userId;
    }
}
