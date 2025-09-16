package greencity;

import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.category.CategoryDto;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.InterestingEcoNewsDto;
import greencity.dto.econews.ShortEcoNewsDto;
import greencity.dto.event.EventDto;
import greencity.dto.habit.CustomHabitDtoRequest;
import greencity.dto.habit.CustomHabitDtoResponse;
import greencity.dto.habit.UserToDoAndCustomToDoListsDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.location.UserLocationDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.dto.todolistitem.CustomToDoListItemResponseDto;
import greencity.dto.tag.TagUkEnDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.dto.user.SubscriberDto;
import greencity.dto.user.UserToDoListItemResponseDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.Role;
import greencity.enums.ToDoListItemStatus;
import greencity.message.ScheduledEmailMessage;
import greencity.message.SendReportEmailMessage;
import greencity.message.SendHabitNotification;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static greencity.TestConst.ACCESS_TOKEN;
import static greencity.TestConst.EMAIL;
import static greencity.TestConst.USER_ID;
import static greencity.enums.UserStatus.ACTIVATED;

public class ModelUtils {
    public static UserVO getUserVO() {
        return UserVO.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .build();
    }

    public static PlaceNotificationDto getPlaceNotificationDto() {
        return PlaceNotificationDto.builder()
            .category(getCategoryDto())
            .name("name")
            .build();
    }

    public static SendHabitNotification getSendHabitNotification() {
        return SendHabitNotification.builder()
            .userId(2L)
            .name("taras")
            .build();
    }

    public static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer accessToken");
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public static CategoryDto getCategoryDto() {
        return CategoryDto.builder()
            .nameEn("name")
            .parentCategoryId(1L)
            .build();
    }

    public static SendReportEmailMessage getSendReportEmailMessage() {
        return SendReportEmailMessage.builder()
            .periodicity(EmailPreferencePeriodicity.WEEKLY)
            .categoriesDtoWithPlacesDtoMap(Collections.singletonMap(
                getCategoryDto(), Collections.singletonList(getPlaceNotificationDto())))
            .subscribers(getSubscribers())
            .build();
    }

    public static AddEcoNewsDtoResponse getAddEcoNewsDtoResponse() {
        return new AddEcoNewsDtoResponse(1L, "title",
            "text", "shortInfo", EcoNewsAuthorDto.builder().id(1L).name(TestConst.NAME).build(),
            ZonedDateTime.now(), TestConst.SITE, null,
            Arrays.asList("Новини", "News", "Новины"));
    }

    public static InterestingEcoNewsDto getInterestingEcoNewsDto() {
        return InterestingEcoNewsDto.builder()
            .ecoNewsList(getShortEcoNewsDto())
            .subscribers(getSubscribers())
            .build();
    }

    private static List<SubscriberDto> getSubscribers() {
        return List.of(SubscriberDto.builder()
            .email("email@gmail.com")
            .name("Ilia")
            .unsubscribeToken(UUID.randomUUID())
            .build());
    }

    private static List<ShortEcoNewsDto> getShortEcoNewsDto() {
        return List.of(ShortEcoNewsDto.builder()
            .ecoNewsId(1L)
            .imagePath("https://google.com")
            .text("Text")
            .title("Title")
            .build());
    }

    public static TagUkEnDto tagUaEnDto = TagUkEnDto.builder().id(1L).nameUk("Соціальний").nameEn("Social").build();

    public static EventDto getEventDtoWithTag() {
        return EventDto.builder()
            .id(1L)
            .countComments(2)
            .likes(20)
            .tags(List.of(tagUaEnDto))
            .build();
    }

    public static EventDto getEventDtoWithoutTag() {
        return EventDto.builder()
            .id(1L)
            .countComments(2)
            .likes(20)
            .build();
    }

    public static UserToDoListItemResponseDto getUserToDoListItemResponseDto() {
        return UserToDoListItemResponseDto.builder()
            .id(1L)
            .text("text")
            .status(ToDoListItemStatus.ACTIVE)
            .build();
    }

    public static CustomToDoListItemResponseDto getCustomToDoListItemResponseDto() {
        return CustomToDoListItemResponseDto.builder()
            .id(1L)
            .text("text")
            .status(ToDoListItemStatus.ACTIVE)
            .build();
    }

    public static UserToDoAndCustomToDoListsDto getUserToDoAndCustomToDoListsDto() {
        return UserToDoAndCustomToDoListsDto.builder()
            .userToDoListItemDto(List.of(getUserToDoListItemResponseDto()))
            .customToDoListItemDto(List.of(getCustomToDoListItemResponseDto()))
            .build();
    }

    public static CustomHabitDtoRequest getAddCustomHabitDtoRequest() {
        return CustomHabitDtoRequest.builder()
            .complexity(1)
            .image("")
            .defaultDuration(14)
            .tagIds(Set.of(20L))
            .build();
    }

