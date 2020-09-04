package greencity.service.impl;

import greencity.entity.SocialNetwork;
import greencity.entity.User;
import greencity.repository.SocialNetworkRepo;
import greencity.service.SocialNetworkImageService;
import greencity.service.SocialNetworkService;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocialNetworkServiceImpl implements SocialNetworkService {
    @Autowired
    SocialNetworkRepo socialNetworkRepo;
    @Autowired
    SocialNetworkImageService socialNetworkImageService;

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
        socialNetworkRepo.deleteAllByUserIs(user);
        socialNetworkRepo.saveAll(listSocialNetwork);
        return listSocialNetwork;
    }
}
