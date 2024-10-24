package greencity;

import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import greencity.constant.AppConstant;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.AchievementManagementDto;
import greencity.dto.achievement.AchievementPostDto;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.ActionDto;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryDto;
import greencity.dto.achievementcategory.AchievementCategoryTranslationDto;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.breaktime.BreakTimeDto;
import greencity.dto.category.CategoryDto;
import greencity.dto.category.CategoryVO;
import greencity.dto.comment.AddCommentDtoRequest;
import greencity.dto.comment.AddCommentDtoResponse;
import greencity.dto.comment.AmountCommentLikesDto;
import greencity.dto.comment.CommentAuthorDto;
import greencity.dto.comment.CommentDto;
import greencity.dto.comment.CommentVO;
import greencity.dto.discount.DiscountValueDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.EcoNewsDtoManagement;
import greencity.dto.econews.EcoNewsGenericDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econews.EcoNewsViewDto;
import greencity.dto.econews.ShortEcoNewsDto;
import greencity.dto.econews.UpdateEcoNewsDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddressDto;
import greencity.dto.event.EventAttenderDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventVO;
import greencity.dto.event.UpdateAddressDto;
import greencity.dto.event.UpdateEventDateLocationDto;
import greencity.dto.event.UpdateEventDto;
import greencity.dto.event.UpdateEventRequestDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayPostDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationEmbeddedPostDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationVO;
import greencity.dto.factoftheday.FactOfTheDayVO;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.favoriteplace.FavoritePlaceVO;
import greencity.dto.filter.FilterEventDto;
import greencity.dto.friends.UserAsFriendDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.geocoding.AddressLatLngResponse;
import greencity.dto.geocoding.AddressResponse;
import greencity.dto.habit.CustomHabitDtoRequest;
import greencity.dto.habit.CustomHabitDtoResponse;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignPropertiesDto;
import greencity.dto.habit.HabitAssignUserDurationDto;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habit.HabitDto;
import greencity.dto.habit.HabitEnrollDto;
import greencity.dto.habit.HabitManagementDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habit.HabitsDateEnrollmentDto;
import greencity.dto.habit.UserShoppingAndCustomShoppingListsDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.habittranslation.HabitTranslationManagementDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.language.LanguageVO;
import greencity.dto.location.AddPlaceLocation;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.location.LocationDto;
import greencity.dto.location.LocationVO;
import greencity.dto.location.UserLocationDto;
import greencity.dto.notification.EmailNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.photo.PhotoVO;
import greencity.dto.place.AddPlaceDto;
import greencity.dto.place.FilterPlaceCategory;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.dto.place.PlaceResponse;
import greencity.dto.place.PlaceVO;
import greencity.dto.placecomment.PlaceCommentRequestDto;
import greencity.dto.placecomment.PlaceCommentResponseDto;
import greencity.dto.search.SearchEventsDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemSaveRequestDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemWithStatusSaveRequestDto;
import greencity.dto.shoppinglistitem.ShoppingListItemWithStatusRequestDto;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.dto.specification.SpecificationVO;
import greencity.dto.tag.TagDto;
import greencity.dto.tag.TagPostDto;
import greencity.dto.tag.TagTranslationDto;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagUaEnDto;
import greencity.dto.tag.TagVO;
import greencity.dto.tag.TagViewDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.dto.user.SubscriberDto;
import greencity.dto.user.UserFilterDto;
import greencity.dto.user.UserFilterDtoRequest;
import greencity.dto.user.UserFilterDtoResponse;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserSearchDto;
import greencity.dto.user.UserShoppingListItemAdvanceDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.dto.user.UserShoppingListItemVO;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserTagDto;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.Achievement;
import greencity.entity.AchievementCategory;
import greencity.entity.BreakTime;
import greencity.entity.Category;
import greencity.entity.Comment;
import greencity.entity.CommentImages;
import greencity.entity.CustomShoppingListItem;
import greencity.entity.DiscountValue;
import greencity.entity.EcoNews;
import greencity.entity.FactOfTheDay;
import greencity.entity.FactOfTheDayTranslation;
import greencity.entity.FavoritePlace;
import greencity.entity.Filter;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatistic;
import greencity.entity.HabitStatusCalendar;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import greencity.entity.Location;
import greencity.entity.Notification;
import greencity.entity.OpeningHours;
import greencity.entity.Photo;
import greencity.entity.Place;
import greencity.entity.PlaceComment;
import greencity.entity.ShoppingListItem;
import greencity.entity.SocialNetworkImage;
import greencity.entity.Specification;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.entity.UserAction;
import greencity.entity.UserShoppingListItem;
import greencity.entity.VerifyEmail;
import greencity.entity.RatingPoints;
import greencity.entity.event.Address;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventGrade;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.TagTranslation;
import greencity.enums.ArticleType;
import greencity.enums.CommentStatus;
import greencity.enums.EmailNotification;
import greencity.enums.EventType;
import greencity.enums.HabitAssignStatus;
import greencity.enums.HabitRate;
import greencity.enums.PlaceStatus;
import greencity.enums.Role;
import greencity.enums.ShoppingListItemStatus;
import greencity.enums.TagType;
import greencity.enums.UserStatus;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import org.hibernate.sql.results.internal.TupleElementImpl;
import org.hibernate.sql.results.internal.TupleImpl;
import org.hibernate.sql.results.internal.TupleMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static greencity.TestConst.ROLE_ADMIN;
import static greencity.TestConst.STATUS_ACTIVATED;
import static greencity.TestConst.TEST_QUERY;
import static greencity.constant.EventTupleConstant.cityEn;
import static greencity.constant.EventTupleConstant.cityUa;
import static greencity.constant.EventTupleConstant.countComments;
import static greencity.constant.EventTupleConstant.countryEn;
import static greencity.constant.EventTupleConstant.countryUa;
import static greencity.constant.EventTupleConstant.creationDate;
import static greencity.constant.EventTupleConstant.description;
import static greencity.constant.EventTupleConstant.eventId;
import static greencity.constant.EventTupleConstant.finishDate;
import static greencity.constant.EventTupleConstant.formattedAddressEn;
import static greencity.constant.EventTupleConstant.formattedAddressUa;
import static greencity.constant.EventTupleConstant.grade;
import static greencity.constant.EventTupleConstant.houseNumber;
import static greencity.constant.EventTupleConstant.isFavorite;
import static greencity.constant.EventTupleConstant.isOpen;
import static greencity.constant.EventTupleConstant.isOrganizedByFriend;
import static greencity.constant.EventTupleConstant.isRelevant;
import static greencity.constant.EventTupleConstant.isSubscribed;
import static greencity.constant.EventTupleConstant.languageCode;
import static greencity.constant.EventTupleConstant.latitude;
import static greencity.constant.EventTupleConstant.likes;
import static greencity.constant.EventTupleConstant.longitude;
import static greencity.constant.EventTupleConstant.onlineLink;
import static greencity.constant.EventTupleConstant.organizerId;
import static greencity.constant.EventTupleConstant.organizerName;
import static greencity.constant.EventTupleConstant.regionEn;
import static greencity.constant.EventTupleConstant.regionUa;
import static greencity.constant.EventTupleConstant.startDate;
import static greencity.constant.EventTupleConstant.streetEn;
import static greencity.constant.EventTupleConstant.streetUa;
import static greencity.constant.EventTupleConstant.tagId;
import static greencity.constant.EventTupleConstant.tagName;
import static greencity.constant.EventTupleConstant.title;
import static greencity.constant.EventTupleConstant.titleImage;
import static greencity.constant.EventTupleConstant.type;
import static greencity.enums.EventStatus.OPEN;
import static greencity.enums.EventTime.PAST;
import static greencity.enums.NotificationType.EVENT_CREATED;
import static greencity.enums.ProjectName.GREENCITY;
import static greencity.enums.UserStatus.ACTIVATED;

public class ModelUtils {
    public static User testUser = createUser();
    public static User testUserRoleUser = createUserRoleUser();
    public static UserVO testUserVo = createUserVO();
    public static UserVO userVORoleUser = createUserVORoleUser();
    public static UserStatusDto testUserStatusDto = createUserStatusDto();
    public static String testEmail = "test@mail.com";
    public static String testEmail2 = "test2@mail.com";
    public static HabitAssign habitAssignInProgress = createHabitAssignInProgress();
    public static ZonedDateTime zonedDateTime = ZonedDateTime.now();
    public static LocalDateTime localDateTime = LocalDateTime.now();
    public static String habitTranslationName = "use shopper";
    public static String habitTranslationDescription = "Description";
    public static String shoppingListText = "buy a shopper";
    public static String habitItem = "Item";
    public static String habitDefaultImage = "img/habit-default.png";
    public static AddEventDtoRequest addEventDtoRequest = AddEventDtoRequest.builder()
        .datesLocations(List.of(EventDateLocationDto.builder()
            .id(1L)
            .event(null)
            .startDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
            .finishDate(ZonedDateTime.of(2000, 1, 1, 2, 1, 1, 1, ZoneId.systemDefault()))
            .onlineLink("/url")
            .coordinates(getAddressDto()).build()))
        .description("Description")
        .title("Title")
        .tags(List.of("Social"))
        .build();
    public static AddEventDtoRequest addEventDtoWithoutLinkRequest = AddEventDtoRequest.builder()
        .datesLocations(List.of(EventDateLocationDto.builder()
            .id(1L)
            .event(null)
            .startDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
            .finishDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
            .onlineLink(null)
            .coordinates(getAddressDto()).build()))
        .description("Description")
        .title("Title")
        .tags(List.of("Social"))
        .build();
    public static AddEventDtoRequest addEventDtoRequestWithNullRegionUa = AddEventDtoRequest.builder()
        .datesLocations(List.of(EventDateLocationDto.builder()
            .id(1L)
            .event(null)
            .startDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
            .finishDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
            .onlineLink(null)
            .coordinates(getAddressDtoWithNullRegionUa()).build()))
        .description("Description")
        .title("Title")
        .tags(List.of("Social"))
        .build();
    public static AddEventDtoRequest addEventDtoRequestWithNullCountryUa = AddEventDtoRequest.builder()
        .datesLocations(List.of(EventDateLocationDto.builder()
            .id(1L)
            .event(null)
            .startDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
            .finishDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
            .onlineLink(null)
            .coordinates(getAddressDtoWithNullCountryUa()).build()))
        .description("Description")
        .title("Title")
        .tags(List.of("Social"))
        .build();
    public static AddEventDtoRequest addEventDtoRequestWithNullData = AddEventDtoRequest.builder()
        .datesLocations(List.of(EventDateLocationDto.builder()
            .id(1L)
            .event(null)
            .startDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
            .finishDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
            .onlineLink(null)
            .coordinates(getAddressDtoWithoutData()).build()))
        .description("Description")
        .title("Title")
        .tags(List.of("Social"))
        .build();
    public static AddEventDtoRequest addEventDtoWithoutAddressRequest = AddEventDtoRequest.builder()
        .datesLocations(List.of(EventDateLocationDto.builder()
            .id(1L)
            .event(null)
            .startDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
            .finishDate(ZonedDateTime.of(2000, 1, 1, 2, 1, 1, 1, ZoneId.systemDefault()))
            .onlineLink("/url")
            .coordinates(null).build()))
        .description("Description")
        .title("Title")
        .tags(List.of("Social"))
        .build();
    public static AddEventDtoRequest addEventDtoWithoutAddressAndLinkRequest = AddEventDtoRequest.builder()
        .datesLocations(List.of(EventDateLocationDto.builder()
            .id(1L)
            .event(null)
            .startDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
            .finishDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
            .onlineLink(null)
            .coordinates(null).build()))
        .description("Description")
        .title("Title")
        .tags(List.of("Social"))
        .build();

    public static EventAttenderDto getEventAttenderDto() {
        return EventAttenderDto.builder().id(1L).name(TestConst.NAME).build();
    }

