package greencity.mapping;

import greencity.constant.AppConstant;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import greencity.service.CommentService;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link EcoNews} into
 * {@link EcoNewsDto}.
 */
@Component
public class EcoNewsDtoMapper extends AbstractConverter<EcoNews, EcoNewsDto> {
    private final CommentService commentService;

    @Autowired
    public EcoNewsDtoMapper(@Lazy CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Method for converting {@link EcoNews} into {@link EcoNewsDto}.
     *
     * @param ecoNews object ot convert.
     * @return converted object.
     */
    @Override
    public EcoNewsDto convert(EcoNews ecoNews) {
        User author = ecoNews.getAuthor();

        return EcoNewsDto.builder()
            .author(EcoNewsAuthorDto.builder()
                .id(author.getId())
                .name(author.getName())
                .build())
            .id(ecoNews.getId())
            .content(ecoNews.getText())
            .creationDate(ecoNews.getCreationDate())
            .imagePath(ecoNews.getImagePath())
            .likes(ecoNews.getUsersLikedNews().size())
            .shortInfo(ecoNews.getShortInfo())
            .tagsEn(ecoNews.getTags().stream()
                .flatMap(t -> t.getTagTranslations().stream())
                .filter(t -> t.getLanguageCode().equals(AppConstant.DEFAULT_LANGUAGE_CODE))
                .map(TagTranslation::getName).toList())
            .tagsUk(ecoNews.getTags().stream()
                .flatMap(t -> t.getTagTranslations().stream())
                .filter(t -> t.getLanguageCode().equals("ua"))
                .map(TagTranslation::getName).toList())
            .likes(ecoNews.getUsersLikedNews().size())
            .dislikes(ecoNews.getUsersDislikedNews().size())
            .title(ecoNews.getTitle())
            .countComments(commentService.countCommentsForEcoNews(ecoNews.getId()))
            .hidden(ecoNews.isHidden())
            .build();
    }
}
