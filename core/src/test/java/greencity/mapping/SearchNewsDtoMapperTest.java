package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.entity.User;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SearchNewsDtoMapperTest {

    private EcoNews ecoNewsTest = ModelUtils.getEcoNews();

    private User user = ModelUtils.getUser();
    
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

        SearchNewsDtoMapper filter = new SearchNewsDtoMapper() {
            public SearchNewsDtoMapper callProtectedMethod(EcoNews ecoNews) {
                convert(ecoNews);
                return this;
            }
        }.callProtectedMethod(ecoNewsTest);

        assertEquals(filter.convert(ecoNewsTest), searchedNews);

    }
}