    public static Tag getTag() {
        return new Tag(1L, TagType.ECO_NEWS, getTagTranslations(), Collections.emptyList(),
            Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    public static Tag getHabitTag() {
        return new Tag(1L, TagType.HABIT, getHabitTagTranslations(), Collections.emptyList(),
            Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    public static Tag getEventTag() {
        return new Tag(1L, TagType.EVENT, getEventTagTranslations(), Collections.emptyList(),
            Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    public static List<TagTranslation> getTagTranslations() {
        return Arrays.asList(
            TagTranslation.builder().id(1L).name("Новини").language(Language.builder().id(2L).code("ua").build())
                .build(),
            TagTranslation.builder().id(2L).name("News").language(Language.builder().id(1L).code("en").build())
                .build());
    }

    public static List<TagTranslation> getHabitTagTranslations() {
        return Arrays.asList(
            TagTranslation.builder().id(1L).name("Багаторазове використання")
                .language(Language.builder().id(2L).code("ua").build())
                .build(),
            TagTranslation.builder().id(2L).name("Reusable").language(Language.builder().id(1L).code("en").build())
                .build());
    }

    public static List<TagTranslation> getEventTagTranslations() {
        Language language = getLanguage();
        return Arrays.asList(
            TagTranslation.builder().id(1L).name("Соціальний").language(getLanguageUa()).build(),
            TagTranslation.builder().id(2L).name("Social").language(language).build(),
            TagTranslation.builder().id(3L).name("Соціальний").language(language).build());
    }

    public static TagDto getTagDto() {
        return TagDto.builder().id(2L).name("News").build();
    }

    public static List<Tag> getTags() {
        return Collections.singletonList(getTag());
    }

    public static List<Tag> getHabitsTags() {
        return Collections.singletonList(getHabitTag());
    }

    public static List<Tag> getEventTags() {
        return Collections.singletonList(getEventTag());
    }

    public static User getUser() {
        return User.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .lastActivityTime(localDateTime)
            .verifyEmail(new VerifyEmail())
            .dateOfRegistration(localDateTime)
            .subscribedEvents(new HashSet<>())
            .favoriteEcoNews(new HashSet<>())
            .favoriteEvents(new HashSet<>())
            .language(getLanguage())
            .build();
    }

    public static User getAttenderUser() {
        return User.builder()
            .id(2L)
            .email("danylo@gmail.com")
            .name("Danylo")
            .role(Role.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .lastActivityTime(localDateTime)
            .verifyEmail(new VerifyEmail())
            .dateOfRegistration(localDateTime)
            .build();
    }

    public static UserVO getTestUserVo() {
        return UserVO.builder()
            .id(2L)
            .role(Role.ROLE_USER)
            .email("user@email.com")
            .build();
    }

    public static User getTestUser() {
        return User.builder()
            .id(2L)
            .role(Role.ROLE_USER)
            .email("user@email.com")
            .build();
    }

    public static List<User> getFriendsList() {
        User friend1 = User.builder()
            .id(10L)
            .rating(10.0)
            .build();
        User friend2 = User.builder()
            .id(2L)
            .rating(20.0)
            .build();
        User friend3 = User.builder()
            .id(3L)
            .rating(30.0)
            .build();
        User friend4 = User.builder()
            .id(4L)
            .rating(40.0)
            .build();
        User friend5 = User.builder()
            .id(5L)
            .rating(50.0)
            .build();
        User friend6 = User.builder()
            .id(6L)
            .rating(60.0)
            .build();
        User friend7 = User.builder()
            .id(7L)
            .rating(70.0)
            .build();
        User friend8 = User.builder()
            .id(8L)
            .rating(80.0)
            .build();
        return List.of(friend1, friend2, friend3, friend4, friend5, friend6, friend7, friend8);
    }

    public static UserVO getUserVO() {
        return UserVO.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .lastActivityTime(localDateTime)
            .verifyEmail(new VerifyEmailVO())
            .dateOfRegistration(localDateTime)
            .languageVO(getLanguageVO())
            .build();
    }

    public static UserVO getAuthorVO() {
        return UserVO.builder()
            .id(2L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .lastActivityTime(localDateTime)
            .verifyEmail(new VerifyEmailVO())
            .dateOfRegistration(localDateTime)
            .languageVO(getLanguageVO())
            .build();
    }

    public static UserManagementVO getUserManagementVO() {
        return UserManagementVO.builder()
            .id(1L)
            .name(TestConst.NAME)
            .email(TestConst.EMAIL)
            .userStatus(ACTIVATED)
            .role(Role.ROLE_USER).build();
    }

    public static UserVO getUserVOWithData() {
        return UserVO.builder()
            .id(13L)
            .name("user")
            .email("namesurname1995@gmail.com")
            .role(Role.ROLE_USER)
            .userCredo("save the world")
            .firstName("name")
            .emailNotification(EmailNotification.MONTHLY)
            .userStatus(ACTIVATED)
            .rating(13.4)
            .verifyEmail(VerifyEmailVO.builder()
                .id(32L)
                .user(UserVO.builder()
                    .id(13L)
                    .name("user")
                    .build())
                .token("toooookkkeeeeen42324532542")
                .build())
            .userFriends(Collections.singletonList(
                UserVO.builder()
                    .id(75L)
                    .name("Andrew")
                    .build()))
            .refreshTokenKey("refreshtoooookkkeeeeen42324532542")
            .ownSecurity(null)
            .dateOfRegistration(LocalDateTime.of(2020, 6, 6, 13, 47))
            .userLocationDto(
                new UserLocationDto(1L, "Lviv", "Львів", "Lvivska",
                    "Львівська", "Ukraine", "Україна", 20.000000, 20.000000))
            .showShoppingList(true)
            .showEcoPlace(true)
            .showLocation(true)
            .socialNetworks(Collections.singletonList(
                SocialNetworkVO.builder()
                    .id(10L)
                    .user(UserVO.builder()
                        .id(13L)
                        .email("namesurname1995@gmail.com")
                        .build())
                    .url("www.network.com")
                    .socialNetworkImage(SocialNetworkImageVO.builder()
                        .id(25L)
                        .hostPath("path///")
                        .imagePath("imagepath///")
                        .build())
                    .build()))
            .ownSecurity(OwnSecurityVO.builder()
                .id(1L)
                .password("password")
                .user(UserVO.builder()
                    .id(13L)
                    .build())
                .build())
            .lastActivityTime(LocalDateTime.of(2020, 12, 11, 13, 30))
            .userAchievements(List.of(
                UserAchievementVO.builder()
                    .id(47L)
                    .user(UserVO.builder()
                        .id(13L)
                        .build())
                    .achievement(AchievementVO.builder()
                        .id(56L)
                        .build())
                    .build(),
                UserAchievementVO.builder()
                    .id(39L)
                    .user(UserVO.builder()
                        .id(13L)
                        .build())
                    .achievement(AchievementVO.builder()
                        .id(14L)
                        .build())
                    .build()))
            .userActions(Collections.singletonList(UserActionVO.builder()
                .id(13L)
                .achievementCategory(AchievementCategoryVO.builder()
                    .id(1L)
                    .build())
                .count(0)
                .user(UserVO.builder()
                    .id(13L)
                    .build())
                .build()))
            .languageVO(LanguageVO.builder()
                .id(1L)
                .code("ua")
                .build())
            .build();
    }

    public static Language getLanguage() {
        return new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList());
    }

    public static Language getLanguageUa() {
        return new Language(2L, "ua", Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList());
    }

    public static EcoNews getEcoNews() {
        Tag tag = new Tag();
        tag.setTagTranslations(
            List.of(TagTranslation.builder().name("Новини").language(Language.builder().code("ua").build()).build(),
                TagTranslation.builder().name("News").language(Language.builder().code("en").build()).build()));
        return EcoNews.builder()
            .id(1L)
            .creationDate(zonedDateTime)
            .imagePath(TestConst.SITE)
            .source("source")
            .shortInfo("shortInfo")
            .author(getUser())
            .title("title")
            .text("text")
            .hidden(false)
            .followers(new HashSet<>())
            .tags(Collections.singletonList(tag))
            .build();
    }

    public static EcoNews getEcoNewsForMethodConvertTest() {
        Tag tag = new Tag();
        tag.setTagTranslations(
            List.of(TagTranslation.builder().name("Новини").language(Language.builder().code("ua").build()).build(),
                TagTranslation.builder().name("News").language(Language.builder().code("en").build()).build()));
        return new EcoNews(1L, ZonedDateTime.now(), TestConst.SITE, null, "shortInfo", getUser(),
            "title", "text", false, Collections.singletonList(tag), Collections.emptySet(),
            Collections.emptySet(), Collections.emptySet());
    }

    public static EcoNews getEcoNewsForFindDtoByIdAndLanguage() {
        return new EcoNews(1L, null, TestConst.SITE, null, "shortInfo", getUser(),
            "title", "text", false, Collections.singletonList(getTag()), Collections.emptySet(),
            Collections.emptySet(), Collections.emptySet());
    }

    public static EcoNewsVO getEcoNewsVO() {
        return new EcoNewsVO(1L, zonedDateTime, TestConst.SITE, null, getUserVO(),
            "title", "text", Collections.emptySet(), Collections.singletonList(getTagVO()),
            Collections.emptySet());
    }

    public static ShoppingListItemTranslation getShoppingListItemTranslation() {
        return ShoppingListItemTranslation.builder()
            .id(2L)
            .language(
                new Language(2L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList()))
            .shoppingListItem(
                new ShoppingListItem(1L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
            .content("Buy a bamboo toothbrush")
            .build();
    }

    public static ShoppingListItemTranslation getShoppingListItemTranslations1() {
        return ShoppingListItemTranslation.builder()
            .id(1L)
            .language(
                new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList()))
            .shoppingListItem(
                new ShoppingListItem(1L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
            .content("Buy a bamboo toothbrush")
            .build();
    }

    public static ShoppingListItemWithStatusRequestDto getShoppingListItemWithStatusRequestDto() {
        return ShoppingListItemWithStatusRequestDto.builder()
            .id(1L)
            .status(ShoppingListItemStatus.ACTIVE)
            .build();
    }

    public static CustomShoppingListItemWithStatusSaveRequestDto getCustomShoppingListItemWithStatusSaveRequestDto() {
        return CustomShoppingListItemWithStatusSaveRequestDto.builder()
            .text("TEXT")
            .status(ShoppingListItemStatus.INPROGRESS)
            .build();
    }

    public static CustomShoppingListItemSaveRequestDto getCustomShoppingListItemSaveRequestDto() {
        return CustomShoppingListItemSaveRequestDto.builder().text("TEXT").build();
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
            .status(HabitAssignStatus.ACQUIRED)
            .createDateTime(ZonedDateTime.now())
            .habit(HabitDto.builder().id(1L).build())
            .userId(1L).build();
    }

    public static HabitAssignDto getHabitAssignDtoWithFriendsIds() {
        return HabitAssignDto.builder()
            .id(1L)
            .status(HabitAssignStatus.INPROGRESS)
            .createDateTime(ZonedDateTime.now())
            .friendsIdsTrackingHabit(List.of(1L, 2L))
            .habit(HabitDto.builder().id(1L).build())
            .userId(1L).build();
    }

    public static HabitAssign getHabitAssign() {
        return getHabitAssign(HabitAssignStatus.ACQUIRED);
    }

    public static HabitAssign getHabitAssign(HabitAssignStatus status) {
        return HabitAssign.builder()
            .id(1L)
            .status(status)
            .createDate(ZonedDateTime.now())
            .habit(Habit.builder()
                .id(1L)
                .image("")
                .userId(2L)
                .habitTranslations(Collections.singletonList(HabitTranslation.builder()
                    .id(1L)
                    .name("")
                    .description("")
                    .habitItem("")
                    .language(getLanguage())
                    .build()))
                .build())
            .user(getUser())
            .userShoppingListItems(new ArrayList<>())
            .workingDays(0)
            .duration(0)
            .habitStreak(0)
            .habitStatistic(Collections.singletonList(getHabitStatistic()))
            .habitStatusCalendars(Collections.singletonList(getHabitStatusCalendar()))
            .lastEnrollmentDate(ZonedDateTime.now())
            .build();
    }

    public static HabitAssign getHabitAssignForCurrentUser() {
        return HabitAssign.builder()
            .id(1L)
            .status(HabitAssignStatus.ACQUIRED)
            .createDate(ZonedDateTime.of(2020, 12, 28,
                12, 12, 12, 12, ZoneId.of("Europe/Kiev")))
            .habit(Habit.builder()
                .id(1L)
                .image("")
                .userId(2L)
                .habitTranslations(Collections.singletonList(HabitTranslation.builder()
                    .id(1L)
                    .name("")
                    .description("")
                    .habitItem("")
                    .language(getLanguage())
                    .build()))
                .build())
            .user(getUser())
            .userShoppingListItems(new ArrayList<>())
            .workingDays(0)
            .duration(3)
            .habitStreak(0)
            .habitStatistic(Collections.singletonList(getHabitStatistic()))
            .habitStatusCalendars(Collections.singletonList(HabitStatusCalendar
                .builder().enrollDate(LocalDate.of(2020, 12, 28)).build()))
            .lastEnrollmentDate(ZonedDateTime.now())
            .build();
    }

    public static HabitAssign getAdditionalHabitAssignForCurrentUser() {
        return HabitAssign.builder()
            .id(2L)
            .status(HabitAssignStatus.ACQUIRED)
            .createDate(ZonedDateTime.of(2020, 12, 28,
                12, 12, 12, 12, ZoneId.of("Europe/Kiev")))
            .habit(Habit.builder()
                .id(2L)
                .image("")
                .userId(2L)
                .habitTranslations(Collections.singletonList(HabitTranslation.builder()
                    .id(1L)
                    .name("")
                    .description("")
                    .habitItem("")
                    .language(getLanguage())
                    .build()))
                .build())
            .user(getUser())
            .userShoppingListItems(new ArrayList<>())
            .workingDays(0)
            .duration(3)
            .habitStreak(0)
            .habitStatistic(Collections.singletonList(getHabitStatistic()))
            .habitStatusCalendars(Collections.emptyList())
            .lastEnrollmentDate(ZonedDateTime.now())
            .build();
    }

    public static List<HabitsDateEnrollmentDto> getHabitsDateEnrollmentDtos() {
        return Arrays.asList(
            HabitsDateEnrollmentDto
                .builder()
                .enrollDate(LocalDate.of(2020, 12, 27))
                .habitAssigns(Collections.emptyList())
                .build(),

            HabitsDateEnrollmentDto
                .builder()
                .enrollDate(LocalDate.of(2020, 12, 28))
                .habitAssigns(Arrays.asList(
                    new HabitEnrollDto(1L, "", "", true),
                    new HabitEnrollDto(2L, "", "", false)))
                .build(),

            HabitsDateEnrollmentDto
                .builder()
                .enrollDate(LocalDate.of(2020, 12, 29))
                .habitAssigns(Arrays.asList(
                    new HabitEnrollDto(1L, "", "", false),
                    new HabitEnrollDto(2L, "", "", false)))
                .build());
    }

    public static HabitAssign getFullHabitAssign() {
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
            .userShoppingListItems(getUserShoppingListItemList())
            .workingDays(0)
            .duration(0)
            .habitStreak(0)
            .habitStatistic(Collections.singletonList(getHabitStatistic()))
            .habitStatusCalendars(Collections.singletonList(getHabitStatusCalendar()))
            .lastEnrollmentDate(ZonedDateTime.now())
            .build();
    }

    public static HabitAssignVO getHabitAssignVO() {
        return HabitAssignVO.builder()
            .id(1L)
            .habitVO(getHabitVO())
            .status(HabitAssignStatus.ACQUIRED)
            .createDateTime(ZonedDateTime.now())
            .userVO(UserVO.builder().id(1L).build()).build();
    }

    public static HabitStatistic getHabitStatistic() {
        return HabitStatistic.builder()
            .id(1L).habitRate(HabitRate.GOOD).createDate(ZonedDateTime.now())
            .amountOfItems(10).build();
    }

    public static HabitVO getHabitVO() {
        return HabitVO.builder().id(1L).image("img.png").build();
    }

    public static UserShoppingListItem getCustomUserShoppingListItem() {
        return UserShoppingListItem.builder()
            .id(1L)
            .habitAssign(HabitAssign.builder().id(1L).build())
            .status(ShoppingListItemStatus.DONE)
            .build();
    }

    public static UserShoppingListItem getFullUserShoppingListItem() {
        return UserShoppingListItem.builder()
            .id(1L)
            .shoppingListItem(getShoppingListItem())
            .habitAssign(HabitAssign.builder().id(1L).build())
            .status(ShoppingListItemStatus.DONE)
            .build();
    }

    public static List<UserShoppingListItem> getUserShoppingListItemList() {
        List<UserShoppingListItem> getUserShoppingListItemList = new ArrayList<>();
        getUserShoppingListItemList.add(getFullUserShoppingListItem());
        getUserShoppingListItemList.add(getFullUserShoppingListItem());
        getUserShoppingListItemList.add(getFullUserShoppingListItem());

        return getUserShoppingListItemList;
    }

    public static List<ShoppingListItemTranslation> getShoppingListItemTranslationList() {
        ShoppingListItemTranslation translation = getShoppingListItemTranslations1();
        ShoppingListItemTranslation translation2 = getShoppingListItemTranslations1();
        ShoppingListItemTranslation translation3 = getShoppingListItemTranslations1();
        List<ShoppingListItemTranslation> list = new ArrayList<>();
        list.add(translation);
        list.add(translation2);
        list.add(translation3);
        return list;
    }

    public static UserShoppingListItemResponseDto getUserShoppingListItemResponseDto() {
        return UserShoppingListItemResponseDto.builder()
            .id(1L)
            .text("Buy electric car")
            .status(ShoppingListItemStatus.ACTIVE)
            .build();
    }

    public static UserShoppingListItem getPredefinedUserShoppingListItem() {
        return UserShoppingListItem.builder()
            .id(2L)
            .habitAssign(HabitAssign.builder().id(1L).build())
            .status(ShoppingListItemStatus.ACTIVE)
            .shoppingListItem(ShoppingListItem.builder().id(1L).userShoppingListItems(Collections.emptyList())
                .translations(
                    getShoppingListItemTranslations())
                .build())
            .build();
    }

    public static UserShoppingListItemVO getUserShoppingListItemVO() {
        return UserShoppingListItemVO.builder()
            .id(1L)
            .habitAssign(HabitAssignVO.builder()
                .id(1L)
                .build())
            .status(ShoppingListItemStatus.DONE)
            .build();
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

    public static List<ShoppingListItemTranslation> getShoppingListItemTranslations() {
        return Arrays.asList(
            ShoppingListItemTranslation.builder()
                .id(2L)
                .language(new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(),
                    Collections.emptyList(), Collections.emptyList()))
                .content("Buy a bamboo toothbrush")
                .shoppingListItem(
                    new ShoppingListItem(1L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
                .build(),
            ShoppingListItemTranslation.builder()
                .id(11L)
                .language(new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(),
                    Collections.emptyList(), Collections.emptyList()))
                .content("Start recycling batteries")
                .shoppingListItem(
                    new ShoppingListItem(4L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
                .build());
    }

    public static FactOfTheDay getFactOfTheDay() {
        return new FactOfTheDay(1L, "Fact of the day",
            List.of(ModelUtils.getFactOfTheDayTranslation(), FactOfTheDayTranslation.builder()
                .id(2L)
                .content("Контент")
                .language(new Language(2L, "ua", Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList()))
                .factOfTheDay(null)
                .build()),
            ZonedDateTime.now(),
            Collections.emptySet());
    }

    public static FactOfTheDayDTO getFactOfTheDayDto() {
        return new FactOfTheDayDTO(1L, "name", null, ZonedDateTime.now(), Collections.emptySet());
    }

    public static FactOfTheDayPostDTO getFactOfTheDayPostDto() {
        return new FactOfTheDayPostDTO(1L, "name",
            Collections.singletonList(getFactOfTheDayTranslationEmbeddedPostDTO()),
            Collections.singletonList(25L));
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
        place.setLocation(new Location(1L, 49.84988, 24.022533, "вулиця Під Дубом, 7Б", "test", place));
        place.setId(1L);
        place.setName("Forum");
        place.setDescription("Shopping center");
        place.setPhone("0322 489 850");
        place.setEmail("forum_lviv@gmail.com");
        place.setAuthor(getUser());
        place.setModifiedDate(ZonedDateTime.now());
        place.setStatus(PlaceStatus.PROPOSED);
        place.setAuthor(getUser());
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
        placeVO.setAuthor(getAuthorVO());
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

    public static LanguageTranslationDTO getLanguageTranslationDTO() {
        return new LanguageTranslationDTO(getLanguageDTO(), "content");
    }

    public static LanguageDTO getLanguageDTO() {
        return new LanguageDTO(1L, "en");
    }

    public static AddEcoNewsDtoRequest getAddEcoNewsDtoRequest() {
        return new AddEcoNewsDtoRequest("title", "text",
            Collections.singletonList("News"), "source", "shortInfo");
    }

    public static AddEcoNewsDtoResponse getAddEcoNewsDtoResponse() {
        return new AddEcoNewsDtoResponse(1L, "title",
            "text", "shortInfo", EcoNewsAuthorDto.builder().id(1L).name(TestConst.NAME).build(),
            ZonedDateTime.now(), TestConst.SITE, "source",
            Arrays.asList("Новини", "News"));
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
        return URI.create(TestConst.SITE).toURL();
    }

    public static EcoNewsAuthorDto getEcoNewsAuthorDto() {
        return new EcoNewsAuthorDto(1L, TestConst.NAME);
    }

    public static FactOfTheDayTranslationDTO getFactOfTheDayTranslationDTO() {
        return new FactOfTheDayTranslationDTO(1L, List.of(getFactOfTheDayTranslationEmbeddedPostDTO()));
    }

    public static FactOfTheDayTranslationEmbeddedPostDTO getFactOfTheDayTranslationEmbeddedPostDTO() {
        return FactOfTheDayTranslationEmbeddedPostDTO.builder()
            .content("content")
            .languageCode(AppConstant.DEFAULT_LANGUAGE_CODE)
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
        return greencity.entity.Photo.builder()
            .id(1L)
            .user(getUser()).id(1L)
            .place(getPlace()).id(1L)
            .comment(getPlaceComment()).id(1L)
            .name("photo")
            .build();
    }

    public static DiscountValue getDiscountValue() {
        return new DiscountValue(null, 33, null, null);
    }

    public static DiscountValueDto getDiscountValueDto() {
        return new DiscountValueDto(33, null);
    }

    public static List<TagTranslationVO> getTagTranslationsVO() {
        return Arrays.asList(TagTranslationVO.builder().id(1L).name("Новини")
            .languageVO(LanguageVO.builder().id(1L).code("ua").build()).build(),
            TagTranslationVO.builder().id(2L).name("News").languageVO(LanguageVO.builder().id(2L).code("en").build())
                .build());
    }

    public static LanguageVO getLanguageVO() {
        return new LanguageVO(1L, AppConstant.DEFAULT_LANGUAGE_CODE);
    }

    public static TagVO getTagVO() {
        return new TagVO(1L, TagType.ECO_NEWS, getTagTranslationsVO(), null, null, null);
    }

    public static TagPostDto getTagPostDto() {
        return new TagPostDto(TagType.ECO_NEWS, getTagTranslationDtos());
    }

    public static List<TagTranslationDto> getTagTranslationDtos() {
        return Arrays.asList(
            TagTranslationDto.builder().name("Новини")
                .language(LanguageDTO.builder().id(2L).code("ua").build()).build(),
            TagTranslationDto.builder().name("News")
                .language(LanguageDTO.builder().id(1L).code("en").build()).build());
    }

    public static TagViewDto getTagViewDto() {
        return new TagViewDto("3", "ECO_NEWS", "News");
    }

    public static PageableAdvancedDto<TagVO> getPageableAdvancedDtoForTag() {
        return new PageableAdvancedDto<>(Collections.singletonList(getTagVO()),
            9, 1, 2, 1,
            true, false, false, true);
    }

    public static Specification getSpecification() {
        return Specification.builder()
            .id(1L)
            .name("specification")
            .build();
    }

    public static List<SpecificationVO> getListSpecificationVO() {
        List<SpecificationVO> listOfSpecificationVO = new ArrayList<>();
        listOfSpecificationVO.add(SpecificationVO.builder().id(1L).name("Animal").build());
        return listOfSpecificationVO;
    }

    public static List<SocialNetworkVO> getListSocialNetworkVO() {
        List<SocialNetworkVO> socialNetworkVO = new ArrayList<>();
        socialNetworkVO.add(SocialNetworkVO.builder().id(1L).url("url").build());
        return socialNetworkVO;
    }

    public static PlaceByBoundsDto getPlaceByBoundsDtoForFindAllTest() {
        return PlaceByBoundsDto.builder()
            .id(1L)
            .name("Ekotel")
            .location(LocationDto.builder()
                .id(12L)
                .lat(49.80)
                .lng(24.03)
                .address("Some adress")
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

    public static PlaceComment getPlaceComment() {
        return new PlaceComment(1L, "text", getUser(),
            getPlace(), null, null, Collections.emptyList(), null, null, null);
    }

    public static PlaceCommentResponseDto getCommentReturnDto() {
        return new PlaceCommentResponseDto(1L, "text", null, null, null);
    }

    public static PlaceCommentRequestDto getAddCommentDto() {
        return new PlaceCommentRequestDto("comment", null, null);
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
            .addressUa("test")
            .lng(12.12d)
            .lat(12.12d)
            .build();
    }

    public static List<LanguageTranslationDTO> getLanguageTranslationsDTOs() {
        return Arrays.asList(
            new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "hello"),
            new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "text"),
            new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "smile"));
    }

    public static Habit getHabit() {
        Habit habit = Habit.builder().id(1L).image("image.png")
            .usersLiked(new HashSet<>())
            .complexity(1).tags(new HashSet<>(getTags())).build();

        return habit.setHabitTranslations(List.of(getHabitTranslation(habit)));
    }

    public static Habit getHabitWithCustom() {
        return Habit.builder().id(1L).image("image.png")
            .complexity(1).isCustomHabit(true).tags(new HashSet<>(getHabitsTags())).build();
    }

    public static HabitTranslation getHabitTranslation() {
        return HabitTranslation.builder()
            .id(1L)
            .description("test description")
            .habitItem("test habit item")
            .language(getLanguage())
            .name("test name")
            .habit(getHabit())
            .build();
    }

    public static HabitTranslation getHabitTranslation(Habit habit) {
        return HabitTranslation.builder()
            .id(1L)
            .description("test description")
            .habitItem("test habit item")
            .language(getLanguage())
            .name("test name")
            .habit(habit)
            .build();
    }

    public static HabitTranslation getHabitTranslationUa() {
        return HabitTranslation.builder()
            .id(1L)
            .description("тест")
            .habitItem("тест")
            .language(getLanguage())
            .name("тест")
            .habit(getHabit())
            .build();
    }

    public static HabitManagementDto gethabitManagementDto() {
        return HabitManagementDto.builder()
            .id(1L)
            .image("image")
            .habitTranslations(null)
            .build();
    }

    public static HabitManagementDto getHabitManagementDtoWithTranslation() {
        return HabitManagementDto.builder()
            .id(1L)
            .image("https://example.com/sample-image.jpg")
            .complexity(2)
            .habitTranslations(getHabitTranslationManagementDtoList())
            .defaultDuration(21)
            .build();
    }

    public static List<HabitTranslationManagementDto> getHabitTranslationManagementDtoList() {
        return List.of(
            HabitTranslationManagementDto.builder()
                .id(1L)
                .name("Пийте воду")
                .habitItem("Вода бутильована")
                .description("Пийте не менше 8 склянок води щодня.")
                .languageCode("ua")
                .build(),
            HabitTranslationManagementDto.builder()
                .id(2L)
                .name("Drink Water")
                .habitItem("Water Bottle")
                .description("Drink at least 8 glasses of water daily.")
                .languageCode("en")
                .build());
    }

    public static List<HabitTranslation> getHabitTranslationList() {
        return List.of(
            HabitTranslation.builder()
                .id(1L)
                .name("Пийте воду")
                .habitItem("Вода бутильована")
                .description("Пийте не менше 8 склянок води щодня.")
                .language(getLanguage())
                .build(),
            HabitTranslation.builder()
                .id(2L)
                .name("Drink Water")
                .habitItem("Water Bottle")
                .description("Drink at least 8 glasses of water daily.")
                .language(getLanguage())
                .build());
    }

    public static HabitManagementDto getHabitManagementDtoWithoutImage() {
        return HabitManagementDto.builder().id(1L)
            .image(null)
            .habitTranslations(List.of(
                HabitTranslationManagementDto.builder().habitItem("Item").description("Description").languageCode("en")
                    .name("Name").build()))
            .build();
    }

    public static HabitManagementDto getHabitManagementDtoWithDefaultImage() {
        return HabitManagementDto.builder().id(1L)
            .image(AppConstant.DEFAULT_HABIT_IMAGE)
            .habitTranslations(List.of(
                HabitTranslationManagementDto.builder().habitItem("Item").description("Description").languageCode("en")
                    .name("Name").build()))
            .build();
    }

    public static Habit getHabitWithDefaultImage() {
        return Habit.builder()
            .image(AppConstant.DEFAULT_HABIT_IMAGE)
            .build();
    }

    public static Achievement getAchievement() {
        return new Achievement(1L,
            "ACQUIRED_HABIT_14_DAYS", "Набуття звички протягом 14 днів", "Acquired habit 14 days",
            Collections.emptyList(),
            new AchievementCategory(1L, "CREATE_NEWS", "Створи Еко Новини", "Create Eco News", new ArrayList<>()), 1);
    }

    public static AchievementCategory getAchievementCategory() {
        return new AchievementCategory(1L, "HABIT", "Набудь Звички", "Acquire Habits", Collections.emptyList());
    }

    public static AchievementVO getAchievementVO() {
        return new AchievementVO(1L, "ACQUIRED_HABIT_14_DAYS", "Набуття звички протягом 14 днів",
            "Acquired habit 14 days", new AchievementCategoryVO(), null,
            1, 0);
    }

    public static AchievementPostDto getAchievementPostDto() {
        return new AchievementPostDto("ACQUIRED_HABIT_14_DAYS", "Набуття звички протягом 14 днів",
            "Acquired habit 14 days", getAchievementCategoryDto(),
            1);
    }

    public static AchievementCategoryDto getAchievementCategoryDto() {
        return new AchievementCategoryDto("Test");
    }

    public static AchievementCategoryVO getAchievementCategoryVO() {
        return new AchievementCategoryVO(1L, "Category");
    }

    public static AchievementCategoryTranslationDto getAchievementCategoryTranslationDto() {
        return new AchievementCategoryTranslationDto(1L, "Назва", "Title", null, null);
    }

    public static AchievementManagementDto getAchievementManagementDto() {
        return AchievementManagementDto.builder()
            .id(1L)
            .title("ACQUIRED_HABIT_14_DAYS")
            .name("Набуття звички протягом 14 днів")
            .nameEng("Acquired habit 14 days")
            .achievementCategory(getAchievementCategoryDto())
            .condition(1)
            .build();
    }

    public static UserAchievement getUserAchievement() {
        return new UserAchievement(1L, getUser(), getAchievement(), false, getHabit());
    }

    public static UserAction getUserAction() {
        return new UserAction(1L, ModelUtils.getUser(), ModelUtils.getAchievementCategory(), 0, getHabit());
    }

    public static UserActionVO getUserActionVO() {
        return new UserActionVO(1L, ModelUtils.getUserVO(), ModelUtils.getAchievementCategoryVO(), 0);
    }

    public static EcoNewsDto getEcoNewsDto() {
        return new EcoNewsDto(ZonedDateTime.now(), "imagePath", 1L, "title", "content", "text",
            getEcoNewsAuthorDto(), Collections.singletonList("tag"), Collections.singletonList("тег"), 1, 0, 0, false);
    }

    public static EcoNewsGenericDto getEcoNewsGenericDto() {
        String[] tagsEn = {"News"};
        String[] tagsUa = {"Новини"};
        return new EcoNewsGenericDto(1L, "title", "text", "shortInfo",
            ModelUtils.getEcoNewsAuthorDto(), zonedDateTime, "https://google.com/", "source",
            List.of(tagsUa), List.of(tagsEn), 0, 1, 0);
    }

    public static ShortEcoNewsDto getShortEcoNewsDto() {
        return ShortEcoNewsDto.builder()
            .text("content")
            .title("title")
            .imagePath("imagePath")
            .ecoNewsId(1L)
            .build();
    }

    public static EcoNewsDto getEcoNewsDtoForFindDtoByIdAndLanguage() {
        return new EcoNewsDto(null, TestConst.SITE, 1L, "title", "text", "shortInfo",
            getEcoNewsAuthorDto(), Collections.singletonList("News"), Collections.singletonList("Новини"), 0, 0, 0,
            false);
    }

    public static UpdateEcoNewsDto getUpdateEcoNewsDto() {
        return new UpdateEcoNewsDto(1L, "title", "text", "shortInfo", Collections.singletonList("tag"), "source");
    }

    public static SearchNewsDto getSearchNewsDto() {
        return new SearchNewsDto(1L, "title", getEcoNewsAuthorDto(), ZonedDateTime.now(),
            Collections.singletonList("tag"));
    }

    public static EcoNewsDtoManagement getEcoNewsDtoManagement() {
        return new EcoNewsDtoManagement(1L, "title", "text", ZonedDateTime.now(),
            Collections.singletonList("tag"), "imagePath", "source");
    }

    public static EcoNewsViewDto getEcoNewsViewDto() {
        return new EcoNewsViewDto("1", "title", "author", "text", "startDate",
            "endDate", "tag", "true");
    }

    public static HabitDto getHabitDto() {
        return HabitDto.builder()
            .id(1L)
            .image("image")
            .isCustomHabit(true)
            .habitTranslation(new HabitTranslationDto())
            .defaultDuration(1)
            .tags(new ArrayList<>())
            .habitAssignStatus(HabitAssignStatus.INPROGRESS)
            .isAssigned(true)
            .build();
    }

    public static ShoppingListItem getShoppingListItem() {
        return ShoppingListItem.builder()
            .id(1L)
            .translations(getShoppingListItemTranslations())
            .build();
    }

    public static HabitAssignPropertiesDto getHabitAssignPropertiesDto() {
        return HabitAssignPropertiesDto.builder()
            .defaultShoppingListItems(List.of(1L))
            .duration(20)
            .build();
    }

    public static HabitAssign getHabitAssignWithUserShoppingListItem() {
        return HabitAssign.builder()
            .id(1L)
            .user(User.builder().id(21L).build())
            .habit(Habit.builder().id(1L).build())
            .status(HabitAssignStatus.INPROGRESS)
            .workingDays(0)
            .duration(20)
            .userShoppingListItems(List.of(UserShoppingListItem.builder()
                .id(1L)
                .shoppingListItem(ShoppingListItem.builder().id(1L).build())
                .status(ShoppingListItemStatus.INPROGRESS)
                .build()))
            .build();
    }

    public static HabitAssignUserDurationDto getHabitAssignUserDurationDto() {
        return HabitAssignUserDurationDto.builder()
            .habitAssignId(1L)
            .userId(21L)
            .habitId(1L)
            .status(HabitAssignStatus.INPROGRESS)
            .workingDays(0)
            .duration(20)
            .build();
    }

    public static HabitAssignDto getFullHabitAssignDto() {
        return HabitAssignDto.builder()
            .id(1L)
            .status(HabitAssignStatus.ACQUIRED)
            .createDateTime(ZonedDateTime.of(1, 1, 1, 1, 1, 1, 1, ZoneOffset.systemDefault()))
            .habit(HabitDto.builder().id(1L).build())
            .userId(1L)
            .duration(null)
            .userShoppingListItems(List.of(UserShoppingListItemAdvanceDto.builder()
                .id(1L)
                .shoppingListItemId(1L)
                .status(ShoppingListItemStatus.INPROGRESS)
                .build()))
            .habitStatusCalendarDtoList(List.of(getHabitStatusCalendarDto()))
            .habitStreak(1)
            .lastEnrollmentDate(ZonedDateTime.of(1, 1, 1, 1, 1, 1, 1, ZoneOffset.systemDefault()))
            .workingDays(1)
            .build();
    }

    public static HabitAssign getHabitAssignForMapper() {
        return HabitAssign.builder()
            .id(1L)
            .status(HabitAssignStatus.ACQUIRED)
            .createDate(ZonedDateTime.of(1, 1, 1, 1, 1, 1, 1, ZoneOffset.systemDefault()))
            .habit(Habit.builder()
                .id(1L)
                .image(null)
                .habitTranslations(null)
                .build())
            .user(null)
            .userShoppingListItems(List.of(UserShoppingListItem.builder()
                .id(1L)
                .habitAssign(null)
                .shoppingListItem(ShoppingListItem.builder()
                    .id(1L)
                    .habits(null)
                    .translations(null)
                    .build())
                .build()))
            .workingDays(1)
            .duration(null)
            .habitStreak(1)
            .habitStatistic(null)
            .habitStatusCalendars(null)
            .lastEnrollmentDate(ZonedDateTime.of(1, 1, 1, 1, 1, 1, 1, ZoneOffset.systemDefault()))
            .build();
    }

    private static UserStatusDto createUserStatusDto() {
        return UserStatusDto.builder()
            .id(2L)
            .userStatus(UserStatus.CREATED)
            .build();
    }

    private static User createUserRoleUser() {
        return User.builder()
            .id(2L)
            .role(Role.ROLE_USER)
            .email("test2@mail.com")
            .build();
    }

    private static UserVO createUserVORoleUser() {
        return UserVO.builder()
            .id(2L)
            .role(Role.ROLE_USER)
            .email("test2@mail.com")
            .build();
    }

    private static User createUser() {
        return User.builder()
            .id(1L)
            .role(Role.ROLE_MODERATOR)
            .email("test@mail.com")
            .build();
    }

    private static UserVO createUserVO() {
        return UserVO.builder()
            .id(1L)
            .role(Role.ROLE_MODERATOR)
            .email("test@mail.com")
            .build();
    }

    private static HabitAssign createHabitAssignInProgress() {
        return HabitAssign.builder()
            .habit(Habit.builder().id(1L).build())
            .status(HabitAssignStatus.INPROGRESS)
            .build();
    }

    public static CustomShoppingListItemResponseDto getCustomShoppingListItemResponseDto() {
        return CustomShoppingListItemResponseDto.builder()
            .id(1L)
            .status(ShoppingListItemStatus.INPROGRESS)
            .text("TEXT")
            .build();
    }

    public static CustomShoppingListItem getCustomShoppingListItem() {
        return CustomShoppingListItem.builder()
            .id(1L)
            .status(ShoppingListItemStatus.INPROGRESS)
            .text("TEXT")
            .build();
    }

    public static Event getEvent() {
        Event event = new Event();
        Set<User> followers = new HashSet<>();
        followers.add(getUser());
        event.setOpen(true);
        event.setDescription("Description");
        event.setId(1L);
        event.setOrganizer(getUser());
        event.setFollowers(followers);
        event.setTitle("Title");
        event.setAttenders(new HashSet<>(Collections.singleton(getUser())));
        List<EventDateLocation> dates = new ArrayList<>();
        dates.add(new EventDateLocation(1L, event,
            ZonedDateTime.of(2022, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            ZonedDateTime.of(2022, 2, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            getKyivAddress(), null));
        dates.add(new EventDateLocation(2L, event,
            ZonedDateTime.of(2023, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            ZonedDateTime.of(2050, 11, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            getKyivAddress(), null));
        event.setDates(dates);
        event.setTags(List.of(getEventTag()));
        event.setTitleImage(AppConstant.DEFAULT_EVENT_IMAGES);
        return event;
    }

    public static HashSet<User> getUsersHashSet() {
        return new HashSet<>(Arrays.asList(
            User.builder().id(1L).build(),
            User.builder().id(2L).build()));
    }

    public static Event getCloseEvent() {
        Event event = new Event();
        event.setOpen(false);
        event.setDescription("Description");
        event.setId(1L);
        event.setOrganizer(getUser());
        event.setTitle("Title");
        List<EventDateLocation> dates = new ArrayList<>();
        dates.add(new EventDateLocation(1L, event,
            ZonedDateTime.of(2098, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            ZonedDateTime.of(2099, 2, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            getAddress(), null));
        dates.add(new EventDateLocation(2L, event,
            ZonedDateTime.of(2099, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            ZonedDateTime.of(2100, 2, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            getAddress(), null));
        event.setDates(dates);
        event.setTags(List.of(getEventTag()));
        event.setTitleImage(AppConstant.DEFAULT_HABIT_IMAGE);
        return event;
    }

    public static Event getEventWithFinishedDate() {
        Event event = new Event();
        event.setDescription("Description");
        event.setId(1L);
        event.setOrganizer(getUser());
        event.setTitle("Title");
        List<EventDateLocation> dates = new ArrayList<>();
        dates.add(new EventDateLocation(1L, event,
            ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            ZonedDateTime.of(2001, 2, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            getAddress(), null));
        event.setDates(dates);
        event.setTags(List.of(getEventTag()));
        event.setTitleImage(AppConstant.DEFAULT_HABIT_IMAGE);
        return event;
    }

    public static Event getEventWithoutAddress() {
        Event event = new Event();
        event.setDescription("Description");
        event.setId(1L);
        event.setOrganizer(getUser());
        event.setTitle("Title");
        List<EventDateLocation> dates = new ArrayList<>();
        dates.add(new EventDateLocation(1L, event,
            ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            ZonedDateTime.of(2000, 2, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            getAddress(), null));
        dates.add(new EventDateLocation(2L, event,
            ZonedDateTime.of(2002, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            ZonedDateTime.of(2002, 2, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            null, "url/"));
        event.setDates(dates);
        event.setTags(List.of(getEventTag()));
        return event;
    }

    public static MultipartFile getMultipartFile() {
        return new MockMultipartFile("firstFile.tmp", "Hello World".getBytes());
    }

    public static MultipartFile[] getMultipartFiles() {
        return new MultipartFile[] {new MockMultipartFile("firstFile.tmp", "Hello World".getBytes()),
            new MockMultipartFile("secondFile.tmp", "Hello World".getBytes())};
    }

    public static MultipartFile[] getMultipartImageFiles() {
        return new MockMultipartFile[] {
            new MockMultipartFile(
                "images", "image.jpg", "image/jpeg", "image data".getBytes()),
            new MockMultipartFile(
                "images", "image.jpg", "image/jpeg", "image data".getBytes())};
    }

    public static AddressDto getAddressDtoWithNullRegionUa() {
        return AddressDto.builder()
            .latitude(13.4567236)
            .longitude(98.2354469)
            .streetUa("Вулиця")
            .streetEn("Street")
            .houseNumber("1B")
            .cityUa("Місто")
            .cityEn("City")
            .regionUa(null)
            .regionEn("Oblast")
            .countryUa("Країна")
            .countryEn("Country")
            .build();
    }

    public static AddressDto getAddressDtoWithNullCountryUa() {
        return AddressDto.builder()
            .latitude(13.4567236)
            .longitude(98.2354469)
            .streetUa("Вулиця")
            .streetEn("Street")
            .houseNumber("1B")
            .cityUa("Місто")
            .cityEn("City")
            .regionUa("Область")
            .regionEn("Oblast")
            .countryUa(null)
            .countryEn("Country")
            .build();
    }

    public static AddressDto getAddressDtoCorrect() {
        return AddressDto.builder()
            .latitude(50.4567236)
            .longitude(30.2354469)
            .streetUa("Вулиця")
            .streetEn("Street")
            .houseNumber("1B")
            .cityUa("Київ")
            .cityEn("Kyiv")
            .regionUa("Область")
            .regionEn("Oblast")
            .countryUa("Країна")
            .countryEn("Country")
            .build();
    }

    public static AddressDto getAddressDtoWithoutData() {
        return AddressDto.builder().build();
    }

    public static EventDto getEventDtoWithoutAddress() {
        return EventDto.builder()
            .id(1L)
            .countComments(2)
            .likes(1)
            .description("Description")
            .organizer(EventAuthorDto.builder()
                .name("User")
                .id(1L)
                .build())
            .title("Title")
            .dates(List.of(EventDateLocationDto.builder()
                .id(1L)
                .event(null)
                .startDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
                .finishDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
                .onlineLink(null)
                .coordinates(null).build()))
            .tags(List.of(TagUaEnDto.builder().id(1L).nameEn("Social")
                .nameUa("Соціальний").build()))
            .build();
    }

    public static EventDto getEventDto() {
        return EventDto.builder()
            .id(1L)
            .countComments(2)
            .likes(1)
            .description("Description")
            .organizer(EventAuthorDto.builder()
                .name("User")
                .id(1L)
                .build())
            .title("Title")
            .dates(List.of(EventDateLocationDto.builder()
                .id(1L)
                .event(null)
                .startDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
                .finishDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
                .onlineLink("/url")
                .coordinates(getAddressDtoCorrect()).build()))
            .tags(List.of(TagUaEnDto.builder().id(1L).nameEn("Social")
                .nameUa("Соціальний").build()))
            .isFavorite(false)
            .isSubscribed(false)
            .build();
    }

    public static EventDto getEventWithoutAddressDto() {
        return EventDto.builder()
            .id(1L)
            .countComments(2)
            .likes(1)
            .description("Description")
            .organizer(EventAuthorDto.builder()
                .name("User")
                .id(1L)
                .build())
            .title("Title")
            .dates(List.of(EventDateLocationDto.builder()
                .id(1L)
                .event(null)
                .startDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
                .finishDate(ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()))
                .onlineLink("/url")
                .coordinates(AddressDto.builder().build()).build()))
            .tags(List.of(TagUaEnDto.builder().id(1L).nameEn("Social")
                .nameUa("Соціальний").build()))
            .build();
    }

    public static FilterPlaceCategory getFilterPlaceCategory() {
        return FilterPlaceCategory.builder()
            .name("category")
            .nameUa("Категорії")
            .build();
    }

    public static AddPlaceDto getAddPlaceDto() {
        return AddPlaceDto.builder()
            .categoryName("category")
            .placeName("test")
            .locationName("Test")
            .openingHoursList(Set.of(OpeningHoursDto.builder()
                .openTime(LocalTime.now())
                .closeTime(LocalTime.now())
                .weekDay(DayOfWeek.MONDAY)
                .build()))
            .build();
    }

    public static PlaceResponse getPlaceResponse() {
        return PlaceResponse.builder()
            .category(CategoryDto.builder()
                .name("category")
                .nameUa("Test")
                .build())
            .locationAddressAndGeoDto(AddPlaceLocation.builder()
                .lng(32.2)
                .lat(32.3)
                .addressEng("test")
                .address("test")
                .build())
            .openingHoursList(Set.of(OpeningHoursDto.builder()
                .weekDay(DayOfWeek.MONDAY)
                .openTime(LocalTime.now())
                .closeTime(LocalTime.now())
                .build()))
            .build();
    }

    public static List<GeocodingResult> getGeocodingResult() {
        List<GeocodingResult> geocodingResults = new ArrayList<>();

        GeocodingResult geocodingResult1 = new GeocodingResult();

        Geometry geometry = new Geometry();
        geometry.location = new LatLng(50.5555555d, 50.5555555d);

        AddressComponent locality = new AddressComponent();
        locality.longName = "fake city";
        locality.types = new AddressComponentType[] {AddressComponentType.LOCALITY};

        AddressComponent streetNumber = new AddressComponent();
        streetNumber.longName = "13";
        streetNumber.types = new AddressComponentType[] {AddressComponentType.STREET_NUMBER};

        AddressComponent region = new AddressComponent();
        region.longName = "fake region";
        region.types = new AddressComponentType[] {AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1};

        AddressComponent country = new AddressComponent();
        country.longName = "fake country";
        country.types = new AddressComponentType[] {AddressComponentType.COUNTRY};

        AddressComponent route = new AddressComponent();
        route.longName = "fake street name";
        route.types = new AddressComponentType[] {AddressComponentType.ROUTE};

        geocodingResult1.addressComponents = new AddressComponent[] {
            locality,
            streetNumber,
            region,
            country,
            route
        };

        geocodingResult1.geometry = geometry;

        GeocodingResult geocodingResult2 = new GeocodingResult();

        AddressComponent locality2 = new AddressComponent();
        locality2.longName = "fake city";
        locality2.types = new AddressComponentType[] {AddressComponentType.LOCALITY};

        AddressComponent streetNumber2 = new AddressComponent();
        streetNumber2.longName = "13";
        streetNumber2.types = new AddressComponentType[] {AddressComponentType.STREET_NUMBER};

        AddressComponent region2 = new AddressComponent();
        region2.longName = "fake region";
        region2.types = new AddressComponentType[] {AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1};

        AddressComponent country2 = new AddressComponent();
        country2.longName = "fake country";
        country2.types = new AddressComponentType[] {AddressComponentType.COUNTRY};

        AddressComponent route2 = new AddressComponent();
        route2.longName = "fake street name";
        route2.types = new AddressComponentType[] {AddressComponentType.ROUTE};

        geocodingResult2.addressComponents = new AddressComponent[] {
            locality2,
            streetNumber2,
            region2,
            country2,
            route2
        };

        geocodingResult2.geometry = geometry;

        geocodingResults.add(geocodingResult1);
        geocodingResults.add(geocodingResult2);

        return geocodingResults;
    }

    public static GeocodingResult[] getGeocodingResultUk() {
        GeocodingResult geocodingResult = new GeocodingResult();

        geocodingResult.formattedAddress = "Повна відформатована адреса";

        AddressComponent route = new AddressComponent();
        route.longName = "вулиця";
        route.types = new AddressComponentType[] {AddressComponentType.ROUTE};

        AddressComponent streetNumber = new AddressComponent();
        streetNumber.longName = "13";
        streetNumber.types = new AddressComponentType[] {AddressComponentType.STREET_NUMBER};

        AddressComponent locality = new AddressComponent();
        locality.longName = "місто";
        locality.types = new AddressComponentType[] {AddressComponentType.LOCALITY};

        AddressComponent region = new AddressComponent();
        region.longName = "область";
        region.types = new AddressComponentType[] {AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1};

        AddressComponent country = new AddressComponent();
        country.longName = "країна";
        country.types = new AddressComponentType[] {AddressComponentType.COUNTRY};

        geocodingResult.addressComponents = new AddressComponent[] {
            locality,
            streetNumber,
            region,
            country,
            route
        };

        return new GeocodingResult[] {geocodingResult};
    }

    public static GeocodingResult[] getGeocodingResultEn() {
        GeocodingResult geocodingResult = new GeocodingResult();

        geocodingResult.formattedAddress = "Full formatted address";

        AddressComponent route = new AddressComponent();
        route.longName = "fake street name";
        route.types = new AddressComponentType[] {AddressComponentType.ROUTE};

        AddressComponent streetNumber = new AddressComponent();
        streetNumber.longName = "13";
        streetNumber.types = new AddressComponentType[] {AddressComponentType.STREET_NUMBER};

        AddressComponent locality = new AddressComponent();
        locality.longName = "fake city";
        locality.types = new AddressComponentType[] {AddressComponentType.LOCALITY};

        AddressComponent region = new AddressComponent();
        region.longName = "fake region";
        region.types = new AddressComponentType[] {AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1};

        AddressComponent country = new AddressComponent();
        country.longName = "fake country";
        country.types = new AddressComponentType[] {AddressComponentType.COUNTRY};

        geocodingResult.addressComponents = new AddressComponent[] {
            locality,
            streetNumber,
            region,
            country,
            route
        };

        return new GeocodingResult[] {geocodingResult};
    }

    public static AddressLatLngResponse getAddressLatLngResponse() {
        return AddressLatLngResponse
            .builder()
            .latitude(51.1234567)
            .longitude(28.7654321)
            .addressEn(AddressResponse
                .builder()
                .street("fake street name")
                .houseNumber("13")
                .city("fake city")
                .region("fake region")
                .country("fake country")
                .formattedAddress("Full formatted address")
                .build())
            .addressUa(AddressResponse
                .builder()
                .street("вулиця")
                .houseNumber("13")
                .city("місто")
                .region("область")
                .country("країна")
                .formattedAddress("Повна відформатована адреса")
                .build())
            .build();
    }

    public static AddPlaceLocation getAddPlaceLocation() {
        return AddPlaceLocation.builder()
            .address("test")
            .addressEng("address")
            .lat(12.12)
            .lng(12.12)
            .build();
    }

    public static Event getExpectedEvent() {
        Event event = new Event();
        event.setDescription("Description");
        event.setId(1L);
        event.setOrganizer(getUser());
        event.setTitle("Title");
        List<EventDateLocation> dates = new ArrayList<>();
        dates.add(new EventDateLocation(1L, event,
            ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            ZonedDateTime.of(2000, 2, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            getAddress(), null));
        dates.add(getEventDateLocation());
        event.setDates(dates);
        event.setTags(List.of(getEventTag()));
        event.setTitleImage(AppConstant.DEFAULT_EVENT_IMAGES);
        return event;
    }

    public static UpdateEventDto getUpdateEventDto() {
        UpdateEventDto updateEventDto = new UpdateEventDto();
        updateEventDto.setId(1L);
        return updateEventDto;
    }

    public static UpdateEventRequestDto getUpdateEventRequestDto() {
        return UpdateEventRequestDto.builder()
            .id(1L)
            .build();
    }

    public static List<String> getUpdatedEventTags() {
        return List.of("Social");
    }

    public static List<TagUaEnDto> getUpdatedEventTagUaEn() {
        return List.of(TagUaEnDto.builder().nameEn("Social").nameUa("Сщціальний").build());
    }

    public static List<EventDateLocationDto> getUpdatedEventDateLocationDto() {
        return List.of(EventDateLocationDto.builder().startDate(ZonedDateTime.now().plusDays(1))
            .finishDate(ZonedDateTime.now().plusDays(1).plusHours(2))
            .coordinates(AddressDto.builder().latitude(1.).longitude(1.).build()).build());
    }

    public static List<EventDateLocationDto> getEventDateLocationDtoWithSameDateTime() {
        ZonedDateTime sameDateTime = ZonedDateTime.now().plusDays(1);
        return List.of(EventDateLocationDto.builder().startDate(sameDateTime)
            .finishDate(sameDateTime)
            .coordinates(AddressDto.builder().latitude(1.).longitude(1.).build()).build());
    }

    public static List<UpdateEventDateLocationDto> getUpdateEventDateLocationDto() {
        return List
            .of(UpdateEventDateLocationDto.builder().startDate(ZonedDateTime.now()).finishDate(ZonedDateTime.now())
                .coordinates(UpdateAddressDto.builder().latitude(1.).longitude(1.).build()).build());
    }

    public static EventDateLocation getUpdatedEventDateLocation() {
        return EventDateLocation.builder().startDate(ZonedDateTime.now()).finishDate(ZonedDateTime.now())
            .address(Address.builder().latitude(1D).longitude(1D).build()).build();
    }

    public static Event getEventWithGrades() {
        Event event = new Event();
        event.setDescription("Description");
        event.setId(1L);
        event.setOrganizer(getUser());
        event.setTitle("Title");
        List<EventDateLocation> dates = new ArrayList<>();
        dates.add(new EventDateLocation(1L, event,
            ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            ZonedDateTime.of(2000, 2, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            getAddress(), null));
        dates.add(new EventDateLocation(2L, event,
            ZonedDateTime.of(2002, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            ZonedDateTime.of(2002, 2, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            getAddress(), null));
        event.setDates(dates);
        event.setTags(List.of(getEventTag()));
        event.setTitleImage(AppConstant.DEFAULT_HABIT_IMAGE);
        event.setEventGrades(List.of(EventGrade.builder().grade(2).event(event).build()));
        return event;
    }

    public static Address getAddress() {
        return Address.builder()
            .latitude(13.4567236)
            .longitude(98.2354469)
            .streetUa("Вулиця")
            .streetEn("Street")
            .houseNumber("1B")
            .cityUa("Місто")
            .cityEn("City")
            .regionUa("Область")
            .regionEn("Oblast")
            .countryUa("Країна")
            .countryEn("Country")
            .formattedAddressEn("Full formatted address")
            .formattedAddressUa("Повна відформатована адреса")
            .build();
    }

    public static Address getKyivAddress() {
        return Address.builder()
            .latitude(50.4567236)
            .longitude(30.2354469)
            .streetUa("Вулиця")
            .streetEn("Street")
            .houseNumber("1B")
            .cityUa("Київ")
            .cityEn("Kyiv")
            .regionUa("Область")
            .regionEn("Oblast")
            .countryUa("Країна")
            .countryEn("Country")
            .formattedAddressEn("Full formatted address")
            .formattedAddressUa("Повна відформатована адреса")
            .build();
    }

    public static EventDateLocation getEventDateLocation() {
        return EventDateLocation.builder()
            .id(1L)
            .startDate(ZonedDateTime.now())
            .finishDate(ZonedDateTime.now().plusDays(2L))
            .onlineLink("https://events.com/1")
            .address(getAddress())
            .build();
    }

    public static AddressDto getAddressDto() {
        return AddressDto.builder()
            .latitude(13.4567236)
            .longitude(98.2354469)
            .streetUa("Вулиця")
            .streetEn("Street")
            .houseNumber("1B")
            .cityUa("Місто")
            .cityEn("City")
            .regionUa("Область")
            .regionEn("Oblast")
            .countryUa("Країна")
            .countryEn("Country")
            .formattedAddressEn("Full formatted address")
            .formattedAddressUa("Повна відформатована адреса")
            .countryEn("Country")
            .build();
    }

    public static Principal getPrincipal() {
        return () -> "danylo@gmail.com";
    }

    public static UserFilterDtoRequest getUserFilterDtoRequest() {
        return UserFilterDtoRequest.builder()
            .userRole("USER")
            .name("Test_Filter")
            .searchCriteria("Test")
            .userStatus("ACTIVATED")
            .build();
    }

    public static UserFilterDtoResponse getUserFilterDtoResponse() {
        return UserFilterDtoResponse.builder()
            .id(1L)
            .userRole("ADMIN")
            .searchCriteria("Test")
            .userStatus("ACTIVATED")
            .name("Test")
            .build();
    }

    public static Filter getFilter() {
        return Filter.builder()
            .id(1L)
            .name("Test")
            .user(new User())
            .type("USERS")
            .values("Test;ADMIN;ACTIVATED")
            .build();
    }

    public static EventVO getEventVO() {
        return EventVO.builder()
            .id(1L)
            .description("description")
            .organizer(getUserVO())
            .title("title")
            .titleImage("title image")
            .build();
    }

    public static Comment getComment() {
        return Comment.builder()
            .id(1L)
            .user(getUser())
            .articleType(ArticleType.HABIT)
            .articleId(10L)
            .text("text")
            .usersLiked(new HashSet<>())
            .createdDate(LocalDateTime.now())
            .user(getUser())
            .comments(List.of(getSubComment()))
            .status(CommentStatus.ORIGINAL)
            .build();
    }

    public static Comment getParentComment() {
        return Comment.builder()
            .id(1L)
            .articleType(ArticleType.HABIT)
            .articleId(10L)
            .text("text")
            .usersLiked(new HashSet<>())
            .createdDate(LocalDateTime.now())
            .user(getUser())
            .comments(List.of(getSubComment()))
            .status(CommentStatus.ORIGINAL)
            .build();
    }

    public static CommentVO getCommentVO() {
        return CommentVO.builder()
            .id(1L)
            .articleType("HABIT")
            .articleId(10L)
            .text("text")
            .usersLiked(new HashSet<>())
            .createdDate(LocalDateTime.now())
            .user(getUserVO())
            .status("ORIGINAL")
            .build();
    }

    public static Comment getSubComment() {
        return Comment.builder()
            .id(5L)
            .articleType(ArticleType.HABIT)
            .articleId(10L)
            .text("other text")
            .usersLiked(new HashSet<>())
            .createdDate(LocalDateTime.now())
            .user(getUser())
            .status(CommentStatus.ORIGINAL)
            .build();
    }

    public static CommentDto getCommentDto() {
        return CommentDto.builder()
            .id(1L)
            .text("text")
            .createdDate(LocalDateTime.now())
            .author(getCommentAuthorDto())
            .status(CommentStatus.ORIGINAL.toString())
            .build();
    }

    public static AddCommentDtoResponse getAddCommentDtoResponse() {
        return AddCommentDtoResponse.builder()
            .id(getComment().getId())
            .author(getCommentAuthorDto())
            .text(getComment().getText())
            .build();
    }

    public static CommentAuthorDto getCommentAuthorDto() {
        return CommentAuthorDto.builder()
            .id(getUser().getId())
            .name(getUser().getName().trim())
            .profilePicturePath(getUser().getProfilePicturePath())
            .build();
    }

    public static CommentImages getCommentImage() {
        return CommentImages.builder()
            .id(1L)
            .link("http://example.com/image1.jpg")
            .comment(getComment())
            .build();
    }

    public static AddCommentDtoRequest getAddCommentDtoRequest() {
        return new AddCommentDtoRequest("text", 10L);
    }

    public static UserShoppingAndCustomShoppingListsDto getUserShoppingAndCustomShoppingListsDto() {
        return UserShoppingAndCustomShoppingListsDto.builder()
            .userShoppingListItemDto(List.of(getUserShoppingListItemResponseDto()))
            .customShoppingListItemDto(List.of(getCustomShoppingListItemResponseDto()))
            .build();
    }

    public static CustomHabitDtoRequest getAddCustomHabitDtoRequest() {
        return CustomHabitDtoRequest.builder()
            .complexity(2)
            .defaultDuration(7)
            .build();

    }

    public static HabitTranslationDto getHabitTranslationDto() {
        return HabitTranslationDto.builder()
            .description(habitTranslationDescription)
            .habitItem(habitItem)
            .name(habitTranslationName)
            .build();
    }

    public static HabitTranslation getHabitTranslationForServiceTest() {
        return HabitTranslation.builder()
            .id(1L)
            .description(habitTranslationDescription)
            .habitItem(habitItem)
            .name(habitTranslationName)
            .habit(getCustomHabitForServiceTest())
            .build();
    }

    public static CustomHabitDtoRequest getAddCustomHabitDtoRequestForServiceTest() {
        return CustomHabitDtoRequest.builder()
            .complexity(2)
            .customShoppingListItemDto(List.of(
                CustomShoppingListItemResponseDto.builder()
                    .id(1L)
                    .status(ShoppingListItemStatus.ACTIVE)
                    .text(shoppingListText)
                    .build()))
            .defaultDuration(7)
            .habitTranslations(
                List.of(HabitTranslationDto.builder()
                    .description(habitTranslationDescription)
                    .habitItem(habitItem)
                    .languageCode("ua")
                    .name(habitTranslationName)
                    .build()))
            .tagIds(Set.of(20L))
            .build();
    }

    public static CustomHabitDtoRequest getCustomHabitDtoRequestWithNewCustomShoppingListItem() {
        return CustomHabitDtoRequest.builder()
            .customShoppingListItemDto(List.of(
                CustomShoppingListItemResponseDto.builder()
                    .id(null)
                    .status(ShoppingListItemStatus.ACTIVE)
                    .text(shoppingListText)
                    .build()))
            .tagIds(Set.of(20L))
            .build();
    }

    public static CustomShoppingListItem getCustomShoppingListItemForUpdate() {
        return CustomShoppingListItem.builder()
            .id(1L)
            .status(ShoppingListItemStatus.ACTIVE)
            .text(shoppingListText)
            .build();
    }

    public static CustomHabitDtoRequest getСustomHabitDtoRequestWithComplexityAndDuration() {
        return CustomHabitDtoRequest.builder()
            .complexity(2)
            .defaultDuration(7)
            .build();
    }

    public static CustomHabitDtoRequest getAddCustomHabitDtoRequestWithImage() {
        return CustomHabitDtoRequest.builder()
            .complexity(2)
            .customShoppingListItemDto(List.of(
                CustomShoppingListItemResponseDto.builder()
                    .id(1L)
                    .status(ShoppingListItemStatus.ACTIVE)
                    .text(shoppingListText)
                    .build()))
            .defaultDuration(7)
            .image(habitDefaultImage)
            .habitTranslations(
                List.of(HabitTranslationDto.builder()
                    .description(habitTranslationDescription)
                    .habitItem(habitItem)
                    .languageCode("ua")
                    .name(habitTranslationName)
                    .build()))
            .tagIds(Set.of(20L))
            .build();
    }

    public static CustomHabitDtoResponse getAddCustomHabitDtoResponse() {
        return CustomHabitDtoResponse.builder()
            .id(1L)
            .complexity(2)
            .customShoppingListItemDto(List.of(
                CustomShoppingListItemResponseDto.builder()
                    .id(1L)
                    .status(ShoppingListItemStatus.ACTIVE)
                    .text(shoppingListText)
                    .build()))
            .defaultDuration(7)
            .habitTranslations(
                List.of(HabitTranslationDto.builder()
                    .description(habitTranslationDescription)
                    .habitItem(habitItem)
                    .languageCode("ua")
                    .name(habitTranslationName)
                    .build(),

                    HabitTranslationDto.builder()
                        .description(habitTranslationDescription)
                        .habitItem(habitItem)
                        .languageCode("en")
                        .name(habitTranslationName)
                        .build()))
            .tagIds(Set.of(20L))
            .build();
    }

    public static CustomShoppingListItemResponseDto getCustomShoppingListItemResponseDtoForServiceTest() {
        return CustomShoppingListItemResponseDto.builder()
            .id(1L)
            .status(ShoppingListItemStatus.ACTIVE)
            .text(shoppingListText)
            .build();
    }

    public static CustomShoppingListItem getCustomShoppingListItemForServiceTest() {
        return CustomShoppingListItem.builder()
            .id(1L)
            .habit(getCustomHabitForServiceTest())
            .status(ShoppingListItemStatus.ACTIVE)
            .text(shoppingListText)
            .user(getUser())
            .build();
    }

    public static Tag getTagHabitForServiceTest() {
        return Tag.builder().id(1L).type(TagType.HABIT)
            .tagTranslations(List.of(TagTranslation.builder().id(20L).name("Reusable")
                .language(Language.builder().id(1L).code("en").build()).build()))
            .build();
    }

    public static Habit getCustomHabitForServiceTest() {
        return Habit.builder()
            .id(1L)
            .complexity(2)
            .defaultDuration(7)
            .isCustomHabit(true)
            .build();
    }

    public static Habit getHabit(Long id, String image) {
        return Habit.builder()
            .id(id)
            .image(image)
            .habitTranslations(Collections.singletonList(HabitTranslation.builder()
                .id(1L)
                .name("name")
                .description("")
                .habitItem("")
                .language(new Language(1L, "en", Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList()))
                .build()))
            .build();
    }

    public static HabitAssign getHabitAssign(Long id, Habit habit, HabitAssignStatus status) {
        return HabitAssign.builder()
            .id(id)
            .status(status)
            .createDate(ZonedDateTime.now())
            .habit(habit)
            .user(getUser())
            .userShoppingListItems(new ArrayList<>())
            .workingDays(0)
            .duration(7)
            .habitStreak(0)
            .habitStatistic(Collections.singletonList(HabitStatistic.builder()
                .id(1L).habitRate(HabitRate.GOOD).createDate(ZonedDateTime.now())
                .amountOfItems(10).build()))
            .habitStatusCalendars(Collections.singletonList(HabitStatusCalendar.builder()
                .enrollDate(LocalDate.now()).id(1L).build()))
            .lastEnrollmentDate(ZonedDateTime.now())
            .build();
    }

    public static HabitAssignDto getHabitAssignDto(Long id, HabitAssignStatus status, String image) {
        return HabitAssignDto.builder()
            .id(id)
            .status(status)
            .createDateTime(ZonedDateTime.now())
            .habit(HabitDto.builder()
                .id(1L)
                .image(image).build())
            .userId(1L).build();
    }

    public static CustomShoppingListItem getCustomShoppingListItemWithStatusInProgress() {
        return CustomShoppingListItem.builder()
            .id(2L)
            .habit(Habit.builder()
                .id(3L)
                .build())
            .user(getUser())
            .text("item")
            .status(ShoppingListItemStatus.INPROGRESS)
            .build();
    }

    public static CustomShoppingListItemResponseDto getCustomShoppingListItemResponseDtoWithStatusInProgress() {
        return CustomShoppingListItemResponseDto.builder()
            .id(2L)
            .text("item")
            .status(ShoppingListItemStatus.INPROGRESS)
            .build();
    }

    public static UserFriendDto getUserFriendDto() {
        return UserFriendDto.builder()
            .id(1L)
            .name("name")
            .userLocationDto(new UserLocationDto(1L, "Lviv", "Львів", "Lvivska",
                "Львівська", "Ukraine", "Україна", 12.345678, 12.345678))
            .rating(10.0)
            .mutualFriends(3L)
            .profilePicturePath("path-to-picture")
            .chatId(4L)
            .build();
    }

    public static FilterEventDto getFilterEventDto() {
        return FilterEventDto.builder()
            .time(PAST)
            .cities(List.of("Kyiv"))
            .statuses(List.of(OPEN))
            .tags(List.of("SOCIAL", "ECONOMIC", "ENVIRONMENTAL"))
            .title("111")
            .build();
    }

    public static SearchNewsDto getSearchNews() {
        return SearchNewsDto.builder().id(1L).title("title").tags(Collections.singletonList("tag")).build();
    }

    public static SearchEventsDto getSearchEvents() {
        return SearchEventsDto.builder().id(1L).title("Title").tags(new ArrayList<>()).build();
    }

    public static User getTagUser() {
        return User.builder()
            .id(1L)
            .name("Test")
            .profilePicturePath("Pic")
            .build();
    }

    public static UserTagDto getUserTagDto() {
        return UserTagDto.builder()
            .userId(1L)
            .userName("Test")
            .profilePicture("Pic")
            .build();
    }

    public static UserSearchDto getUserSearchDto() {
        return UserSearchDto.builder()
            .currentUserId(1L)
            .searchQuery("Test")
            .build();
    }

    public static List<Tuple> getTuples(TupleElement<?>[] elements) {
        TupleMetadata tupleMetadata = new TupleMetadata(
            elements, new String[] {eventId, title, description, tagId, languageCode, tagName,
                isOpen, type, organizerId, organizerName, titleImage, creationDate, startDate,
                finishDate, onlineLink, latitude, longitude, streetEn, streetUa, houseNumber,
                cityEn, cityUa, regionEn, regionUa, countryEn, countryUa, formattedAddressEn,
                formattedAddressUa, isRelevant, likes, countComments, grade, isOrganizedByFriend, isSubscribed,
                isFavorite});

        Object[] row1 = new Object[] {1L, "test1", "<p>description</p>", 1L, "en", "Social", true, "ONLINE", 1L,
            "Test", "image.png", Date.valueOf("2024-04-16"), Instant.parse("2025-05-15T00:00:03Z"),
            Instant.parse("2025-05-16T00:00:03Z"), "testtesttesttest", 0., 1., null,
            null, null, "Kyiv", null, null, null, null, null, null, null, true, 0L, 2L, new BigDecimal("3.5"), false,
            true, true, true};
        Object[] row2 = new Object[] {1L, "test1", "<p>description</p>", 1L, "ua", "Соціальний", true, "ONLINE", 1L,
            "Test", "image.png", Date.valueOf("2024-04-16"), Instant.parse("2025-05-15T00:00:03Z"),
            Instant.parse("2025-05-16T00:00:03Z"), "testtesttesttest", 0., 1., null,
            null, null, "Kyiv", null, null, null, null, null, null, null, true, 0L, 2L, new BigDecimal("3.5"), false,
            true, true, true};
        Object[] row3 = new Object[] {3L, "test3", "<p>description</p>", 2L, "en", "Social1", true, "ONLINE_OFFLINE",
            2L,
            "Test3", "image.png", Date.valueOf("2024-04-14"), Instant.parse("2025-05-15T00:00:03Z"),
            Instant.parse("2025-05-16T00:00:03Z"), "testtesttesttest", 0., 1., null,
            null, null, "Kyiv", null, null, null, null, null, null, null, true, 0L, 2L, new BigDecimal("3.5"), false,
            true, true, true};
        Object[] row4 = new Object[] {3L, "test3", "<p>description</p>", 2L, "ua", "Соціальний1", true,
            "ONLINE_OFFLINE", 2L,
            "Test3", "image.png", Date.valueOf("2024-04-14"), Instant.parse("2025-05-15T00:00:03Z"),
            Instant.parse("2025-05-16T00:00:03Z"), "testtesttesttest", 0., 1., null,
            null, null, "Kyiv", null, null, null, null, null, null, null, true, 0L, 2L, new BigDecimal("3.5"), false,
            true, true, true};
        return List.of(new TupleImpl(tupleMetadata, row1), new TupleImpl(tupleMetadata, row2),
            new TupleImpl(tupleMetadata, row3), new TupleImpl(tupleMetadata, row4));
    }

    public static TupleElement<?>[] getTupleElements() {
        return new TupleElement<?>[] {
            new TupleElementImpl<>(Long.class, eventId),
            new TupleElementImpl<>(String.class, title),
            new TupleElementImpl<>(String.class, description),
            new TupleElementImpl<>(Long.class, tagId),
            new TupleElementImpl<>(String.class, languageCode),
            new TupleElementImpl<>(String.class, tagName),
            new TupleElementImpl<>(Boolean.class, isOpen),
            new TupleElementImpl<>(String.class, type),
            new TupleElementImpl<>(Long.class, organizerId),
            new TupleElementImpl<>(String.class, organizerName),
            new TupleElementImpl<>(String.class, titleImage),
            new TupleElementImpl<>(Date.class, creationDate),
            new TupleElementImpl<>(Instant.class, startDate),
            new TupleElementImpl<>(Instant.class, finishDate),
            new TupleElementImpl<>(String.class, onlineLink),
            new TupleElementImpl<>(Double.class, latitude),
            new TupleElementImpl<>(Double.class, longitude),
            new TupleElementImpl<>(String.class, streetEn),
            new TupleElementImpl<>(String.class, streetUa),
            new TupleElementImpl<>(String.class, houseNumber),
            new TupleElementImpl<>(String.class, cityEn),
            new TupleElementImpl<>(String.class, cityUa),
            new TupleElementImpl<>(String.class, regionEn),
            new TupleElementImpl<>(String.class, regionUa),
            new TupleElementImpl<>(String.class, countryEn),
            new TupleElementImpl<>(String.class, countryUa),
            new TupleElementImpl<>(String.class, formattedAddressEn),
            new TupleElementImpl<>(String.class, formattedAddressUa),
            new TupleElementImpl<>(Boolean.class, isRelevant),
            new TupleElementImpl<>(Long.class, likes),
            new TupleElementImpl<>(Long.class, countComments),
            new TupleElementImpl<>(BigDecimal.class, grade),
            new TupleElementImpl<>(Boolean.class, isOrganizedByFriend),
            new TupleElementImpl<>(Boolean.class, isSubscribed),
            new TupleElementImpl<>(Boolean.class, isFavorite)
        };
    }

    public static List<EventDto> getEventPreviewDtos() {
        return List.of(
            EventDto.builder()
                .id(3L)
                .title("test3")
                .description("<p>description</p>")
                .organizer(EventAuthorDto.builder().id(2L).name("Test3").build())
                .creationDate(Date.valueOf("2024-04-14").toLocalDate())
                .dates(List.of(
                    EventDateLocationDto.builder()
                        .startDate(
                            ZonedDateTime.ofInstant(Instant.parse("2025-05-15T00:00:03Z"), ZoneId.systemDefault()))
                        .finishDate(
                            ZonedDateTime.ofInstant(Instant.parse("2025-05-16T00:00:03Z"), ZoneId.systemDefault()))
                        .onlineLink("testtesttesttest")
                        .coordinates(AddressDto.builder()
                            .latitude(0.0)
                            .longitude(1.0)
                            .cityEn("Kyiv")
                            .build())
                        .build()))
                .tags(List.of(TagUaEnDto.builder()
                    .id(2L)
                    .nameUa("Соціальний1")
                    .nameEn("Social1")
                    .build()))
                .titleImage("image.png")
                .isOpen(true)
                .type(EventType.ONLINE_OFFLINE)
                .isSubscribed(true)
                .isFavorite(true)
                .isRelevant(true)
                .likes(0)
                .countComments(2)
                .isOrganizedByFriend(false)
                .eventRate(3.5)
                .build(),
            EventDto.builder()
                .id(1L)
                .description("<p>description</p>")
                .title("test1")
                .organizer(EventAuthorDto.builder().id(1L).name("Test").build())
                .creationDate(Date.valueOf("2024-04-16").toLocalDate())
                .dates(List.of(
                    EventDateLocationDto.builder()
                        .startDate(
                            ZonedDateTime.ofInstant(Instant.parse("2025-05-15T00:00:03Z"), ZoneId.systemDefault()))
                        .finishDate(
                            ZonedDateTime.ofInstant(Instant.parse("2025-05-16T00:00:03Z"), ZoneId.systemDefault()))
                        .onlineLink("testtesttesttest")
                        .coordinates(AddressDto.builder()
                            .latitude(0.0)
                            .longitude(1.0)
                            .cityEn("Kyiv")
                            .build())
                        .build()))
                .tags(List.of(TagUaEnDto.builder()
                    .id(1L)
                    .nameUa("Соціальний")
                    .nameEn("Social")
                    .build()))
                .titleImage("image.png")
                .isOpen(true)
                .type(EventType.ONLINE)
                .isSubscribed(true)
                .isFavorite(true)
                .isRelevant(true)
                .likes(0)
                .countComments(2)
                .isOrganizedByFriend(false)
                .eventRate(3.5)
                .build());
    }

    public static ActionDto getActionDto() {
        return ActionDto.builder().userId(1L).build();
    }

    public static NotificationDto getNotificationDto() {
        return NotificationDto.builder()
            .notificationId(1L)
            .projectName(String.valueOf(GREENCITY))
            .notificationType(String.valueOf(EVENT_CREATED))
            .time(LocalDateTime.of(2100, 1, 31, 12, 0))
            .viewed(true)
            .titleText("You have created event")
            .bodyText("You successfully created event {message}.")
            .actionUserId(1L)
            .actionUserText("Taras")
            .targetId(1L)
            .message("Message")
            .secondMessage("Second message")
            .secondMessageId(2L)
            .build();
    }

    public static Notification getNotification() {
        return Notification.builder()
            .id(1L)
            .customMessage("Message")
            .targetId(1L)
            .secondMessage("Second message")
            .secondMessageId(2L)
            .notificationType(EVENT_CREATED)
            .projectName(GREENCITY)
            .viewed(true)
            .time(LocalDateTime.of(2100, 1, 31, 12, 0))
            .actionUsers(List.of(getUser()))
            .emailSent(true)
            .build();
    }

    public static Notification getNotificationWithSeveralActionUsers(int numberOfUsers) {
        long userIdCounter = 0L;
        List<User> actionUsers = new ArrayList<>();
        for (int i = 0; i < numberOfUsers; i++) {
            User user = new User();
            user.setId(userIdCounter++);
            actionUsers.add(user);
        }

        return Notification.builder()
            .id(1L)
            .customMessage("Message")
            .targetId(1L)
            .secondMessage("Second message")
            .secondMessageId(2L)
            .notificationType(EVENT_CREATED)
            .projectName(GREENCITY)
            .viewed(true)
            .time(LocalDateTime.of(2100, 1, 31, 12, 0))
            .actionUsers(actionUsers)
            .emailSent(true)
            .build();
    }

    public static PageableAdvancedDto<NotificationDto> getPageableAdvancedDtoForNotificationDto() {
        return new PageableAdvancedDto<>(Collections.singletonList(getNotificationDto()),
            1, 0, 1, 0,
            false, false, true, true);
    }

    public static EmailNotificationDto getEmailNotificationDto() {
        return EmailNotificationDto.builder()
            .targetUser(getUserVO())
            .actionUsers(new ArrayList<>())
            .customMessage("custom")
            .secondMessage("second")
            .notificationType(EVENT_CREATED)
            .build();
    }

    public static UserAsFriendDto getUserAsFriendDto() {
        return UserAsFriendDto.builder()
            .id(1L)
            .requesterId(1L)
            .friendStatus("FRIEND")
            .chatId(1L)
            .build();
    }

    public static SubscriberDto getSubscriberDto() {
        return SubscriberDto.builder()
            .email("test@example.com")
            .build();
    }

    public static RatingPoints getRatingPointsUndoAcquiredHabit14Days() {
        return RatingPoints.builder().id(1L).name("UNDO_ACQUIRED_HABIT_14_DAYS").points(-20).build();
    }

    public static RatingPoints getRatingPointsAcquiredHabit14Days() {
        return RatingPoints.builder().id(1L).name("ACQUIRED_HABIT_14_DAYS").points(20).build();
    }

    public static AmountCommentLikesDto getAmountCommentLikesDto() {
        return AmountCommentLikesDto.builder()
            .id(1L)
            .amountLikes(2)
            .build();
    }

    public static PhotoVO getPhotoVO() {
        return PhotoVO.builder().id(1L).name("photo").commentId(1L).placeId(1L).userId(1L).build();
    }

    public static Pageable getSortedPageable() {
        return PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
    }

    public static Pageable getUnSortedPageable() {
        return PageRequest.of(0, 10);
    }

    public static List<UserManagementVO> getListUserManagementVO() {
        return List.of(UserManagementVO.builder()
            .id(1L)
            .name(TestConst.NAME)
            .email(TestConst.EMAIL)
            .userStatus(ACTIVATED)
            .role(Role.ROLE_USER).build());
    }

    public static Page<UserManagementVO> getUserManagementVOPage() {
        return new PageImpl<>(getListUserManagementVO(), getSortedPageable(), 1);
    }

    public static Page<User> getUserPage() {
        return new PageImpl<>(List.of(getUser()), getSortedPageable(), 1);
    }

    public static SocialNetworkImage getSocialNetworkImage() {
        return SocialNetworkImage.builder()
            .id(1L)
            .hostPath("hostPath")
            .imagePath("imagePath")
            .build();
    }

    public static SocialNetworkImage getSocialNetworkImageId2() {
        return SocialNetworkImage.builder()
            .id(2L)
            .hostPath("hostPath2")
            .imagePath("imagePath2")
            .build();
    }

    public static SocialNetworkImage getSocialNetworkImageId3() {
        return SocialNetworkImage.builder()
            .id(3L)
            .hostPath("hostPath3")
            .imagePath("imagePath3")
            .build();
    }

    public static UserFilterDto getUserFilterDto() {
        return UserFilterDto.builder()
            .query(TEST_QUERY)
            .role(ROLE_ADMIN)
            .status(STATUS_ACTIVATED)
            .build();
    }
}
