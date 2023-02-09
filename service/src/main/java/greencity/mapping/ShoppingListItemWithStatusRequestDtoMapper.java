package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemWithStatusRequestDto;
import greencity.entity.ShoppingListItem;
import greencity.entity.UserShoppingListItem;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class ShoppingListItemWithStatusRequestDtoMapper
    extends AbstractConverter<ShoppingListItemWithStatusRequestDto, UserShoppingListItem> {
    /**
     * Method for converting {@link ShoppingListItemWithStatusRequestDto} into
     * {@link UserShoppingListItem}.
     *
     * @param itemDto object to convert.
     * @return converted object.
     */
    @Override
    protected UserShoppingListItem convert(ShoppingListItemWithStatusRequestDto itemDto) {
        return UserShoppingListItem.builder()
            .shoppingListItem(ShoppingListItem.builder().id(itemDto.getId()).build())
            .status(itemDto.getStatus())
            .build();
    }
}
