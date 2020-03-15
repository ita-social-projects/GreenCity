package greencity.mapping;

import greencity.dto.econews.EcoNewsDto;
import greencity.dto.tag.TagDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.localization.EcoNewsTranslation;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

@RunWith(MockitoJUnitRunner.class)
public class EcoNewsDtoMapperTest {
    @Mock
    ModelMapper modelMapper;

    @Mock
    TagDtoMapper tagDtoMapper;

    @InjectMocks
    EcoNewsDtoMapper ecoNewsDtoMapper;

    private User author =
        User.builder()
            .id(1L)
            .email("Nazar.stasyuk@gmail.com")
            .firstName("Nazar")
            .lastName("Stasyuk")
            .role(ROLE.ROLE_USER)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();

    private EcoNewsAuthorDto ecoNewsAuthorDto = new EcoNewsAuthorDto(1L, "Nazar", "Stasyuk");

    private Tag tag = new Tag(1L, "tag", Collections.emptyList());

    private TagDto tagDto = new TagDto("tag");

    private Language language = new Language(1L, "en", Collections.emptyList(), Collections.emptyList(),
        Collections.emptyList());
    private EcoNews ecoNews = new EcoNews(1L, ZonedDateTime.now(), "imagePath", author,
        Collections.emptyList(), Collections.singletonList(tag));
    private EcoNewsTranslation ecoNewsTranslation = new EcoNewsTranslation(1L, language, "title", "text", ecoNews);


    @Test
    public void convertTest() {
        when(modelMapper.map(tag, TagDto.class)).thenReturn(tagDto);
        when(tagDtoMapper.convert(tag)).thenReturn(tagDto);

        EcoNewsDto expected = new EcoNewsDto(ecoNews.getCreationDate(), ecoNews.getImagePath(), 1L,
            ecoNewsTranslation.getTitle(), ecoNewsTranslation.getText(), ecoNewsAuthorDto,
            ecoNews.getTags().stream()
                .map(tag -> modelMapper.map(tag, TagDto.class))
                .collect(Collectors.toList())
        );

        assertEquals(expected, ecoNewsDtoMapper.convert(ecoNewsTranslation));
    }
}
