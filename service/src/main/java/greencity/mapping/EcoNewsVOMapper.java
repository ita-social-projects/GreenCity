package greencity.mapping;

import greencity.dto.econews.EcoNewsVO;
import greencity.dto.language.LanguageDTO;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class EcoNewsVOMapper extends AbstractConverter<EcoNews, EcoNewsVO> {
    private final ModelMapper modelMapper;

    @Lazy
    public EcoNewsVOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    protected EcoNewsVO convert(EcoNews ecoNews) {
        User author = ecoNews.getAuthor();
        UserVO authorVO = modelMapper.map(author, UserVO.class);

        return EcoNewsVO.builder()
            .id(ecoNews.getId())
            .author(UserVO.builder()
                .id(author.getId())
                .name(author.getName())
                .email(authorVO.getEmail())
                .status(authorVO.getStatus())
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
                            .languageVO(LanguageDTO.builder()
                                .code(tagTranslation.getLanguageCode())
                                .id(tagTranslation.getId())
                                .build())
                            .build())
                        .toList())
                    .build())
                .toList())
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
