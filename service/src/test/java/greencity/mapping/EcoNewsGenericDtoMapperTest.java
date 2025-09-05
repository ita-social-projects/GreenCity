package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import greencity.ModelUtils;
import greencity.dto.econews.EcoNewsGenericDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.repository.EcoNewsRepo;
import greencity.service.CommentService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EcoNewsGenericDtoMapperTest {
    @Mock
    EcoNewsRepo ecoNewsRepo;

    @Mock
    CommentService commentService;

    @InjectMocks
    EcoNewsGenericDtoMapper mapper;

    EcoNews ecoNews;
    User author;
    Tag tag;

    @BeforeEach
    void setUp() {
        author = ModelUtils.getUser();
        tag = ModelUtils.getTag();
        ecoNews = ModelUtils.getEcoNews();
    }

    @Test
    void convertTest() {
        int expectedCommentCount = 5;
        int expectedEcoNewsCount = 10;
        when(commentService.countCommentsForEcoNews(anyLong()))
            .thenReturn(expectedCommentCount);
        when(ecoNewsRepo.totalCountOfCreationNews()).thenReturn(expectedEcoNewsCount);

        EcoNewsGenericDto dto = mapper.convert(ecoNews);

        assertNotNull(dto);
        assertEquals(ecoNews.getId(), dto.getId());
        assertEquals(ecoNews.getImagePath(), dto.getImagePath());
        assertEquals(ecoNews.getShortInfo(), dto.getShortInfo());
        assertEquals(ecoNews.getText(), dto.getContent());
        assertEquals(ecoNews.getTitle(), dto.getTitle());
        assertEquals(ecoNews.getCreationDate(), dto.getCreationDate());
        assertEquals(ecoNews.getSource(), dto.getSource());
        assertEquals(ecoNews.getUsersLikedNews() != null ? ecoNews.getUsersLikedNews().size() : 0, dto.getLikes());
        assertEquals(expectedCommentCount, dto.getCountComments());
        assertEquals(expectedEcoNewsCount, dto.getCountOfEcoNews());

        EcoNewsAuthorDto authorDto = dto.getAuthor();
        assertNotNull(authorDto);
        assertEquals(ecoNews.getAuthor().getId(), authorDto.getId());
        assertEquals(ecoNews.getAuthor().getName(), authorDto.getName());

        List<String> tagsEn = dto.getTagsEn();
        List<String> tagsUk = dto.getTagsUk();
        assertTrue(tagsEn.contains("News"));
        assertTrue(tagsUk.contains("Новини"));
        assertFalse(tagsEn.contains("Новини"));
        assertFalse(tagsUk.contains("News"));
    }

    @Test
    void convertWithNullUsersLikedNewsTest() {
        ecoNews.setUsersLikedNews(null);
        when(commentService.countCommentsForEcoNews(anyLong())).thenReturn(0);
        when(ecoNewsRepo.totalCountOfCreationNews()).thenReturn(0);

        EcoNewsGenericDto dto = mapper.convert(ecoNews);

        assertEquals(0, dto.getLikes());
    }
}