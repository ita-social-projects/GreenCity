package greencity.mapping;

import greencity.dto.econews.EcoNewsGenericDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import greencity.repository.EcoNewsRepo;
import greencity.service.CommentService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EcoNewsGenericDtoMapper extends AbstractConverter<EcoNews, EcoNewsGenericDto> {
    private final EcoNewsRepo ecoNewsRepo;
    private final CommentService commentService;

    @Override
    public EcoNewsGenericDto convert(EcoNews ecoNews) {
        List<String> tags = ecoNews.getTags().stream()
            .flatMap(t -> t.getTagTranslations().stream())
            .map(TagTranslation::getName)
            .toList();

        return buildEcoNewsGenericDto(ecoNews, tags);
    }

    private EcoNewsGenericDto buildEcoNewsGenericDto(EcoNews ecoNews, List<String> tags) {
        User author = ecoNews.getAuthor();
        EcoNewsAuthorDto ecoNewsAuthorDto = new EcoNewsAuthorDto(author.getId(), author.getName());

        int countOfComments = commentService.countCommentsForEcoNews(ecoNews.getId());
        int countOfEcoNews = ecoNewsRepo.totalCountOfCreationNews();

        return EcoNewsGenericDto.builder()
            .id(ecoNews.getId())
            .imagePath(ecoNews.getImagePath())
            .author(ecoNewsAuthorDto)
            .tagsEn(tags.stream()
                .filter(tag -> tag.matches("^([A-Za-z-])+$"))
                .toList())
            .tagsUk(tags.stream()
                .filter(tag -> tag.matches("^([А-Яа-яієїґ'-])+$"))
                .toList())
            .shortInfo(ecoNews.getShortInfo())
            .content(ecoNews.getText())
            .title(ecoNews.getTitle())
            .creationDate(ecoNews.getCreationDate())
            .source(ecoNews.getSource())
            .likes(ecoNews.getUsersLikedNews() != null ? ecoNews.getUsersLikedNews().size() : 0)
            .countComments(countOfComments)
            .countOfEcoNews(countOfEcoNews)
            .build();
    }
}
