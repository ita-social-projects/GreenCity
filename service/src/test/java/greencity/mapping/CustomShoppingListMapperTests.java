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
class CustomShoppingListMapperTests {
    @InjectMocks
    private CustomShoppingListMapper customShoppingListMapper;

    @Test
    void convertTest() {
        CustomShoppingListItemResponseDto customShoppingListItemResponseDto =
            ModelUtils.getCustomShoppingListItemResponseDto();

        CustomShoppingListItem expected = CustomShoppingListItem.builder()
            .id(customShoppingListItemResponseDto.getId())
            .status(customShoppingListItemResponseDto.getStatus())
            .text(customShoppingListItemResponseDto.getText())
            .build();
        assertEquals(expected, customShoppingListMapper.convert(customShoppingListItemResponseDto));
    }

    @Test
    void mapAllToListTest() {
        CustomShoppingListItemResponseDto customShoppingListItemResponseDto =
            ModelUtils.getCustomShoppingListItemResponseDto();
        List<CustomShoppingListItemResponseDto> customShoppingListItemResponseDtoList =
            List.of(ModelUtils.getCustomShoppingListItemResponseDto());

        CustomShoppingListItem expected = CustomShoppingListItem.builder()
            .id(customShoppingListItemResponseDto.getId())
            .status(customShoppingListItemResponseDto.getStatus())
            .text(customShoppingListItemResponseDto.getText())
            .build();
        List<CustomShoppingListItem> expectedList = List.of(expected);
        assertEquals(expectedList, customShoppingListMapper.mapAllToList(customShoppingListItemResponseDtoList));
    }
}
