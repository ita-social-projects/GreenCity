package greencity.mapping;

import greencity.dto.search.SearchNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class SearchNewsDtoMapperTest {

    private EcoNews ecoNewsTest;

    private User user;

    @Before
    public void setUp() {

        user = User.builder()
            .id(1L)
            .email("test@gmail.com")
            .name("TestUserName")
            .role(ROLE.ROLE_USER)
            .dateOfRegistration(LocalDateTime.now())
            .lastVisit(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .refreshTokenKey("testToken")
            .build();

        ZonedDateTime dateTime = ZonedDateTime.now();
        Tag tag = new Tag(1L, "test", null);
        List<Tag> tags = new LinkedList<>();
        tags.add(tag);
        ecoNewsTest = new EcoNews(1L, dateTime, null, null, user, "TestTitle", "TestText", null, tags);
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

        SearchNewsDtoMapper filter = new SearchNewsDtoMapper() {
            public SearchNewsDtoMapper callProtectedMethod(EcoNews ecoNews) {
                convert(ecoNews);
                return this;
            }
        }.callProtectedMethod(ecoNewsTest);

        assertEquals(filter.convert(ecoNewsTest), searchedNews);

    }
}


