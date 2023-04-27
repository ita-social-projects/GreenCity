package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CustomShoppingListResponseDtoMapperTest {

    @InjectMocks
    private CustomShoppingListResponseDtoMapper customShoppingListResponseDtoMapper;

    @Test
    void convertTest() {
        CustomShoppingListItem customShoppingListItem =
            ModelUtils.getCustomShoppingListItem();

        CustomShoppingListItemResponseDto expected = CustomShoppingListItemResponseDto.builder()
            .id(customShoppingListItem.getId())
            .status(customShoppingListItem.getStatus())
            .text(customShoppingListItem.getText())
            .build();
        assertEquals(expected, customShoppingListResponseDtoMapper.convert(customShoppingListItem));
    }

    @Test
    void mapAllToListTest() {
        CustomShoppingListItem customShoppingListItem =
            ModelUtils.getCustomShoppingListItem();

        List<CustomShoppingListItem> customShoppingListItemList =
            List.of(customShoppingListItem);

        CustomShoppingListItemResponseDto expected = CustomShoppingListItemResponseDto.builder()
            .id(customShoppingListItem.getId())
            .status(customShoppingListItem.getStatus())
            .text(customShoppingListItem.getText())
            .build();
        List<CustomShoppingListItemResponseDto> expectedList = List.of(expected);
        assertEquals(expectedList, customShoppingListResponseDtoMapper.mapAllToList(customShoppingListItemList));
    }
}
