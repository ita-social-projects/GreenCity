package greencity;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.AppConstant;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.AchievementPostDto;
import greencity.dto.achievement.AchievementTranslationVO;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryDto;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.breaktime.BreakTimeDto;
import greencity.dto.category.CategoryVO;
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
import greencity.dto.shoppinglistitem.ShoppingListItemPostDto;
import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactTranslationUpdateDto;
import greencity.dto.habitfact.HabitFactTranslationVO;
import greencity.dto.habitfact.HabitFactUpdateDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.language.LanguageVO;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.location.LocationVO;
import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.place.PlaceVO;
import greencity.dto.tag.TagPostDto;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.dto.tag.TagViewDto;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoRequest;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentAuthorDto;
import greencity.dto.user.*;
import greencity.entity.*;
import greencity.entity.localization.AdviceTranslation;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ModelUtils {
    public static Tag getTag() {
        return new Tag(1L, TagType.ECO_NEWS, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
            Collections.emptySet());
    }

    public static List<TagTranslationVO> getTagTranslationsVO() {
        return Arrays.asList(TagTranslationVO.builder().id(1L).name("Новини").build(),
            TagTranslationVO.builder().id(2L).name("News").build(),
            TagTranslationVO.builder().id(3L).name("Новины").build());
    }

    public static LanguageVO getLanguageVO() {
        return new LanguageVO(1L, AppConstant.DEFAULT_LANGUAGE_CODE);
    }

    public static TagVO getTagVO() {
        return new TagVO(1L, TagType.ECO_NEWS, getTagTranslationsVO(), null, null, null);
    }

    public static TagPostDto getTagPostDto() {
        return new TagPostDto(TagType.ECO_NEWS, Collections.emptyList());
    }

    public static TagViewDto getTagViewDto() {
        return new TagViewDto("3", "ECO_NEWS", "News");
    }

    public static PageableAdvancedDto<TagVO> getPageableAdvancedDtoForTag() {
        return new PageableAdvancedDto<>(Collections.singletonList(getTagVO()),
            10, 1, 4, 8,
            true, true, true, true);
    }

    public static User getUser() {
        return User.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .lastActivityTime(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    }

    public static UserVO getUserVO() {
        return UserVO.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
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
            "title", "text", null, Collections.singletonList(getTag()), null);
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

    public static Category getCategory() {
        return Category.builder()
            .id(12L)
            .name("category")
            .build();
    }

    public static ShoppingListItemTranslation getShoppingListItemTranslation() {
        return ShoppingListItemTranslation.builder()
            .id(2L)
            .language(
                new Language(2L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()))
            .shoppingListItem(
                new ShoppingListItem(1L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
            .content("Buy a bamboo toothbrush")
            .build();
    }

    public static List<ShoppingListItemTranslation> getShoppingListItemTranslations() {
        return Arrays.asList(
            ShoppingListItemTranslation.builder()
                .id(2L)
                .language(new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()))
                .content("Buy a bamboo toothbrush")
                .shoppingListItem(
                    new ShoppingListItem(1L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
                .build(),
            ShoppingListItemTranslation.builder()
                .id(11L)
                .language(new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()))
                .content("Start recycling batteries")
                .shoppingListItem(
                    new ShoppingListItem(4L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
                .build());
    }

    public static Advice getAdvice() {
        return new Advice(1L, null, null);
    }

    public static HabitFact getHabitFact() {
        return new HabitFact(1L, Collections.singletonList(getHabitFactTranslation()), null);
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

    public static LocationVO getLocationVO() {
        return LocationVO.builder()
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
        return new UserProfilePictureDto(1L, "name", "image");
    }

    public static FactOfTheDayDTO getFactOfTheDayDto() {
        return new FactOfTheDayDTO(1L, "name", null, ZonedDateTime.now());
    }

    public static FactOfTheDay getFactOfTheDay() {
        return new FactOfTheDay(1L, "Fact of the day", null, ZonedDateTime.now());
    }

    public static FactOfTheDayPostDTO getFactOfTheDayPostDto() {
        return new FactOfTheDayPostDTO(1L, "name",
            Collections.singletonList(
                new FactOfTheDayTranslationEmbeddedPostDTO("content", AppConstant.DEFAULT_LANGUAGE_CODE)));
    }

    public static ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    public static HabitAssign getHabitAssign() {
        return HabitAssign.builder()
            .id(1L)
            .status(HabitAssignStatus.ACQUIRED)
            .createDate(ZonedDateTime.now())
            .habit(Habit.builder()
                .id(1L)
                .image("")
                .habitTranslations(Collections.singletonList(HabitTranslation.builder()
                    .id(1L)
                    .name("")
                    .description("")
                    .habitItem("")
                    .language(getLanguage())
                    .build()))
                .build())
            .user(getUser())
            .workingDays(0)
            .duration(0)
            .habitStreak(0)
            .lastEnrollmentDate(ZonedDateTime.now())
            .build();
    }

    public static HabitStatusCalendar getHabitStatusCalendar() {
        return HabitStatusCalendar.builder()
            .id(1L)
            .enrollDate(LocalDate.now()).build();
    }

    public static AdviceTranslation getAdviceTranslation() {
        return AdviceTranslation.builder()
            .id(1L)
            .language(getLanguage())
            .content("Text content")
            .advice(getAdvice())
            .build();
    }

    public static HabitFactTranslation getHabitFactTranslation() {
        return HabitFactTranslation.builder()
            .id(1L)
            .content("content")
            .language(getLanguage())
            .factOfDayStatus(FactOfDayStatus.POTENTIAL)
            .habitFact(getHabitFact())
            .build();
    }

    public static HabitFactVO getHabitFactVO() {
        return HabitFactVO.builder()
            .id(1L)
            .habit(HabitVO.builder()
                .id(1L)
                .image("string")
                .build())
            .translations(Collections.singletonList(HabitFactTranslationVO.builder()
                .id(1L)
                .content("content")
                .factOfDayStatus(FactOfDayStatus.POTENTIAL)
                .habitFact(null)
                .language(LanguageVO.builder()
                    .id(1L)
                    .code("ua")
                    .build())
                .build()))
            .build();
    }

    public static PlaceVO getPlaceVO() {
        PlaceVO placeVO = new PlaceVO();
        placeVO.setLocation(getLocationVO());
        placeVO.setId(1L);
        placeVO.setName("Forum");
        placeVO.setDescription("Shopping center");
        placeVO.setPhone("0322 489 850");
        placeVO.setEmail("forum_lviv@gmail.com");
        placeVO.setAuthor(getUserVO());
        placeVO.setModifiedDate(ZonedDateTime.now());
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setName("category");
        placeVO.setCategory(categoryVO);
        return placeVO;
    }

    public static HabitFactPostDto getHabitFactPostDto() {
        return HabitFactPostDto.builder()
            .translations(List.of(getLanguageTranslationDTO()))
            .habit(new HabitIdRequestDto(1L))
            .build();
    }

    public static HabitFactUpdateDto getHabitFactUpdateDto() {
        return HabitFactUpdateDto.builder()
            .habit(new HabitIdRequestDto(1L))
            .translations(Collections.singletonList(
                new HabitFactTranslationUpdateDto(FactOfDayStatus.POTENTIAL, getLanguageDTO(), "")))
            .build();
    }

    public static List<LanguageTranslationDTO> getLanguageTranslationsDTOs() {
        return Arrays.asList(
            new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "hello"),
            new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "text"),
            new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "smile"));
    }

    public static AdvicePostDto getAdvicePostDto() {
        return new AdvicePostDto(getLanguageTranslationsDTOs(), new HabitIdRequestDto(1L));
    }

    public static ShoppingListItemPostDto getShoppingListItemPostDto() {
        return new ShoppingListItemPostDto(getLanguageTranslationsDTOs(), new ShoppingListItemRequestDto(1L));
    }

    public static List<AchievementTranslationVO> getAchievementTranslationVOS() {
        return Arrays.asList(
            new AchievementTranslationVO(1L, getLanguageVO(), "title", "description", "message"),
            new AchievementTranslationVO(2L, getLanguageVO(), "title", "description", "message"),
            new AchievementTranslationVO(3L, getLanguageVO(), "title", "description", "message"));
    }

    public static AchievementCategoryDto getAchievementCategoryDto() {
        return new AchievementCategoryDto("name");
    }

    public static AchievementPostDto getAchievementPostDto() {
        return new AchievementPostDto(getAchievementTranslationVOS(), getAchievementCategoryDto(), 1);
    }

    public static AchievementVO getAchievementVO() {
        return new AchievementVO(1L, getAchievementTranslationVOS(),
            Collections.singletonList(new UserAchievementVO()),
            new AchievementCategoryVO(1L, "name", null, null), 1);
    }

    public static UserShoppingListItem getUserShoppingListItem() {
        return UserShoppingListItem.builder()
            .id(1L)
            .status(ShoppingListItemStatus.DONE)
            .habitAssign(HabitAssign.builder()
                .id(1L)
                .status(HabitAssignStatus.ACQUIRED)
                .habitStreak(10)
                .duration(300)
                .lastEnrollmentDate(ZonedDateTime.now())
                .workingDays(5)
                .build())
            .shoppingListItem(ShoppingListItem.builder()
                .id(1L)
                .build())
            .dateCompleted(LocalDateTime.of(2021, 2, 2, 14, 2))
            .build();
    }

    public static UserManagementDto getUserManagementDto() {
        return UserManagementDto.builder()
            .id(1L)
            .name("Username")
            .email("user@gmail.com")
            .userCredo("Credo")
            .role(Role.ROLE_ADMIN)
            .userStatus(UserStatus.ACTIVATED)
            .build();
    }
}
