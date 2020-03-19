package greencity;

import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsTranslationDto;
import greencity.dto.language.LanguageRequestDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.localization.EcoNewsTranslation;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;

public class ModelUtils {
    public static Tag getTag() {
        return new Tag(1L, "tag", Collections.emptyList());
    }

    public static User getUser() {
        return User.builder()
            .id(1L)
            .email("Nazar.stasyuk@gmail.com")
            .firstName("Nazar")
            .lastName("Stasyuk")
            .role(ROLE.ROLE_USER)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    }

    public static EcoNewsAuthorDto getEcoNewsAuthorDto() {
        return new EcoNewsAuthorDto(1L, "Nazar", "Stasyuk");
    }

    public static EcoNewsTranslation getEcoNewsTranslation() {
        return new EcoNewsTranslation(1L, getLanguage(), "title", "text", null);
    }

    public static EcoNewsTranslationDto getEcoNewsTranslationDto() {
        return new EcoNewsTranslationDto(getLanguageRequestDto(), "title", "text");
    }

    public static Language getLanguage() {
        return new Language(1L, "en", Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList());
    }

    public static LanguageRequestDto getLanguageRequestDto() {
        return new LanguageRequestDto("en");
    }

    public static EcoNews getEcoNews() {
        return new EcoNews(1L, ZonedDateTime.now(), "imagePath", getUser(),
            Collections.singletonList(getEcoNewsTranslation()),
            Collections.singletonList(getTag()));
    }

    public static AddEcoNewsDtoRequest getAddEcoNewsDtoRequest() {
        return new AddEcoNewsDtoRequest(Collections.singletonList(getEcoNewsTranslationDto()),
            Collections.singletonList("tag"),
            getEcoNewsAuthorDto(), "imagePath");
    }

    public static AddEcoNewsDtoResponse getAddEcoNewsDtoResponse() {
        return new AddEcoNewsDtoResponse(1L, getEcoNewsTranslation().getTitle(),
            getEcoNewsTranslation().getText(), getEcoNewsAuthorDto(),
            getEcoNews().getCreationDate(), "imagePath",
            Collections.singletonList("tag"));
    }
}
