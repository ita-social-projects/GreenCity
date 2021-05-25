package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.ShoppingListItemStatus;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map
 * {@link ShoppingListItemTranslation} into {@link ShoppingListItemDto}.
 */
@Component
public class ShoppingListItemDtoMapper extends AbstractConverter<ShoppingListItemTranslation, ShoppingListItemDto> {
    /**
     * Method for converting {@link ShoppingListItemTranslation} into
     * {@link ShoppingListItemDto}.
     *
     * @param shoppingListItemTranslation object to convert.
     * @return converted object.
     */
    @Override
    protected ShoppingListItemDto convert(ShoppingListItemTranslation shoppingListItemTranslation) {
        return new ShoppingListItemDto(shoppingListItemTranslation.getShoppingListItem().getId(),
            shoppingListItemTranslation.getContent(), ShoppingListItemStatus.ACTIVE.toString());
    }
}
