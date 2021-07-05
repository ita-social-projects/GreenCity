package greencity.controller;

import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.service.ShoppingListItemService;
import java.util.Collections;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.validation.Validator;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ShoppingListItemControllerTest {
    private static final String shoppingListItemLink = "/user/shopping-list-items";
    private MockMvc mockMvc;
    @InjectMocks
    private ShoppingListItemController shoppingListItemController;
    @Mock
    private ShoppingListItemService shoppingListItemService;
    @Mock
    private Validator mockValidator;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(shoppingListItemController)
            .setValidator(mockValidator)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void bulkDeleteUserShoppingListItemTest() throws Exception {
        mockMvc.perform(delete(shoppingListItemLink + "/user-shopping-list-items?ids=1,2", 1))
            .andExpect(status().isOk());

        verify(shoppingListItemService).deleteUserShoppingListItems("1,2");
    }

    @Test
    void updateUserShoppingListItemStatusWithLanguageParamTest() throws Exception {
        mockMvc.perform(patch(shoppingListItemLink + "/{userShoppingListItemId}", 1, 1)
            .locale(new Locale("ru")))
            .andExpect(status().isCreated());

        verify(shoppingListItemService).updateUserShopingListItemStatus(null, 1L, "ru");
    }

    @Test
    void updateUserShoppingListItemStatus() throws Exception {
        mockMvc.perform(patch(shoppingListItemLink
            + "/{shoppingListItemId}/status/{status}", 1, "DONE")
                .locale(new Locale("en")))
            .andExpect(status().isOk());

        verify(shoppingListItemService)
            .updateUserShoppingListItemStatus(null, 1L, "en", "DONE");
    }

    @Test
    void updateUserShoppingListItemStatusWithoutLanguageParamTest() throws Exception {
        mockMvc.perform(patch(shoppingListItemLink + "/{userShoppingListItemId}", 1, 1))
            .andExpect(status().isCreated());

        verify(shoppingListItemService).updateUserShopingListItemStatus(null, 1L, "en");
    }

    @Test
    void saveUserShoppingListItemWithoutLanguageParamTest() throws Exception {
        String content = "[\n"
            + " {\n"
            + "    \"id\": 1\n"
            + " }\n"
            + "]\n";

        mockMvc.perform(post(shoppingListItemLink + "?habitId=1&lang=en", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        ShoppingListItemRequestDto dto = new ShoppingListItemRequestDto(1L);
        verify(shoppingListItemService).saveUserShoppingListItems(null, 1L, Collections.singletonList(dto), "en");
    }

    @Test
    void getUserShoppingListItemsWithLanguageParamTest() throws Exception {
        mockMvc.perform(get(shoppingListItemLink + "/habits/1/shopping-list?lang=en", 1))
            .andExpect(status().isOk());

        verify(shoppingListItemService).getUserShoppingList(null, 1L, "en");
    }

    @Test
    void getUserShoppingListItemWithoutLanguageParamTest() throws Exception {
        mockMvc.perform(get(shoppingListItemLink + "/habits/1/shopping-list", 1))
            .andExpect(status().isOk());

        verify(shoppingListItemService).getUserShoppingList(null, 1L, "en");
    }
}
