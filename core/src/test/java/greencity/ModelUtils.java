package greencity;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.AppConstant;
import greencity.dto.breaktime.BreakTimeDto;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.dto.discount.DiscountValueDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.econewscomment.EcoNewsCommentAuthorDto;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayPostDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationEmbeddedPostDTO;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoRequest;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentAuthorDto;
import greencity.dto.user.AuthorDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.dto.user.UserGoalResponseDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.entity.*;
import greencity.entity.enums.*;
import greencity.entity.localization.GoalTranslation;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModelUtils {
    public static Tag getTag() {
        return new Tag(1L, "tag", Collections.emptyList(), Collections.emptyList());
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
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public static EcoNews getEcoNews() {
        return new EcoNews(1L, ZonedDateTime.now(), TestConst.SITE, null, getUser(),
                "title", "text", null, Collections.singletonList(getTag()));
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
                .amountOfItems(5)
                .habitRate(HabitRate.DEFAULT)
                .createDate(ZonedDateTime.now())
                .habitAssignId(10L)

                .build();
    }

    public static HabitStatistic getHabitStatistic() {
        return HabitStatistic.builder()
            .id(1L)
            .habitRate(HabitRate.DEFAULT)
            .createDate(ZonedDateTime.now())
            .amountOfItems(5)
            .habitAssign(HabitAssign.builder().id(13L).build())
            .build();
    }

    /*public static Habit getHabit() {
        return Habit.builder()
                .id(13L)
                .statusHabit(true)
                .createDate(ZonedDateTime.now())
                .habitDictionary(HabitDictionary.builder().id(2L).image("cup").build())
                .build();
    }

    public static HabitDictionary getHabitDictionary() {
        return HabitDictionary.builder()
                .id(1L)
                .image("imagePath")
                .habit(Collections.singletonList(ModelUtils.getHabit()))
                .build();
    }

    public static HabitDictionaryTranslation getHabitDictionaryTranslation() {
        return HabitDictionaryTranslation.builder()
                .id(1L)
                .name("habit")
                .description("description")
                .habitItem("habitItem")
                .language(ModelUtils.getLanguage())
                .habitDictionary(ModelUtils.getHabitDictionary())
                .build();
    }*/

    public static HabitStatus getHabitStatus() {
        HabitAssign habitAssign = getHabitAssign();

        return HabitStatus.builder()
                .id(1L)
                .workingDays(10)
                .habitStreak(5)
                .lastEnrollmentDate(LocalDateTime.now())
                .habitAssign(habitAssign).build();

    }

    public static HabitAssign getHabitAssign() {
        return HabitAssign.builder()
                .id(1L)
                .acquired(true)
                .suspended(false)
                .createDate(ZonedDateTime.now())
                .habit(Habit.builder().id(1L).build()).build();
    }

    public static Category getCategory() {
        return Category.builder()
                .id(12L)
                .name("category")
                .build();
    }

    /*public static AdviceTranslation getAdviceTranslation() {
        AdviceTranslation adviceTranslation = new AdviceTranslation();
        adviceTranslation.setId(5L);
        adviceTranslation.setLanguage(
                new Language(2L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));
        adviceTranslation.setAdvice(
                Advice.builder().id(2L).habitDictionary(HabitDictionary.builder().id(2L).image("cup").build()).build());
        adviceTranslation.setContent("Don't take a cup");
        return adviceTranslation;
    }*/

    public static GoalTranslation getGoalTranslation() {
        return GoalTranslation.builder()
                .id(2L)
                .language(
                        new Language(2L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
                                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()))
                .goal(new Goal(1L, Collections.emptyList(), Collections.emptyList()))
                .text("Buy a bamboo toothbrush")
                .build();
    }

    public static UserGoal getCustomUserGoal() {
        return UserGoal.builder()
                .id(1L)
                .user(User.builder().id(1L).email(TestConst.EMAIL).name(TestConst.NAME).role(ROLE.ROLE_USER).build())
                .status(GoalStatus.DONE)
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
                                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()), "Buy a bamboo toothbrush",
                        new Goal(1L, Collections.emptyList(), Collections.emptyList())),
                new GoalTranslation(11L,
                        new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
                                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()), "Start recycling batteries",
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

    /*public static HabitDictionaryIdDto getHabitDictionaryIdDto() {
        return new HabitDictionaryIdDto(1L);
    }

    public static AdvicePostDTO getAdvicePostDTO() {
        return new AdvicePostDTO(null, getHabitDictionaryIdDto());
    }
*/
    public static HabitFactTranslation getFactTranslation() {
        return new HabitFactTranslation(1L, getLanguage(), FactOfDayStatus.CURRENT, null, "Content");
    }

    public static HabitFact getHabitFact() {
        return new HabitFact(1L, Collections.singletonList(getFactTranslation()), null);
    }

    /*public static HabitFactPostDTO getHabitFactPostDTO() {
        return new HabitFactPostDTO(null, getHabitDictionaryIdDto());
    }*/

    public static LocationAddressAndGeoDto getLocationAddressAndGeoDto() {
        return LocationAddressAndGeoDto.builder()
                .address("address")
                .lat(12.12d)
                .lng(12.12d)
                .build();
    }

    public static LocalTime getLocalTime() {
        return LocalTime.of(7, 20, 45, 342123342);
    }

    public static OpeningHoursDto getOpeningHoursDto() {
        OpeningHoursDto openingHours = new OpeningHoursDto();
        openingHours.setOpenTime(getLocalTime());
        openingHours.setCloseTime(getLocalTime());
        openingHours.setBreakTime(BreakTimeDto.builder()
                .startTime(getLocalTime())
                .endTime(getLocalTime())
                .build());
        openingHours.setWeekDay(DayOfWeek.MONDAY);
        return openingHours;
    }

    public static OpeningHours getOpeningHours() {
        OpeningHours openingHoursTest = new OpeningHours();
        openingHoursTest.setOpenTime(getLocalTime());
        openingHoursTest.setCloseTime(getLocalTime());
        openingHoursTest.setBreakTime(BreakTime.builder()
                .startTime(getLocalTime())
                .endTime(getLocalTime())
                .build());
        openingHoursTest.setWeekDay(DayOfWeek.MONDAY);
        return openingHoursTest;
    }

    public static Location getLocation() {
        return Location.builder()
                .address("address")
                .lng(12.12d)
                .lat(12.12d)
                .build();
    }

    public static Specification getSpecification() {
        return Specification.builder()
                .id(1L)
                .name("specification")
                .build();
    }

    public static Photo getPhoto() {
        return Photo.builder()
                .id(1L)
                .name("photo")
                .build();
    }

    public static LanguageDTO getLanguageDTO() {
        return new LanguageDTO(1L, "en");
    }

    public static LanguageTranslationDTO getLanguageTranslationDTO() {
        return new LanguageTranslationDTO(getLanguageDTO(), "content");
    }

    public static TipsAndTricks getTipsAndTricks() {
        return TipsAndTricks.builder()
                .id(1L)
                .titleTranslations(Collections.singletonList(TitleTranslation.builder()
                        .content("title content")
                        .language(getLanguage())
                        .build()))
                .textTranslations(Collections.singletonList(TextTranslation.builder()
                        .content("text content for tips and tricks")
                        .language(getLanguage())
                        .build()))
                .creationDate(ZonedDateTime.now())
                .author(getUser())
                .tags(Collections.singletonList(getTag()))
                .imagePath(null)
                .source(null)
                .build();
    }

    public static TipsAndTricksDtoRequest getTipsAndTricksDtoRequest() {
        return new TipsAndTricksDtoRequest(null, null, Collections.singletonList("tipsAndTricksTag"), null, null);
    }

    public static TipsAndTricksDtoResponse getTipsAndTricksDtoResponse() {
        return TipsAndTricksDtoResponse.builder()
                .id(1L)
                .title("title")
                .text("text")
                .creationDate(ZonedDateTime.now())
                .author(getAuthorDto())
                .tags(Collections.singletonList("tipsAndTricksTag"))
                .imagePath(TestConst.SITE)
                .source(null)
                .build();
    }

    private static AuthorDto getAuthorDto() {
        return AuthorDto.builder()
                .id(1L)
                .name("author")
                .build();
    }

    public static EcoNewsComment getEcoNewsComment() {
        return EcoNewsComment.builder()
                .id(1L)
                .text("text")
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .user(getUser())
                .ecoNews(getEcoNews())
                .build();
    }

    public static AddEcoNewsCommentDtoRequest getAddEcoNewsCommentDtoRequest() {
        return new AddEcoNewsCommentDtoRequest("text", 0L);
    }

    public static AddEcoNewsCommentDtoResponse getAddEcoNewsCommentDtoResponse() {
        return AddEcoNewsCommentDtoResponse.builder()
                .id(getEcoNewsComment().getId())
                .author(getEcoNewsCommentAuthorDto())
                .text(getEcoNewsComment().getText())
                .modifiedDate(getEcoNewsComment().getModifiedDate())
                .build();
    }

    public static EcoNewsCommentAuthorDto getEcoNewsCommentAuthorDto() {
        return EcoNewsCommentAuthorDto.builder()
                .id(getUser().getId())
                .name(getUser().getName().trim())
                .userProfilePicturePath(getUser().getProfilePicturePath())
                .build();
    }

    public static AddTipsAndTricksCommentDtoRequest getAddTipsAndTricksCommentDtoRequest() {
        return AddTipsAndTricksCommentDtoRequest.builder()
                .text(getTipsAndTricksComment().getText().intern())
                .parentCommentId(getTipsAndTricksComment().getId())
                .build();
    }

    public static TipsAndTricksComment getTipsAndTricksComment() {
        return TipsAndTricksComment.builder()
                .id(1L)
                .text("text")
                .user(getUser())
                .build();
    }

    public static AddTipsAndTricksCommentDtoResponse getAddTipsAndTricksCommentDtoResponse() {
        return AddTipsAndTricksCommentDtoResponse.builder()
                .id(getTipsAndTricksComment().getId())
                .text(getTipsAndTricksComment().getText())
                .author(TipsAndTricksCommentAuthorDto.builder()
                        .id(getUser().getId())
                        .name(getUser().getName())
                        .userProfilePicturePath(getUser().getProfilePicturePath())
                        .build())
                .build();
    }

    public static EcoNewsCommentDto getEcoNewsCommentDto() {
        return EcoNewsCommentDto.builder()
                .id(1L)
                .modifiedDate(LocalDateTime.now())
                .author(getEcoNewsCommentAuthorDto())
                .text("text")
                .replies(0)
                .likes(0)
                .currentUserLiked(false)
                .status(CommentStatus.ORIGINAL)
                .build();
    }

    public static Principal getPrincipal() {
        return () -> "test@gmail.com";
    }

    public static NewsSubscriberRequestDto getNewsSubscriberRequestDto() {
        return new NewsSubscriberRequestDto("test@gmail.com");
    }

    public static UserProfilePictureDto getUserProfilePictureDto() {
        return new UserProfilePictureDto(1L, "image");
    }

    public static FactOfTheDayDTO getFactOfTheDayDto() {
        return new FactOfTheDayDTO(1L, "name", null, ZonedDateTime.now());
    }

    public static FactOfTheDay getFactOfTheDay() {
        return new FactOfTheDay(1L, "Fact of the day", null, ZonedDateTime.now());
    }

    public static FactOfTheDayPostDTO getFactOfTheDayPostDto() {
        return new FactOfTheDayPostDTO(1L, "name",
                Collections.singletonList(new FactOfTheDayTranslationEmbeddedPostDTO("content", AppConstant.DEFAULT_LANGUAGE_CODE)));
    }

    public static ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
