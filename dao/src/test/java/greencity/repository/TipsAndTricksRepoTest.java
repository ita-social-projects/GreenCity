package greencity.repository;

import greencity.entity.EcoNews;
import greencity.entity.TipsAndTricks;
import greencity.entity.TitleTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Sql("classpath:sql/tips_and_tricks.sql")
class TipsAndTricksRepoTest {
    @Autowired
    TipsAndTricksRepo tipsAndTricksRepo;

    @Test
    void findTest() {
        Pageable pageable = PageRequest.of(0, 6);
        List<String> tags = Arrays.asList("news", "education");
        Page<TipsAndTricks> page = tipsAndTricksRepo.find(pageable, tags);
        List<TipsAndTricks> tipsAndTricks = page.get().collect(Collectors.toList());
        assertEquals(4, tipsAndTricks.size());
        assertEquals(2, tipsAndTricks.get(1).getId());
    }

    @Test
    void findAllByUserIdTest() {
        List<TipsAndTricks> tipsAndTricksList = tipsAndTricksRepo.findAllByUserId(1L);
        assertEquals(4L, tipsAndTricksList.size());
        assertEquals(2L, tipsAndTricksList.get(0).getId());
    }

    @Test
    void findAllByOrderByCreationDateDescTest() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<TipsAndTricks> page = tipsAndTricksRepo.findAllByOrderByCreationDateDesc(pageable);
        List<TipsAndTricks> tipsAndTricks = page.get().collect(Collectors.toList());
        assertEquals(6, tipsAndTricks.size());
        assertEquals(1, tipsAndTricks.get(5).getId());
    }

    @Test
    void getAmountOfWrittenTipsAndTrickByUserIdTest() {
        Long amount = tipsAndTricksRepo.getAmountOfWrittenTipsAndTrickByUserId(1L);
        assertEquals(4, amount);
    }

}