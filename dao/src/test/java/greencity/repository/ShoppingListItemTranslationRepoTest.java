package greencity.repository;

import greencity.entity.localization.ShoppingListItemTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Sql("classpath:sql/shopping_list_item_translation.sql")
class ShoppingListItemTranslationRepoTest {

    @Autowired
    ShoppingListItemTranslationRepo shoppingListItemTranslationRepo;

    @Test
    void findAllByLanguageCodeTest() {
        List<ShoppingListItemTranslation> shoppingListItemTranslations =
            shoppingListItemTranslationRepo.findAllByLanguageCode("en");
        assertEquals(3, shoppingListItemTranslations.size());
        assertEquals("en", shoppingListItemTranslations.get(0).getLanguage().getCode());
    }

    @Test
    void findAvailableByUserIdTest() {
        List<ShoppingListItemTranslation> shoppingListItemTranslations =
            shoppingListItemTranslationRepo.findAvailableByUserId(1L, "ua");
        assertEquals(2, shoppingListItemTranslations.size());
        assertEquals("ua", shoppingListItemTranslations.get(0).getLanguage().getCode());
    }

    @Test
    void findAvailableByUserIdNotFoundTest() {
        List<ShoppingListItemTranslation> shoppingListItemTranslations =
            shoppingListItemTranslationRepo.findAvailableByUserId(1L, "xx");
        assertTrue(shoppingListItemTranslations.isEmpty());
    }

    @Test
    void findByUserIdLangAndUserGoalIdTest() {
        ShoppingListItemTranslation shoppingListItemTranslation = shoppingListItemTranslationRepo
            .findByLangAndUserShoppingListItemId("ru", 2L);
        assertEquals("ru", shoppingListItemTranslation.getLanguage().getCode());
        assertEquals(2L, shoppingListItemTranslation.getShoppingListItem().getId());
    }

    @Test
    void findByUserIdLangAndUserGoalIdNotFoundTest() {
        ShoppingListItemTranslation shoppingListItemTranslation = shoppingListItemTranslationRepo
            .findByLangAndUserShoppingListItemId("ru", 10L);
        assertNull(shoppingListItemTranslation);
    }

    @Test
    void findByUserIdLangAndUserGoalIdWithEmptyResultTest() {
        ShoppingListItemTranslation shoppingListItemTranslation = shoppingListItemTranslationRepo
            .findByLangAndUserShoppingListItemId("ru", 3L);
        assertNull(shoppingListItemTranslation);
    }
}
