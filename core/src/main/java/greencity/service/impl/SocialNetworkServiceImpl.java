package greencity.service.impl;

import greencity.entity.SocialNetwork;
import greencity.entity.User;
import greencity.repository.SocialNetworkRepo;
import greencity.service.SocialNetworkImageService;
import greencity.service.SocialNetworkService;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SocialNetworkServiceImpl implements SocialNetworkService {
    SocialNetworkRepo socialNetworkRepo;
    SocialNetworkImageService socialNetworkImageService;

    /**
     * Method saves all {@link SocialNetwork}.
     *
     * @param socialNetworkUrls socialnetwork URLs of user
     * @param user              current user
     * @return list of {@link SocialNetwork}
     */
    @Override
    @Transactional
    public List<SocialNetwork> saveAll(List<String> socialNetworkUrls, User user) {
        List<SocialNetwork> listSocialNetwork = socialNetworkUrls.stream()
            .map(url ->
                SocialNetwork.builder()
                    .url(url)
                    .user(user)
                    .socialNetworkImage(socialNetworkImageService.getSocialNetworkImageByUrl(url))
                    .build()
            )
            .collect(Collectors.toList());
        user.getSocialNetworks().clear();
        user.getSocialNetworks().addAll(listSocialNetwork);
        return listSocialNetwork;
    }
}
