package greencity.mapping;

import greencity.client.UserRemoteClient;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.todolistitem.ToDoListItemDto;
import greencity.entity.HabitTranslation;
import greencity.entity.localization.ToDoListItemTranslation;
import greencity.entity.localization.TagTranslation;
import java.util.ArrayList;
import java.util.stream.Collectors;
import greencity.enums.ToDoListItemStatus;
import greencity.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitTranslation} into
 * {@link HabitDto}.
 */
@Component
@RequiredArgsConstructor
public class HabitDtoMapper extends AbstractConverter<HabitTranslation, HabitDto> {
    private final LanguageService languageService;
    private final UserRemoteClient userRemoteClient;

    /**
     * Method convert {@link HabitTranslation} to {@link HabitDto}.
     *
     * @return {@link HabitDto}
     */
    @Override
    protected HabitDto convert(HabitTranslation habitTranslation) {
        Long habitTranslationLanguageId = habitTranslation.getLanguageId();
        String habitTranslationLanguageCode = userRemoteClient.findLanguageCodeById(habitTranslationLanguageId);

        var habit = habitTranslation.getHabit();
        return HabitDto.builder()
            .id(habit.getId())
            .image(habitTranslation.getHabit().getImage())
            .defaultDuration(habitTranslation.getHabit().getDefaultDuration())
            .complexity(habit.getComplexity())
            .habitTranslation(HabitTranslationDto.builder()
                .name(habitTranslation.getName())
                .description(habitTranslation.getDescription())
                .habitItem(habitTranslation.getHabitItem())
                .languageCode(habitTranslationLanguageCode)
                .build())
            .tags(habit.getTags().stream()
                .flatMap(tag -> tag.getTagTranslations().stream())
                .filter(tagTranslation -> {
                    Long languageId = tagTranslation.getLanguageId();
                    String languageCode = languageService.findLanguageCodeById(languageId);
                    return languageCode.equals(habitTranslationLanguageCode);
                })
                .map(TagTranslation::getName).collect(Collectors.toList()))
            .toDoListItems(habit.getToDoListItems() != null ? habit.getToDoListItems().stream()
                .map(shoppingListItem -> ToDoListItemDto.builder()
                    .id(shoppingListItem.getId())
                    .status(ToDoListItemStatus.ACTIVE.toString())
                    .text(shoppingListItem.getTranslations().stream()
                        .filter(shoppingListItemTranslation -> {
                            Long languageId = shoppingListItemTranslation.getLanguageId();
                            String languageCode = languageService.findLanguageCodeById(languageId);
                            return languageCode.equals(habitTranslationLanguageCode);
                        })
                        .map(ToDoListItemTranslation::getContent)
                        .findFirst().orElse(null))
                    .build())
                .collect(Collectors.toList()) : new ArrayList<>())
            .build();
    }
}
