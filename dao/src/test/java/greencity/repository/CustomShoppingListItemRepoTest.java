package greencity.repository;

import greencity.DaoApplication;
import greencity.entity.CustomShoppingListItem;

import java.time.LocalDateTime;
import java.util.List;

import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoApplication.class)
@Sql("classpath:sql/custom_shopping_list_item.sql")
class CustomShoppingListItemRepoTest {
    @Autowired
    private CustomShoppingListItemRepo customShoppingListItemRepo;

    @Test
    void findAllAvailableCustomShoppingListItemTest() {
        List<CustomShoppingListItem> customShoppingListItems =
            customShoppingListItemRepo.findAllAvailableCustomShoppingListItemsForUserId(1L, 1L);
        assertEquals(2, customShoppingListItems.size());
    }

    @Test
    void findByUserIdTest() {
        CustomShoppingListItem customShoppingListItem = customShoppingListItemRepo.findByUserId(2L);
        assertEquals(6L, customShoppingListItem.getId());
    }

    @Test
    void findAllTest() {
        List<CustomShoppingListItem> customShoppingListItems =
            customShoppingListItemRepo.findAllByUserIdAndHabitId(1L, 1L);
        assertEquals(5, customShoppingListItems.size());
    }
}
