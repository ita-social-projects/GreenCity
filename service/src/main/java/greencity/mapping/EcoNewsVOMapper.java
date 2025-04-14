package greencity.mapping;

import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.language.LanguageVO;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.User;
import greencity.exception.exceptions.WrongEmailException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EcoNewsVOMapper extends AbstractConverter<EcoNews, EcoNewsVO> {

    private final UserRemoteClient userRemoteClient;

    @Override
    protected EcoNewsVO convert(EcoNews ecoNews) {
        User author = ecoNews.getAuthor();

        String authorEmail = author.getEmail();
        UserVO authorVO = userRemoteClient.findNotDeactivatedByEmail(authorEmail)
                .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + authorEmail));

        return EcoNewsVO.builder()
            .id(ecoNews.getId())
            .author(UserVO.builder()
                .id(author.getId())
                .name(authorVO.getName())
                .email(author.getEmail())
                .userStatus(authorVO.getUserStatus())
                .role(authorVO.getRole())
                .build())
            .creationDate(ecoNews.getCreationDate())
            .imagePath(ecoNews.getImagePath())
            .source(ecoNews.getSource())
            .text(ecoNews.getText())
            .title(ecoNews.getTitle())
            .tags(ecoNews.getTags().stream()
                .map(tag -> TagVO.builder()
                    .id(tag.getId())
                    .tagTranslations(tag.getTagTranslations().stream()
                        .map(tagTranslation -> TagTranslationVO.builder()
                            .name(tagTranslation.getName())
                            .id(tagTranslation.getId())
                            .languageVO(LanguageVO.builder()
                                .code(tagTranslation.getLanguage().getCode())
                                .id(tagTranslation.getId())
                                .build())
                            .build())
                        .collect(Collectors.toList()))
                    .build())
                .collect(Collectors.toList()))
            .usersLikedNews(ecoNews.getUsersLikedNews().stream()
                .map(user -> UserVO.builder()
                    .id(user.getId())
                    .build())
                .collect(Collectors.toSet()))
            .usersDislikedNews(ecoNews.getUsersDislikedNews().stream()
                .map(user -> UserVO.builder()
                    .id(user.getId())
                    .build())
                .collect(Collectors.toSet()))
            .build();
    }
}
