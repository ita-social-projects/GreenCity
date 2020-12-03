package greencity.repository;

import greencity.DaoApplication;
import greencity.entity.EcoNews;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoApplication.class)
@Sql("classpath:sql/eco_news.sql")
class EcoNewsRepoTest {
    @Autowired
    private EcoNewsRepo ecoNewsRepo;

    @Test
    void getThreeLastEcoNewsTest() {
        List<Long> ecoNewsId = ecoNewsRepo
            .getThreeLastEcoNews()
            .stream()
            .map(EcoNews::getId)
            .collect(Collectors.toList());

        assertEquals(11, ecoNewsId.get(0));
        assertEquals(10, ecoNewsId.get(1));
        assertEquals(9, ecoNewsId.get(2));
    }

    @Test
    void findByTagsTest() {
        PageRequest pageRequest = PageRequest.of(0, 3);
        List<String> tagsList = Collections.singletonList("ads");
        Page<EcoNews> ecoNewsPage = ecoNewsRepo.findByTags(pageRequest, tagsList);

        Long firstActual = ecoNewsPage.getContent().get(0).getId();
        Long secondActual = ecoNewsPage.getContent().get(1).getId();
        Long thirdActual = ecoNewsPage.getContent().get(2).getId();

        assertEquals(4L, firstActual);
        assertEquals(2L, secondActual);
        assertEquals(1L, thirdActual);
    }

    @Test
    void findAllByOrderByCreationDateDescTest() {
        PageRequest pageRequest = PageRequest.of(0, 11);
        Page<EcoNews> ecoNewsPage = ecoNewsRepo.findAllByOrderByCreationDateDesc(pageRequest);

        Long actualSize = ecoNewsPage.getTotalElements();
        Long actualId = ecoNewsPage.stream().map(EcoNews::getId).findFirst().orElse(0L);

        assertEquals(11L, actualSize);
        assertEquals(11L, actualId);
    }

    @Test
    void getAmountOfPublishedNewsByUserIdTest() {
        Long actual = ecoNewsRepo.getAmountOfPublishedNewsByUserId(1L);
        assertEquals(11L, actual);
    }
}