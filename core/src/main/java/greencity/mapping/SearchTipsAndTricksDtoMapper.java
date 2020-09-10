package greencity.mapping;

import greencity.dto.search.SearchTipsAndTricksDto;
import greencity.dto.user.AuthorDto;
import greencity.entity.Tag;
import greencity.entity.TipsAndTricks;
import greencity.entity.User;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class SearchTipsAndTricksDtoMapper extends AbstractConverter<TipsAndTricks, SearchTipsAndTricksDto> {
    @Override
    protected SearchTipsAndTricksDto convert(TipsAndTricks tipsAndTricks) {
        User author = tipsAndTricks.getAuthor();

        return SearchTipsAndTricksDto.builder()
            .id(tipsAndTricks.getId())
            .title(tipsAndTricks.getTitle())
            .author(new AuthorDto(author.getId(),
                author.getName()))
            .creationDate(tipsAndTricks.getCreationDate())
            .tags(tipsAndTricks.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList()))
            .build();
    }
}
