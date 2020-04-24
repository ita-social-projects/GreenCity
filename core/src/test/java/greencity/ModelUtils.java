package greencity;

import greencity.dto.discount.DiscountValueDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.*;
import greencity.entity.enums.HabitRate;
import greencity.entity.enums.ROLE;
import greencity.entity.localization.AdviceTranslation;
import greencity.entity.localization.GoalTranslation;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ModelUtils {
    public static Tag getTag() {
        return new Tag(1L, "tag", Collections.emptyList());
    }

    public static User getUser() {
        return User.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(ROLE.ROLE_USER)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    }

    public static EcoNewsAuthorDto getEcoNewsAuthorDto() {
        return new EcoNewsAuthorDto(1L, TestConst.NAME);
    }

    public static Language getLanguage() {
        return new Language(1L, "en", Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList());
    }

    public static EcoNews getEcoNews() {
        return new EcoNews(1L, ZonedDateTime.now(), TestConst.SITE, getUser(),
            "title", "text", Collections.singletonList(getTag()));
    }

    public static AddEcoNewsDtoRequest getAddEcoNewsDtoRequest() {
        return new AddEcoNewsDtoRequest("title", "text",
            Collections.singletonList("tag"));
    }

    public static AddEcoNewsDtoResponse getAddEcoNewsDtoResponse() {
        return new AddEcoNewsDtoResponse(1L, "title",
            "text", EcoNewsAuthorDto.builder().id(1L).name(TestConst.NAME).build(),
            ZonedDateTime.now(), TestConst.SITE,
            Collections.singletonList("tag"));
    }

    public static MultipartFile getFile() {
        Path path = Paths.get("src/test/resources/test.jpg");
        String name = TestConst.IMG_NAME;
        String contentType = "photo/plain";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }
        return new MockMultipartFile(name,
            name, contentType, content);
    }

    public static URL getUrl() throws MalformedURLException {
        return new URL(TestConst.SITE);
    }

    public static DiscountValue getDiscountValue() {
        return new DiscountValue(null, 33, null, null);
    }

    public static DiscountValueDto getDiscountValueDto() {
        return new DiscountValueDto(33, null);
    }

    public static Place getPlace() {
        Place place = new Place();
        place.setLocation(new Location(1L, 49.84988, 24.022533, "вулиця Під Дубом, 7Б", place));
        place.setId(1L);
        place.setName("Forum");
        place.setDescription("Shopping center");
        place.setPhone("0322 489 850");
        place.setEmail("forum_lviv@gmail.com");
        place.setAuthor(getUser());
        place.setModifiedDate(ZonedDateTime.now());
        return place;
    }

    public static FavoritePlace getFavoritePlace() {
        return new FavoritePlace(3L, "name", getUser(), getPlace());
    }

    public static FavoritePlaceDto getFavoritePlaceDto() {
        return new FavoritePlaceDto("name", 3L);
    }

    public static AddHabitStatisticDto addHabitStatisticDto() {
        return AddHabitStatisticDto.builder()
            .id(1L)
            .amountOfItems(5)
            .habitRate(HabitRate.DEFAULT)
            .habitId(13L)
            .createdOn(ZonedDateTime.now())
            .build();
    }

    public static HabitStatistic getHabitStatistic() {
        return HabitStatistic.builder()
            .id(1L)
            .habitRate(HabitRate.DEFAULT)
            .createdOn(ZonedDateTime.now())
            .amountOfItems(5)
            .habit(Habit.builder().id(13L).build())
            .build();
    }

    public static Habit getHabit() {
        return Habit.builder()
            .id(13L)
            .statusHabit(true)
            .createDate(ZonedDateTime.now())
            .user(getUser())
            .habitDictionary(HabitDictionary.builder().id(2L).image("cup").build())
            .build();
    }

    public static AdviceTranslation getAdviceTranslation() {
        AdviceTranslation adviceTranslation = new AdviceTranslation();
        adviceTranslation.setId(5L);
        adviceTranslation.setLanguage(new Language(2L, "en", Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList()));
        adviceTranslation.setAdvice(
            Advice.builder().id(2L).habitDictionary(HabitDictionary.builder().id(2L).image("cup").build()).build());
        adviceTranslation.setContent("Don't take a cup");
        return adviceTranslation;
    }

    public static GoalTranslation getGoalTranslation() {
        return GoalTranslation.builder()
            .id(2L)
            .language(new Language(2L, "en", Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList()))
            .goal(new Goal(1L, Collections.emptyList(), Collections.emptyList()))
            .text("Buy a bamboo toothbrush")
            .build();
    }
}
