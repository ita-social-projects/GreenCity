package greencity;

import greencity.constant.AppConstant;
import greencity.dto.advice.AdvicePostDTO;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.dto.discount.DiscountValueDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.fact.HabitFactPostDTO;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.dto.user.HabitDictionaryIdDto;
import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.*;
import greencity.entity.enums.GoalStatus;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
        return new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList());
    }

    public static EcoNews getEcoNews() {
        return new EcoNews(1L, ZonedDateTime.now(), TestConst.SITE, null, getUser(),
            "title", "text", Collections.singletonList(getTag()));
    }

    public static AddEcoNewsDtoRequest getAddEcoNewsDtoRequest() {
        return new AddEcoNewsDtoRequest("title", "text",
            Collections.singletonList("tag"), null, null);
    }

    public static AddEcoNewsDtoResponse getAddEcoNewsDtoResponse() {
        return new AddEcoNewsDtoResponse(1L, "title",
            "text", EcoNewsAuthorDto.builder().id(1L).name(TestConst.NAME).build(),
            ZonedDateTime.now(), TestConst.SITE, null,
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
            e.printStackTrace();
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

    public static HabitDictionary getHabitDictionary(){
        return HabitDictionary.builder()
                .id(1L)
                .image("imagePath")
                .habit(Collections.singletonList(ModelUtils.getHabit()))
                .build();
    }

    public static HabitDictionaryTranslation getHabitDictionaryTranslation(){
        return HabitDictionaryTranslation.builder()
                .id(1L)
                .name("habit")
                .description("description")
                .habitItem("habitItem")
                .language(ModelUtils.getLanguage())
                .habitDictionary(ModelUtils.getHabitDictionary())
                .build();
    }

    public static Category getCategory(){
        return Category.builder()
                .id(12L)
                .name("category")
                .build();
    }

    public static AdviceTranslation getAdviceTranslation() {
        AdviceTranslation adviceTranslation = new AdviceTranslation();
        adviceTranslation.setId(5L);
        adviceTranslation.setLanguage(
            new Language(2L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList()));
        adviceTranslation.setAdvice(
            Advice.builder().id(2L).habitDictionary(HabitDictionary.builder().id(2L).image("cup").build()).build());
        adviceTranslation.setContent("Don't take a cup");
        return adviceTranslation;
    }

    public static GoalTranslation getGoalTranslation() {
        return GoalTranslation.builder()
            .id(2L)
            .language(
                new Language(2L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList()))
            .goal(new Goal(1L, Collections.emptyList(), Collections.emptyList()))
            .text("Buy a bamboo toothbrush")
            .build();
    }

    public static UserGoal getCustomUserGoal() {
        return UserGoal.builder()
            .id(1L)
            .user(User.builder().id(1L).email(TestConst.EMAIL).name(TestConst.NAME).role(ROLE.ROLE_USER).build())
            .status(GoalStatus.ACTIVE)
            .customGoal(CustomGoal.builder().id(8L).text("Buy electric car").build())
            .build();
    }

    public static UserGoalResponseDto getCustomUserGoalDto() {
        return UserGoalResponseDto.builder()
            .id(1L)
            .text("Buy electric car")
            .status(GoalStatus.ACTIVE)
            .build();
    }

    public static UserGoal getPredefinedUserGoal() {
        return UserGoal.builder()
            .id(2L)
            .user(User.builder().id(1L).email(TestConst.EMAIL).name(TestConst.NAME).role(ROLE.ROLE_USER).build())
            .status(GoalStatus.ACTIVE)
            .goal(Goal.builder().id(1L).userGoals(Collections.emptyList()).translations(getGoalTranslations()).build())
            .build();
    }

    public static UserGoalResponseDto getPredefinedUserGoalDto() {
        return UserGoalResponseDto.builder()
            .id(2L)
            .text("Buy a bamboo toothbrush")
            .status(GoalStatus.ACTIVE)
            .build();
    }

    public static List<GoalTranslation> getGoalTranslations() {
        return Arrays.asList(
            new GoalTranslation(2L,
                new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList()), "Buy a bamboo toothbrush",
                new Goal(1L, Collections.emptyList(), Collections.emptyList())),
            new GoalTranslation(11L,
                new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList()), "Start recycling batteries",
                new Goal(4L, Collections.emptyList(), Collections.emptyList())));
    }

    public static Comment getComment() {
        return new Comment(1L, "text", getUser(),
            getPlace(), null, null, Collections.emptyList(), null, null, null);
    }

    public static CommentReturnDto getCommentReturnDto() {
        return new CommentReturnDto(1L, "text", null, null, null);
    }

    public static AddCommentDto getAddCommentDto() {
        return new AddCommentDto("comment", null, null);
    }


    public static Advice getAdvice() {
        return new Advice(1L, null, null);
    }

    public static HabitDictionaryIdDto getHabitDictionaryIdDto() {
        return new HabitDictionaryIdDto(1L);
    }

    public static AdvicePostDTO getAdvicePostDTO() {
        return new AdvicePostDTO(null, getHabitDictionaryIdDto());
    }

    public static FactTranslation getFactTranslation() {
        return new FactTranslation(1L, getLanguage(), null, "Content");
    }

    public static HabitFact getHabitFact() {
        return new HabitFact(1L, Collections.singletonList(getFactTranslation()), null);
    }

    public static HabitFactPostDTO getHabitFactPostDTO() {
        return new HabitFactPostDTO(null, getHabitDictionaryIdDto());
    }


}
 