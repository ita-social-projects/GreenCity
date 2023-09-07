package greencity.mapping;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.enums.CommentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AddEcoNewsDtoRequestMapperTest {

    @InjectMocks
    private AddEcoNewsDtoRequestMapper mapper;

    private EcoNews ecoNews = ModelUtils.getEcoNews();

    @Test
    void convertTest() {
        AddEcoNewsDtoRequest request = ModelUtils.getAddEcoNewsDtoRequest();

        EcoNews actual = mapper.convert(request);
        actual.setImagePath(TestConst.SITE);
        actual.setId(1L);
        actual.setCreationDate(ecoNews.getCreationDate());
        actual.setTags(Collections.singletonList(ModelUtils.getTag()));
        actual.setUsersLikedNews(Collections.emptySet());
        actual.setUsersDislikedNews(Collections.emptySet());
        actual.setEcoNewsComments(
            List.of(EcoNewsComment.builder().id(1L).text("test").status(CommentStatus.ORIGINAL).build()));

        assertEquals(ecoNews, actual);
    }
}
