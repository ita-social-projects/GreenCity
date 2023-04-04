package greencity.mapping;

import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomShoppingListMapper
    extends AbstractConverter<CustomShoppingListItemResponseDto, CustomShoppingListItem> {
    @Override
    public CustomShoppingListItem convert(
        CustomShoppingListItemResponseDto customShoppingListItemResponseDto) {
        return CustomShoppingListItem.builder()
            .id(customShoppingListItemResponseDto.getId())
            .text(customShoppingListItemResponseDto.getText())
            .status(customShoppingListItemResponseDto.getStatus())
            .build();
    }

    /**
     * Method that build {@link List} of {@link CustomShoppingListItem} from
     * {@link List} of {@link CustomShoppingListItemResponseDto}.
     *
     * @param dtoList {@link List} of {@link CustomShoppingListItemResponseDto}
     * @return {@link List} of {@link CustomShoppingListItem}
     * @author Lilia Mokhnatska
     */
    public List<CustomShoppingListItem> mapAllToList(
        List<CustomShoppingListItemResponseDto> dtoList) {
        return dtoList.stream().map(this::convert).collect(Collectors.toList());
    }
}
