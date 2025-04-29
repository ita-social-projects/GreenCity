package greencity.client;

import greencity.dto.user.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRemoteWebClient {

    private final WebClient webClient;

    private static final String PAGE_QUERY_PARAM = "page";
    private static final String PAGE_SIZE_QUERY_PARAM = "size";
    private static final String USER_EMAIL_QUERY_PARAM = "email";


    /**
     * Finds {@link UserVO} that is not 'DEACTIVATED' by {@link UserVO}'s Email.
     *
     * @param email {@link UserVO}'s Email.
     * @return {@link Optional} of {@link UserVO}.
     */
    public Optional<UserVO> findNotDeactivatedByEmail(String email) {
        String path = "/user/findNotDeactivatedByEmail";
        UserVO userVO = webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(path)
                                .queryParam(USER_EMAIL_QUERY_PARAM, email)
                                .build()
                )
                .retrieve()
                .bodyToMono(UserVO.class)
                .block();
        return Optional.of(userVO);
    }
}
