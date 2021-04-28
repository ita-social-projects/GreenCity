package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemSaveRequestDto;
import greencity.entity.ShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.CategoryService;
import greencity.service.CustomShoppingListItemService;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;

import static greencity.ModelUtils.getPrincipal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomShoppingListItemControllerTest {
    private MockMvc mockMvc;

    @Mock
    CustomShoppingListItemService customShoppingListItemService;

    @InjectMocks
    CustomShoppingListItemController customController;
    ObjectMapper objectMapper;

    private Principal principal = getPrincipal();

    private static final String customLink = "/custom/shopping-list-items";

    private CustomShoppingListItemResponseDto dto;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(customController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
        objectMapper = new ObjectMapper();

        dto = new CustomShoppingListItemResponseDto(3L, "text",
            ShoppingListItemStatus.ACTIVE);
    }

    @Test
    void getAllAvailableCustomShoppingListItems() throws Exception {
        Long id = 1L;
        this.mockMvc.perform(get(customLink + "/" + id + "/" + id)
            .principal(principal)).andExpect(status().isOk());
        when(customShoppingListItemService.findAllAvailableCustomShoppingListItems(1L, 1L))
            .thenReturn(Collections.singletonList(dto));
        verify(customShoppingListItemService).findAllAvailableCustomShoppingListItems(id, id);
        assertEquals(dto,
            customController.getAllAvailableCustomShoppingListItems(id, id).getBody().get(0));
    }

    @Test
    void findAllByUserTest() throws Exception {
        Long id = 1L;
        this.mockMvc.perform(get(customLink + "/" + id + "/" + "custom-shopping-list-items?lang=ua")
            .principal(principal)).andExpect(status().isOk());
        when(customShoppingListItemService.findByActiveByUserIdAndLanguageCode(id, "ua"))
            .thenReturn(new ArrayList<>());
        verify(customShoppingListItemService).findByActiveByUserIdAndLanguageCode(id, "ua");
        assertTrue(customController.findAllByUser(id, "ua").getBody().isEmpty());
    }

    @Test
    void save() throws Exception {
        Long id = 1L;
        CustomShoppingListItemSaveRequestDto customShoppingListItemSaveRequestDto =
            new CustomShoppingListItemSaveRequestDto("Texttext");
        BulkSaveCustomShoppingListItemDto bulkSaveCustomShoppingListItemDto = new BulkSaveCustomShoppingListItemDto();
        String content = objectMapper.writeValueAsString(bulkSaveCustomShoppingListItemDto);
        this.mockMvc.perform(post(customLink + "/" + id + "/" + id + "/" + "custom-shopping-list-items")
            .content(content)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
        when(customShoppingListItemService.save(bulkSaveCustomShoppingListItemDto, id, id))
            .thenReturn(Collections.singletonList(dto));
        verify(customShoppingListItemService).save(bulkSaveCustomShoppingListItemDto, id, id);
        assertEquals(dto, customController
            .saveUserCustomShoppingListItems(bulkSaveCustomShoppingListItemDto, id, id).getBody().get(0));
    }

    @Test
    void updateItemStatus() throws Exception {
        this.mockMvc.perform(patch(customLink + "/{userId}/custom-shopping-list-items/?itemId=1&status=DONE", 1)
            .principal(principal))
            .andExpect(status().isOk());
        verify(customShoppingListItemService).updateItemStatus(1L, 1L, "DONE");
    }

    @Test
    void delete() throws Exception {
        String ids = "1,2";
        this.mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .delete(customLink + "/{userId}/custom-shopping-list-items", 1)
            .param("ids", ids)).andExpect(status().isOk());
        verify(customShoppingListItemService).bulkDelete(ids);

    }
}
