package greencity.mapping;

import greencity.dto.habitstatistic.HabitDictionaryDto;
import greencity.entity.HabitDictionary;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class HabitDictionaryMapper implements MapperToDto<HabitDictionary, HabitDictionaryDto> {
    private ModelMapper modelMapper;

    @Override
    public HabitDictionaryDto convertToDto(HabitDictionary entity) {
        return modelMapper.map(entity, HabitDictionaryDto.class);
    }
}
