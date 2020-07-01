package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.entity.User;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class SearchNewsDtoMapperTest {
    @InjectMocks
    private SearchNewsDtoMapper searchNewsDtoMapper;

    private EcoNews ecoNewsTest = ModelUtils.getEcoNews();

    private User user = ModelUtils.getUser();

    @BeforeEach
    void init() {
        searchNewsDtoMapper = new SearchNewsDtoMapper();
    }

    @Test
    public void convertTest() {

        SearchNewsDto searchedNews = SearchNewsDto.builder()
            .id(1L)
            .title(ecoNewsTest.getTitle())
            .author(new EcoNewsAuthorDto(user.getId(),
                user.getName()))
            .creationDate(ecoNewsTest.getCreationDate())
            .tags(ecoNewsTest.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList()))
            .build();

        assertEquals(searchedNews, searchNewsDtoMapper.convert(ecoNewsTest));
    }
}
