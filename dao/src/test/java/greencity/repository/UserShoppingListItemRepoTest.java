package greencity.repository;

import greencity.entity.ShoppingListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("classpath:sql/user_shopping_list_item.sql")
class UserShoppingListItemRepoTest {

    @Autowired
    UserShoppingListItemRepo userShoppingListItemRepo;

    @Test
    void findGoalByUserGoalIdTest() {
        ShoppingListItem shoppingListItem = userShoppingListItemRepo.findShoppingListByUserShoppingListItemId(1L).get();
        assertEquals(1, shoppingListItem.getId());
    }
}
