package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.entity.ShoppingListItem;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class ShoppingListItemRequestDtoMapper
    extends AbstractConverter<ShoppingListItemRequestDto, UserShoppingListItem> {
    /**
     * Method for converting {@link ShoppingListItemRequestDto} into
     * {@link UserShoppingListItem}.
     *
     * @param shoppingListItemRequestDto object to convert.
     * @return converted object.
     */
    @Override
    protected UserShoppingListItem convert(ShoppingListItemRequestDto shoppingListItemRequestDto) {
        return UserShoppingListItem.builder()
            .shoppingListItem(ShoppingListItem.builder().id(shoppingListItemRequestDto.getId()).build())
            .status(ShoppingListItemStatus.ACTIVE)
            .build();
    }
}
