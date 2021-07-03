package greencity.mapping;

import static greencity.ModelUtils.*;

import greencity.dto.habit.HabitAssignUserShoppingListItemDto;
import greencity.entity.HabitAssign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HabitAssignUserShoppingListItemDtoMapperTest {
    @InjectMocks
    private HabitAssignUserShoppingListItemDtoMapper mapper;

    @Test
    void convert() {
        HabitAssign habitAssign = getHabitAssignWithUserShoppingListItem();
        HabitAssignUserShoppingListItemDto dto = getHabitAssignUserShoppingListItemDto();

        assertEquals(dto, mapper.convert(habitAssign));
    }
}