package greencity;

import greencity.constant.AppConstant;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceTranslationVO;
import greencity.dto.advice.AdviceVO;
import greencity.dto.breaktime.BreakTimeDto;
import greencity.dto.category.CategoryDto;
import greencity.dto.category.CategoryVO;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.dto.discount.DiscountValueDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.econewscomment.EcoNewsCommentAuthorDto;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.factoftheday.*;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.favoriteplace.FavoritePlaceVO;
import greencity.dto.goal.CustomGoalVO;
import greencity.dto.goal.ShoppingListDtoResponse;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habit.HabitDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.*;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.dto.habitstatus.HabitStatusVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.language.LanguageVO;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.location.LocationVO;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.place.PlaceVO;
import greencity.dto.tag.TagVO;
import greencity.dto.tipsandtricks.*;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoRequest;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentAuthorDto;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO;
import greencity.dto.user.*;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.*;
import greencity.entity.localization.AdviceTranslation;
import greencity.entity.localization.GoalTranslation;
import greencity.enums.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;

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
            .verifyEmail(new VerifyEmail())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    }

    public static UserVO getUserVO() {
        return UserVO.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(ROLE.ROLE_USER)
            .lastVisit(LocalDateTime.now())
            .verifyEmail(new VerifyEmailVO())
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

    public static EcoNewsVO getEcoNewsVO() {
        return new EcoNewsVO(1L, ZonedDateTime.now(), TestConst.SITE, null, getUserVO(),
            "title", "text", null, Collections.singletonList(getTagVO()));
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

    public static HabitStatusCalendarDto getHabitStatusCalendarDto() {
        return HabitStatusCalendarDto.builder()
            .enrollDate(LocalDate.now()).id(1L).build();
    }

    public static HabitStatusCalendarVO getHabitStatusCalendarVO() {
        return HabitStatusCalendarVO.builder()
            .enrollDate(LocalDate.now()).id(1L).build();
    }

    public static HabitStatusCalendar getHabitStatusCalendar() {
        return HabitStatusCalendar.builder()
            .enrollDate(LocalDate.now()).id(1L).build();
    }

    public static HabitAssignDto getHabitAssignDto() {
        return HabitAssignDto.builder()
            .id(1L)
            .acquired(true)
            .suspended(false)
            .createDateTime(ZonedDateTime.now())
            .habit(HabitDto.builder().id(1L).build())
            .userId(1L).build();
    }

    public static HabitAssign getHabitAssign() {
        return HabitAssign.builder()
            .id(1L)
            .acquired(true)
            .suspended(false)
            .createDate(ZonedDateTime.now())
            .habitStatus(getHabitStatus())
            .habit(Habit.builder().id(1L).build())
            .habitStatistic(Collections.singletonList(getHabitStatistic()))
            .user(User.builder().id(1L).build()).build();
    }

    public static HabitAssignVO getHabitAssignVO() {
        return HabitAssignVO.builder()
            .id(1L)
            .habitVO(getHabitVO())
            .acquired(true)
            .suspended(false)
            .createDateTime(ZonedDateTime.now())
            .userVO(UserVO.builder().id(1L).build()).build();
    }

    public static HabitStatistic getHabitStatistic() {
        return HabitStatistic.builder()
            .id(1L).habitRate(HabitRate.GOOD).createDate(ZonedDateTime.now())
            .amountOfItems(10).build();
    }

    public static HabitStatusDto getHabitStatusDto() {
        HabitAssignDto habitAssignDto = getHabitAssignDto();

        return HabitStatusDto.builder()
            .id(1L)
            .workingDays(10)
            .habitStreak(5)
            .lastEnrollmentDate(LocalDateTime.now())
            .habitAssignId(habitAssignDto.getId())
            .habitStatusCalendarDtos(Collections.singletonList(getHabitStatusCalendarDto()))
            .build();
    }

    public static HabitVO getHabitVO() {
        return HabitVO.builder().id(1L).image("img.png").build();
    }

    public static HabitStatus getHabitStatus() {
        return HabitStatus.builder()
            .id(1L)
            .workingDays(10)
            .habitStreak(5)
            .lastEnrollmentDate(LocalDateTime.now())
            .habitAssign(HabitAssign.builder()
                .id(1L)
                .duration(14)
                .build())
            .habitStatusCalendars(
                Collections.singletonList(getHabitStatusCalendar()))
            .build();
    }

    public static HabitStatusVO getHabitStatusVO() {
        return HabitStatusVO.builder()
            .id(1L)
            .workingDays(10)
            .habitStreak(5)
            .lastEnrollmentDate(LocalDateTime.now())
            .habitAssignVO(getHabitAssignVO()).build();
    }

    public static UserGoal getCustomUserGoal() {
        return UserGoal.builder()
            .id(1L)
            .user(User.builder().id(1L).email(TestConst.EMAIL).name(TestConst.NAME).role(ROLE.ROLE_USER).build())
            .status(GoalStatus.DONE)
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

    public static UserGoalVO getUserGoalVO() {
        return UserGoalVO.builder()
            .id(1L)
            .user(UserVO.builder()
                .id(1L)
                .email(TestConst.EMAIL)
                .name(TestConst.NAME)
                .role(ROLE.ROLE_USER)
                .build())
            .status(GoalStatus.DONE)
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
                .language(new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()))
                .content("Buy a bamboo toothbrush")
                .goal(new Goal(1L, Collections.emptyList(), Collections.emptyList()))
                .build(),
            GoalTranslation.builder()
                .id(11L)
                .language(new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(),
                    Collections.emptyList(),
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
        return new FactOfTheDayDTO(1L, "name", null, ZonedDateTime.now());
    }

    public static FactOfTheDayPostDTO getFactOfTheDayPostDto() {
        return new FactOfTheDayPostDTO(1L, "name",
            Collections.singletonList(
                new FactOfTheDayTranslationEmbeddedPostDTO("content", AppConstant.DEFAULT_LANGUAGE_CODE)));
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

    public static FactOfTheDayTranslation getFactOfTheDayTranslation() {
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
        place.setStatus(PlaceStatus.PROPOSED);
        return place;
    }

    public static PlaceVO getPlaceVO() {
        PlaceVO placeVO = new PlaceVO();
        placeVO.setId(1L);
        placeVO.setName("Forum");
        placeVO.setDescription("Shopping center");
        placeVO.setPhone("0322 489 850");
        placeVO.setEmail("forum_lviv@gmail.com");
        placeVO.setLocation(LocationVO.builder()
            .id(1L)
            .build());
        placeVO.setModifiedDate(ZonedDateTime.now());
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setName("category");
        placeVO.setCategory(categoryVO);
        return placeVO;
    }

    public static PlaceAddDto getPlaceAddDto() {
        PlaceAddDto placeAddDto = new PlaceAddDto();
        placeAddDto.setName("Test");
        CategoryDto category = new CategoryDto();
        category.setName("category");
        placeAddDto.setCategory(category);
        placeAddDto.setLocation(getLocationAddressAndGeoDto());
        HashSet<OpeningHoursDto> openingHoursDtos = new HashSet<>();
        openingHoursDtos.add(getOpeningHoursDto());
        placeAddDto.setOpeningHoursList(openingHoursDtos);
        HashSet<DiscountValueDto> discountValueDtos = new HashSet<>();
        discountValueDtos.add(getDiscountValueDto());
        placeAddDto.setDiscountValues(discountValueDtos);
        return placeAddDto;
    }

    public static HabitFactTranslation getFactTranslation() {
        return HabitFactTranslation.builder()
            .id(1L)
            .factOfDayStatus(FactOfDayStatus.CURRENT)
            .habitFact(null)
            .content("Content")
            .build();
    }

    public static HabitFactTranslationVO getFactTranslationVO() {
        return HabitFactTranslationVO.builder()
            .id(1L)
            .factOfDayStatus(FactOfDayStatus.CURRENT)
            .habitFact(null)
            .language(getLanguageVO())
            .content("Content")
            .build();
    }

    public static HabitFact getHabitFact() {
        return new HabitFact(1L, Collections.singletonList(getFactTranslation()), null);
    }

    public static HabitFactVO getHabitFactVO() {
        return new HabitFactVO(1L, Collections.singletonList(getFactTranslationVO()), null);
    }

    public static LanguageTranslationDTO getLanguageTranslationDTO() {
        return new LanguageTranslationDTO(getLanguageDTO(), "content");
    }

    public static LanguageDTO getLanguageDTO() {
        return new LanguageDTO(1L, "en");
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

    public static EcoNewsAuthorDto getEcoNewsAuthorDto() {
        return new EcoNewsAuthorDto(1L, TestConst.NAME);
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

    public static UserProfilePictureDto getUserProfilePictureDto() {
        return new UserProfilePictureDto(1L, "image");
    }

    public static FactOfTheDayTranslationDTO getFactOfTheDayTranslationDTO() {
        return new FactOfTheDayTranslationDTO(1L, "content");
    }

    public static ShoppingListDtoResponse getShoppingListDtoResponse() {
        return ShoppingListDtoResponse.builder()
            .customGoalId(1L)
            .goalId(1L)
            .status("ACTIVE")
            .text("text")
            .build();
    }

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

    public static Photo getPhoto() {
        return Photo.builder()
            .id(1L)
            .name("photo")
            .build();
    }

    public static DiscountValue getDiscountValue() {
        return new DiscountValue(null, 33, null, null);
    }

    public static DiscountValueDto getDiscountValueDto() {
        return new DiscountValueDto(33, null);
    }

    public static TipsAndTricksVO getTipsAndTricksVO() {
        return TipsAndTricksVO.builder()
            .id(1L)
            .titleTranslations(Collections.singletonList(TitleTranslationVO.builder()
                .content("title content")
                .language(getLanguageVO())
                .build()))
            .textTranslations(Collections.singletonList(TextTranslationVO.builder()
                .content("text content for tips and tricks")
                .language(getLanguageVO())
                .build()))
            .creationDate(ZonedDateTime.now())
            .author(getUserVO())
            .tags(Collections.singletonList(getTagVO()))
            .imagePath(null)
            .source(null)
            .build();
    }

    public static LanguageVO getLanguageVO() {
        return new LanguageVO(1L, AppConstant.DEFAULT_LANGUAGE_CODE);
    }

    public static TagVO getTagVO() {
        return new TagVO(1L, "tag", null, null);
    }

    public static TitleTranslationVO getTitleTranslationVO() {
        return TitleTranslationVO.builder()
            .id(1L)
            .content("Content")
            .language(ModelUtils.getLanguageVO())
            .tipsAndTricks(ModelUtils.getTipsAndTricksVO())
            .build();
    }

    public static TextTranslationVO getTextTranslationVO() {
        return TextTranslationVO.builder()
            .id(1L)
            .content("Content")
            .language(ModelUtils.getLanguageVO())
            .tipsAndTricks(ModelUtils.getTipsAndTricksVO())
            .build();
    }

    public static Specification getSpecification() {
        return Specification.builder()
            .id(1L)
            .name("specification")
            .build();
    }

    public static HabitFactTranslation getHabitFactTranslation() {
        return HabitFactTranslation.builder()
            .id(1L)
            .habitFact(getHabitFact())
            .factOfDayStatus(FactOfDayStatus.POTENTIAL)
            .language(getLanguage())
            .content("content")
            .build();
    }

    public static HabitFactDto getHabitFactDto() {
        return HabitFactDto.builder()
            .id(1L)
            .habit(HabitDto.builder()
                .id(1L)
                .image("")
                .habitTranslation(null)
                .build())
            .content("content")
            .build();
    }

    public static HabitFactPostDto getHabitFactPostDto() {
        return HabitFactPostDto.builder()
            .habit(HabitIdRequestDto.builder()
                .id(1L)
                .build())
            .translations(Collections.singletonList(getLanguageTranslationDTO()))
            .build();
    }

    public static AddEcoNewsCommentDtoResponse getAddEcoNewsCommentDtoResponse() {
        return AddEcoNewsCommentDtoResponse.builder()
            .id(getEcoNewsComment().getId())
            .author(getEcoNewsCommentAuthorDto())
            .text(getEcoNewsComment().getText())
            .modifiedDate(getEcoNewsComment().getModifiedDate())
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

    public static EcoNewsCommentAuthorDto getEcoNewsCommentAuthorDto() {
        return EcoNewsCommentAuthorDto.builder()
            .id(getUser().getId())
            .name(getUser().getName().trim())
            .userProfilePicturePath(getUser().getProfilePicturePath())
            .build();
    }

    public static AddEcoNewsCommentDtoRequest getAddEcoNewsCommentDtoRequest() {
        return new AddEcoNewsCommentDtoRequest("text", 0L);
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

    public static TipsAndTricksComment getTipsAndTricksComment() {
        return TipsAndTricksComment.builder()
            .id(1L)
            .text("TipsAndTricksComment")
            .tipsAndTricks(getTipsAndTricks())
            .user(getUser())
            .build();
    }

    public static TipsAndTricksCommentVO getTipsAndTricksCommentVO() {
        TipsAndTricksCommentVO tipsAndTricksCommentVO = new TipsAndTricksCommentVO();
        tipsAndTricksCommentVO.setId(1L);
        tipsAndTricksCommentVO.setText("text");
        tipsAndTricksCommentVO.setUser(getUserVO());
        tipsAndTricksCommentVO.setUsersLiked(new HashSet<>());
        return tipsAndTricksCommentVO;
    }

    public static AddTipsAndTricksCommentDtoRequest getAddTipsAndTricksCommentDtoRequest() {
        return AddTipsAndTricksCommentDtoRequest.builder()
            .text(getTipsAndTricksComment().getText().intern())
            .parentCommentId(getTipsAndTricksComment().getId())
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

    public static FavoritePlaceDto getFavoritePlaceDto() {
        return new FavoritePlaceDto("name", 3L);
    }

    public static FavoritePlace getFavoritePlace() {
        return new FavoritePlace(3L, "name", getUser(), getPlace());
    }

    public static FavoritePlaceVO getFavoritePlaceVO() {
        return new FavoritePlaceVO(3L, "name", getUserVO(), getPlaceVO());
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

    public static AdviceTranslation getAdviceTranslation() {
        return AdviceTranslation.builder()
            .id(1L)
            .language(getLanguage())
            .content("Text content")
            .advice(getAdvice())
            .build();
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

    public static HabitFactUpdateDto getHabitFactUpdateDto() {
        return HabitFactUpdateDto.builder()
            .habit(HabitIdRequestDto.builder()
                .id(1L)
                .build())
            .translations(Collections.singletonList(getHabitFactTranslationUpdateDto()))
            .build();
    }

    public static HabitFactTranslationUpdateDto getHabitFactTranslationUpdateDto() {
        return HabitFactTranslationUpdateDto.builder()
            .content("")
            .factOfDayStatus(FactOfDayStatus.POTENTIAL)
            .language(getLanguageDTO())
            .build();
    }

    public static List<AdviceTranslation> getAdviceTranslations() {
        Language defaultLanguage = getLanguage();
        return new ArrayList<>(Arrays.asList(
            AdviceTranslation.builder().id(1L).language(defaultLanguage).content("hello").build(),
            AdviceTranslation.builder().id(2L).language(defaultLanguage).content("text").build(),
            AdviceTranslation.builder().id(3L).language(defaultLanguage).content("smile").build()));
    }

    public static List<AdviceTranslationVO> getAdviceTranslationVOs() {
        LanguageVO defaultLanguage = getLanguageVO();
        return new ArrayList<>(Arrays.asList(
            AdviceTranslationVO.builder().id(1L).language(defaultLanguage).content("hello").build(),
            AdviceTranslationVO.builder().id(2L).language(defaultLanguage).content("text").build(),
            AdviceTranslationVO.builder().id(3L).language(defaultLanguage).content("smile").build()));
    }

    public static List<LanguageTranslationDTO> getLanguageTranslationsDTOs() {
        return Arrays.asList(
            new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "hello"),
            new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "text"),
            new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "smile"));
    }

    public static List<Advice> getAdvices() {
        List<AdviceTranslation> adviceTranslations = getAdviceTranslations();
        return new ArrayList<>(Arrays.asList(
            Advice.builder().id(1L).habit(Habit.builder().id(1L).build())
                .translations(adviceTranslations).build(),
            Advice.builder().id(2L).habit(Habit.builder().id(1L).build()).translations(adviceTranslations).build(),
            Advice.builder().id(3L).habit(Habit.builder().id(1L).build()).translations(adviceTranslations).build()));
    }

    public static List<AdviceVO> getAdviceVOs() {
        List<AdviceTranslationVO> adviceTranslationVOs = getAdviceTranslationVOs();
        return new ArrayList<>(Arrays.asList(
            AdviceVO.builder().id(1L).habit(new HabitIdRequestDto(1L)).translations(adviceTranslationVOs).build(),
            AdviceVO.builder().id(2L).habit(new HabitIdRequestDto(1L)).translations(adviceTranslationVOs).build(),
            AdviceVO.builder().id(3L).habit(new HabitIdRequestDto(1L)).translations(adviceTranslationVOs).build()));
    }

    public static Habit getHabit() {
        return Habit.builder().id(1L).image("image.png").build();
    }

    public static Advice getAdvice() {
        return Advice.builder().id(1L)
            .translations(getAdviceTranslations())
            .habit(getHabit())
            .build();
    }

    public static AdviceVO getAdviceVO() {
        return AdviceVO.builder().id(1L)
            .translations(getAdviceTranslationVOs())
            .habit(new HabitIdRequestDto(1L))
            .build();
    }

    public static AdvicePostDto getAdvicePostDto() {
        return new AdvicePostDto(getLanguageTranslationsDTOs(), new HabitIdRequestDto(1L));
    }
}
