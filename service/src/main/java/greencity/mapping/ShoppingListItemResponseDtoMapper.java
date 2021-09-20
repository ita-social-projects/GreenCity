package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.ShoppingListItemTranslationDTO;
import greencity.entity.ShoppingListItem;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map {@link ShoppingListItem} into
 * {@link ShoppingListItemResponseDto}.
 */
@Component
public class ShoppingListItemResponseDtoMapper
    extends AbstractConverter<ShoppingListItem, ShoppingListItemResponseDto> {
    @Override
    protected ShoppingListItemResponseDto convert(ShoppingListItem shoppingListItem) {
        return ShoppingListItemResponseDto.builder()
            .id(shoppingListItem.getId())
            .translations(shoppingListItem.getTranslations().stream().map(
                shoppingListItemTranslation -> ShoppingListItemTranslationDTO.builder()
                    .id(shoppingListItemTranslation.getId())
                    .content(shoppingListItemTranslation.getContent())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
