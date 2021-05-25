package greencity.mapping;

import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.entity.UserShoppingListItem;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UserShoppingListItemResponseDtoMapper
    extends AbstractConverter<UserShoppingListItem, UserShoppingListItemResponseDto> {
    /**
     * Method for converting {@link UserShoppingListItem} into
     * {@link UserShoppingListItemResponseDto}.
     *
     * @param userShoppingListItem object to convert.
     * @return converted object.
     */
    @Override
    public UserShoppingListItemResponseDto convert(UserShoppingListItem userShoppingListItem) {
        return UserShoppingListItemResponseDto.builder()
            .id(userShoppingListItem.getId())
            .status(userShoppingListItem.getStatus())
            .build();
    }
}
