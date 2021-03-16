package greencity.controller;

import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.ShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.CategoryService;
import greencity.service.CustomShoppingListItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static greencity.ModelUtils.getPrincipal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomShoppingListItemControllerTest {
    private MockMvc mockMvc;

    @Mock
    CustomShoppingListItemService customShoppingListItemService;

    @InjectMocks
    CustomShoppingListItemController customController;

    private Principal principal = getPrincipal();

    private static final String customLink = "/custom/shopping-list-items";

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(customController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void updateItemStatus() throws Exception {
        mockMvc.perform(patch(customLink + "/{userId}/custom-shopping-list-items/?itemId=1&status=DONE", 1)
            .principal(principal))
            .andExpect(status().isOk());
        verify(customShoppingListItemService).updateItemStatus(1L, 1L, "DONE");
    }
}