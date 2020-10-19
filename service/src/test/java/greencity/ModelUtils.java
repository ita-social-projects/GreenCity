package greencity;

import greencity.dto.factoftheday.*;
import greencity.dto.goal.CustomGoalVO;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.dto.language.LanguageVO;
import greencity.dto.user.UserGoalResponseDto;
import greencity.dto.user.UserGoalVO;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.entity.localization.GoalTranslation;
import greencity.enums.GoalStatus;
import greencity.enums.ROLE;
import greencity.service.TestConst;
import greencity.constant.AppConstant;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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

    public static Language getLanguage() {
        return new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public static EcoNews getEcoNews() {
        return new EcoNews(1L, ZonedDateTime.now(), TestConst.SITE, null, getUser(),
                "title", "text", null, Collections.singletonList(getTag()));
    }

    public static GoalTranslation getGoalTranslation() {
        return GoalTranslation.builder()
                .id(2L)
                .language(
                        new Language(2L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
                                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()))
                .goal(new Goal(1L, Collections.emptyList(), Collections.emptyList()))
                .content("Buy a bamboo toothbrush")
                .build();
    }

    public static HabitStatusDto getHabitStatusDto() {
        HabitAssignDto habitAssignDto = getHabitAssignDto();

        return HabitStatusDto.builder()
                .id(1L)
                .workingDays(10)
                .habitStreak(5)
                .lastEnrollmentDate(LocalDateTime.now())
                .habitAssign(habitAssignDto).build();

    }

    public static HabitAssignDto getHabitAssignDto() {
        return HabitAssignDto.builder()
                .id(1L)
                .acquired(true)
                .suspended(false)
                .createDateTime(ZonedDateTime.now())
                .habit(HabitDto.builder().id(1L).build()).build();
    }

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

    public static UserGoalVO getUserGoalVO(){
        return UserGoalVO.builder()
                .id(1L)
                .user(UserVO.builder()
                        .id(1L)
                        .email(TestConst.EMAIL)
                        .name(TestConst.NAME)
                        .role(ROLE.ROLE_USER)
                        .build())
                .status(GoalStatus.DONE)
                .customGoal(CustomGoalVO.builder()
                        .id(8L)
                        .text("Buy electric car")
                        .build())
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
                GoalTranslation.builder()
                        .id(2L)
                        .language(new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
                                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()))
                        .content("Buy a bamboo toothbrush")
                        .goal(new Goal(1L, Collections.emptyList(), Collections.emptyList()))
                        .build(),
                GoalTranslation.builder()
                        .id(11L)
                        .language(new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
                                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()))
                        .content("Start recycling batteries")
                        .goal(new Goal(4L, Collections.emptyList(), Collections.emptyList()))
                        .build());
    }

    public static FactOfTheDay getFactOfTheDay() {
        return new FactOfTheDay(1L, "Fact of the day",
                Collections.singletonList(ModelUtils.getFactOfTheDayTranslation()), ZonedDateTime.now());
    }

    public static FactOfTheDayDTO getFactOfTheDayDto() {
        return new FactOfTheDayDTO(1L, "name",  null, ZonedDateTime.now());
    }

    public static FactOfTheDayPostDTO getFactOfTheDayPostDto() {
        return new FactOfTheDayPostDTO(1L, "name",
                Collections.singletonList(new FactOfTheDayTranslationEmbeddedPostDTO("content", AppConstant.DEFAULT_LANGUAGE_CODE)));
    }

    public static FactOfTheDayTranslationVO getFactOfTheDayTranslationVO() {
        return FactOfTheDayTranslationVO.builder()
                .id(1L)
                .content("Content")
                .language(LanguageVO.builder()
                        .id(ModelUtils.getLanguage().getId())
                        .code(ModelUtils.getLanguage().getCode())
                        .build())
                .factOfTheDay(FactOfTheDayVO.builder()
                        .id(ModelUtils.getFactOfTheDay().getId())
                        .name(ModelUtils.getFactOfTheDay().getName())
                        .createDate(ModelUtils.getFactOfTheDay().getCreateDate())
                        .build())
                .build();
    }

    public static FactOfTheDayVO getFactOfTheDayVO() {
        return FactOfTheDayVO.builder()
                .id(1L)
                .name("name")
                .factOfTheDayTranslations(Collections.singletonList(ModelUtils.getFactOfTheDayTranslationVO()))
                .build();
    }

    public  static FactOfTheDayTranslation getFactOfTheDayTranslation() {
        return FactOfTheDayTranslation.builder()
                .id(1L)
                .content("Content")
                .language(ModelUtils.getLanguage())
                .factOfTheDay(null)
                .build();
    }

    public static Category getCategory() {
        return Category.builder()
            .id(12L)
            .name("category")
            .build();
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
}