    public static CustomHabitDtoResponse getAddCustomHabitDtoResponse() {
        return CustomHabitDtoResponse.builder()
            .id(1L)
            .complexity(1)
            .image("")
            .defaultDuration(14)
            .tagIds(Set.of(20L))
            .build();
    }

    public static URL getUrl() throws MalformedURLException {
        return new URL(TestConst.SITE);
    }

    public static ScheduledEmailMessage getScheduledEmailMessage() {
        return ScheduledEmailMessage.builder()
            .username("test")
            .body("test")
            .subject("test")
            .language("en")
            .baseLink("test")
            .userId(getUserVO().getId())
            .build();
    }

    public static UserVOAdvancedDto getUserVOAdvancedDto() {
        UserVOAdvancedDto advancedDto = new UserVOAdvancedDto();

        advancedDto.setFirstName(TestConst.NAME);
        advancedDto.setDateOfRegistration(LocalDateTime.of(2025, 4, 20, 13, 30));
        advancedDto.setId(1L);
        advancedDto.setName(TestConst.NAME);
        advancedDto.setEmail(TestConst.EMAIL);
        advancedDto.setRole(Role.ROLE_USER);
        advancedDto.setUserCredo(TestConst.CREDO);
        advancedDto.setUserStatus(ACTIVATED);
        advancedDto.setUserLocation(UserLocationDto.builder()
            .latitude(1d)
            .longitude(1d)
            .build());
        advancedDto.setLanguageVO(getLanguageDTO());
        advancedDto.setUserAchievements(List.of(getUserAchievementVO()));
        advancedDto.setUserFriends(getUserFriends());
        advancedDto.setSocialNetworks(getSocialNetworkVOs());
        advancedDto.setRating(10.0);

        return advancedDto;
    }

    public static LanguageDTO getLanguageDTO() {
        return new LanguageDTO(1L, "en", "English");
    }

    public static UserAchievementVO getUserAchievementVO() {
        return new UserAchievementVO(1L, getUserVoShort(), getAchievementVOWithAchievementCategory(), false);
    }

    public static List<UserVO> getUserFriends() {
        UserVO firstFriend = UserVO.builder()
            .id(3L)
            .name("Sasha")
            .build();

        UserVO secondFriend = UserVO.builder()
            .id(4L)
            .name("Masha")
            .build();

        return List.of(firstFriend, secondFriend);
    }

    public static ConstraintValidatorContext.ConstraintViolationBuilder getConstraintViolationBuilder() {
        return new ConstraintValidatorContext.ConstraintViolationBuilder() {
            @Override
            public NodeBuilderDefinedContext addNode(String name) {
                return null;
            }

            @Override
            public NodeBuilderCustomizableContext addPropertyNode(String name) {
                return null;
            }

            @Override
            public LeafNodeBuilderCustomizableContext addBeanNode() {
                return null;
            }

            @Override
            public ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String name,
                Class<?> containerType,
                Integer typeArgumentIndex) {
                return null;
            }

            @Override
            public NodeBuilderDefinedContext addParameterNode(int index) {
                return null;
            }

            @Override
            public ConstraintValidatorContext addConstraintViolation() {
                return null;
            }
        };
    }

    public static List<SocialNetworkVO> getSocialNetworkVOs() {
        SocialNetworkVO socialNetworkVO1 = SocialNetworkVO.builder()
            .id(9L)
            .url("http://test.com.ua")
            .user(getUserVoShort())
            .socialNetworkImage(getOneSocialNetworkImageVO())
            .build();

        SocialNetworkVO socialNetworkVO2 = SocialNetworkVO.builder()
            .id(10L)
            .url("http://test-test.com.ua")
            .user(getUserVoShort())
            .socialNetworkImage(getOneSocialNetworkImageVO())
            .build();

        return List.of(socialNetworkVO1, socialNetworkVO2);
    }

    public static UserVO getUserVoShort() {
        return UserVO.builder()
            .id(1L)
            .email("taras@gmail.com")
            .build();
    }

    public static SocialNetworkImageVO getOneSocialNetworkImageVO() {
        return SocialNetworkImageVO.builder()
            .id(13L)
            .imagePath("http://test-test.com.ua")
            .hostPath("hostPath2")
            .build();
    }

    public static AchievementVO getAchievementVOWithAchievementCategory() {
        return new AchievementVO(1L, "ACQUIRED_HABIT_14_DAYS", "Набуття звички протягом 14 днів",
            "Acquired habit 14 days", new AchievementCategoryVO(1L, "CREATE_NEWS"), null,
            null, null);
    }

    public static SocialNetworkImageResponseDTO getSocialNetworkImageResponseDTO() {
        return new SocialNetworkImageResponseDTO(1L, "image path", "host path");
    }

    public static UserStatusDto getUserStatusDto() {
        return UserStatusDto.builder().email(EMAIL).userStatus(ACTIVATED).build();
    }

    public static HttpEntity<UserStatusDto> getEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(getUserStatusDto(), headers);
    }
}
