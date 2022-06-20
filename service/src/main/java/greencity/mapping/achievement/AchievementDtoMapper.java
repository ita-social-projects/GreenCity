package greencity.mapping.achievement;

import greencity.constant.ErrorMessage;
import greencity.dto.achievement.AchievementDto;
import greencity.entity.Achievement;
import greencity.entity.localization.AchievementTranslation;
import greencity.exception.exceptions.NotFoundException;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AchievementDtoMapper extends AbstractConverter<Achievement, AchievementDto> {
    @Override
    protected AchievementDto convert(Achievement source) {
        AchievementTranslation enTranslation = source.getTranslations().stream()
            .filter(t -> "en".equalsIgnoreCase(t.getLanguage().getCode()))
            .findFirst()
            .orElse(new AchievementTranslation());
        AchievementTranslation ukTranslation = source.getTranslations().stream()
            .filter(t -> "ua".equalsIgnoreCase(t.getLanguage().getCode()))
            .findFirst()
            .orElse(new AchievementTranslation());

        return AchievementDto.builder()
            .id(source.getId())
            .icon(source.getIcon())
            .titleEn(enTranslation.getTitle())
            .titleUk(ukTranslation.getTitle())
            .descriptionEn(enTranslation.getDescription())
            .descriptionUk(ukTranslation.getDescription())
            .messageEn(enTranslation.getMessage())
            .messageUk(ukTranslation.getMessage())
            .category(source.getAchievementCategory().getName())
            .condition(source.getCondition())
            .usersAchievedTotal(Objects.nonNull(source.getUserAchievements())
                ? (long) source.getUserAchievements().size()
                : null)
            .build();
    }
}
