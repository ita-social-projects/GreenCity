package greencity.mapping;

import greencity.constant.AppConstant;
import greencity.dto.econews.EcoNewsGroupedTagsDto;
import greencity.dto.tag.TagUkEnNamesDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
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
 * {@link EcoNewsGroupedTagsDto}.
 */
@Component
public class EcoNewsGroupedTagsDtoMapper extends AbstractConverter<EcoNews, EcoNewsGroupedTagsDto> {
    private final CommentService commentService;

    @Autowired
    public EcoNewsGroupedTagsDtoMapper(@Lazy CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Method for converting {@link EcoNews} into {@link EcoNewsGroupedTagsDto}.
     *
     * @param ecoNews object ot convert.
     * @return converted object.
     */
    @Override
    protected EcoNewsGroupedTagsDto convert(EcoNews ecoNews) {
        if (ecoNews == null) {
            throw new NullPointerException("EcoNews cannot be null");
        }
        User author = ecoNews.getAuthor();

        return EcoNewsGroupedTagsDto.builder()
            .author(EcoNewsAuthorDto.builder()
                .id(author.getId())
                .name(author.getName())
                .build())
            .id(ecoNews.getId())
            .content(ecoNews.getText())
            .creationDate(ecoNews.getCreationDate())
            .imagePath(ecoNews.getImagePath())
            .shortInfo(ecoNews.getShortInfo())
            .tags(ecoNews.getTags().stream()
                .map(this::mapToTagUkEnNamesDto)
                .toList())
            .likes(ecoNews.getUsersLikedNews().size())
            .dislikes(ecoNews.getUsersDislikedNews().size())
            .title(ecoNews.getTitle())
            .countComments(commentService.countCommentsForEcoNews(ecoNews.getId()))
            .hidden(ecoNews.isHidden())
            .build();
    }

    /**
     * Method for converting {@link Tag} into {@link TagUkEnNamesDto}. Extracts
     * English and Ukrainian names from tag translations and maps them to a DTO.
     *
     * @param tag the tag entity to convert.
     * @return a {@link TagUkEnNamesDto} containing Ukrainian and English tag names.
     */
    private TagUkEnNamesDto mapToTagUkEnNamesDto(Tag tag) {
        String nameEn = tag.getTagTranslations().stream()
            .filter(t -> t.getLanguageCode().equals(AppConstant.DEFAULT_LANGUAGE_CODE))
            .map(TagTranslation::getName)
            .findFirst().orElse("");

        String nameUk = tag.getTagTranslations().stream()
            .filter(t -> t.getLanguageCode().equals(AppConstant.LANGUAGE_CODE_UA))
            .map(TagTranslation::getName)
            .findFirst().orElse("");

        return TagUkEnNamesDto.builder().nameUk(nameUk).nameEn(nameEn).build();
    }
}
