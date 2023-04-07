package greencity.mapping;

import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomShoppingListResponseDtoMapper
    extends AbstractConverter<CustomShoppingListItem, CustomShoppingListItemResponseDto> {
    @Override
    protected CustomShoppingListItemResponseDto convert(CustomShoppingListItem customShoppingListItem) {
        return CustomShoppingListItemResponseDto.builder()
            .id(customShoppingListItem.getId())
            .text(customShoppingListItem.getText())
            .status(customShoppingListItem.getStatus())
            .build();
    }

    /**
     * Method that build {@link List} of {@link CustomShoppingListItemResponseDto}
     * from {@link List} of {@link CustomShoppingListItemResponseDto}.
     *
     * @param itemList {@link List} of {@link CustomShoppingListItemResponseDto}
     * @return {@link List} of {@link CustomShoppingListItemResponseDto}
     * @author Lilia Mokhnatska
     */
    public List<CustomShoppingListItemResponseDto> mapAllToList(
        List<CustomShoppingListItem> itemList) {
        return itemList.stream().map(this::convert).collect(Collectors.toList());
    }
}
