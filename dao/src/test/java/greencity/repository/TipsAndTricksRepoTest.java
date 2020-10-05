package greencity.repository;

import greencity.entity.TipsAndTricks;
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
        List<String> tags = Arrays.asList("news", "events");
        Page<TipsAndTricks> page = tipsAndTricksRepo.find(pageable, tags);
        List<TipsAndTricks> tipsAndTricks = page.get().collect(Collectors.toList());
        assertEquals(4, tipsAndTricks.size());
        assertEquals(1, tipsAndTricks.get(3).getId());
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
    void searchTipsAndTricksTest() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<TipsAndTricks> page = tipsAndTricksRepo.searchTipsAndTricks(pageable, "2");
        List<TipsAndTricks> tipsAndTricks = page.get().collect(Collectors.toList());
        assertEquals("TestTitle2", tipsAndTricks.get(0).getTitle());
        assertEquals("Text3", tipsAndTricks.get(0).getText());
        assertEquals("TestTitle3", tipsAndTricks.get(1).getTitle());
        assertEquals("Text2", tipsAndTricks.get(1).getText());
    }

    @Test
    void searchByTest() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<TipsAndTricks> page = tipsAndTricksRepo.searchBy(pageable, "John");
        List<TipsAndTricks> tipsAndTricks = page.get().collect(Collectors.toList());
        assertEquals(5, tipsAndTricks.get(0).getId());
    }

    @Test
    void getAmountOfWrittenTipsAndTrickByUserIdTest() {
        Long amount = tipsAndTricksRepo.getAmountOfWrittenTipsAndTrickByUserId(1L);
        assertEquals(5, amount);
    }

}