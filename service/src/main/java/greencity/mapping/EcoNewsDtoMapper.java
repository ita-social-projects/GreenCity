package greencity.mapping;

import greencity.constant.AppConstant;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import java.util.stream.Collectors;
import greencity.entity.localization.TagTranslation;
import greencity.enums.CommentStatus;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link EcoNews} into
 * {@link EcoNewsDto}.
 */
@Component
public class EcoNewsDtoMapper extends AbstractConverter<EcoNews, EcoNewsDto> {
    /**
     * Method for converting {@link EcoNews} into {@link EcoNewsDto}.
     *
     * @param ecoNews object ot convert.
     * @return converted object.
     */
    @Override
    public EcoNewsDto convert(EcoNews ecoNews) {
        return EcoNewsDto.builder()
            .author(EcoNewsAuthorDto.builder()
                .id(ecoNews.getAuthor().getId())
                .name(ecoNews.getAuthor().getName())
                .build())
            .id(ecoNews.getId())
            .content(ecoNews.getText())
            .creationDate(ecoNews.getCreationDate())
            .imagePath(ecoNews.getImagePath())
            .likes(ecoNews.getUsersLikedNews().size())
            .shortInfo(ecoNews.getShortInfo())
            .tags(ecoNews.getTags().stream()
                .flatMap(t -> t.getTagTranslations().stream())
                .filter(t -> t.getLanguage().getCode().equals(AppConstant.DEFAULT_LANGUAGE_CODE))
                .map(TagTranslation::getName).collect(Collectors.toList()))
            .tagsUa(ecoNews.getTags().stream()
                .flatMap(t -> t.getTagTranslations().stream())
                .filter(t -> t.getLanguage().getCode().equals("ua"))
                .map(TagTranslation::getName).collect(Collectors.toList()))
            .likes(ecoNews.getUsersLikedNews().size())
            .dislikes(ecoNews.getUsersDislikedNews().size())
            .title(ecoNews.getTitle())
            .countComments(
                (int) ecoNews.getEcoNewsComments().stream()
                    .filter(ecoNewsComment -> !ecoNewsComment.getStatus().equals(CommentStatus.DELETED)).count())
            .build();
    }
}
