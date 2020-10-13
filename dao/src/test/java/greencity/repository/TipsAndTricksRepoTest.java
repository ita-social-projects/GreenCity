package greencity.repository;

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
        List<String> tags = Arrays.asList("news", "events");
        Page<TipsAndTricks> page = tipsAndTricksRepo.find("en", pageable, tags);
        List<TipsAndTricks> tipsAndTricks = page.get().collect(Collectors.toList());
        assertEquals(2, tipsAndTricks.size());
        assertEquals(1, tipsAndTricks.get(1).getId());
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
        Page<TipsAndTricks> page = tipsAndTricksRepo.searchTipsAndTricks(pageable, "News");
        List<TipsAndTricks> tipsAndTricks = page.get().collect(Collectors.toList());
        assertEquals(2, tipsAndTricks.size());
        assertEquals("News", tipsAndTricks.get(0).getTags().get(0).getName());
    }

    @Test
    void searchByAuthorNameTest() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<TipsAndTricks> page = tipsAndTricksRepo.searchBy(pageable, "John", "en");
        List<TipsAndTricks> tipsAndTricks = page.get().collect(Collectors.toList());
        assertEquals(1, tipsAndTricks.size());
        assertEquals("John", tipsAndTricks.get(0).getAuthor().getName());
    }

    @Test
    void searchByTitleTest() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<TipsAndTricks> page = tipsAndTricksRepo.searchBy(pageable, "TitleTest", "en");
        List<TipsAndTricks> tipsAndTricks = page.get().collect(Collectors.toList());
        assertEquals(1, tipsAndTricks.size());
        assertEquals(Optional.of("TitleTest"),
                tipsAndTricks.get(0).getTitleTranslations().stream()
                        .filter(elem -> elem.getLanguage().getCode().equals("en"))
                        .findFirst().map(TitleTranslation::getContent));
    }

    @Test
    void getAmountOfWrittenTipsAndTrickByUserIdTest() {
        Long amount = tipsAndTricksRepo.getAmountOfWrittenTipsAndTrickByUserId(1L);
        assertEquals(4, amount);
    }

}