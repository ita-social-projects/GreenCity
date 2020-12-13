package greencity.repository;

import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Sql("classpath:sql/tags.sql")
class TagTranslationRepoTest {
    @Autowired
    private TagTranslationRepo tagTranslationRepo;

    @Test
    void bulkDeleteByTagId() {
        List<Long> ids = Collections.singletonList(1L);

        tagTranslationRepo.bulkDeleteByTagId(ids);
        List<TagTranslation> remainingTags = tagTranslationRepo.findAll();

        assertEquals(6, remainingTags.size());
    }
}