package greencity;

import greencity.constant.AppConstant;
import greencity.dto.event.AddressDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.EventDto;
import greencity.dto.tag.TagUaEnDto;
import greencity.dto.user.UserVO;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.entity.event.Address;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.localization.TagTranslation;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.enums.UserStatus;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class ModelUtils {
    public static UserVO TEST_USER_VO = createUserVO();

    public static User getUser() {
        return User.builder()
            .id(1L)
            .email("danylo@gmail.com")
            .name("Taras")
            .role(Role.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .lastActivityTime(LocalDateTime.now())
            .verifyEmail(new VerifyEmail())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    }

    public static UserVO getTestUserVo() {
        return UserVO.builder()
            .id(1L)
            .role(Role.ROLE_USER)
            .email("danylo@gmail.com")
            .build();
    }

    private static UserVO createUserVO() {
        return UserVO.builder()
            .id(1L)
            .role(Role.ROLE_MODERATOR)
            .email("test@mail.com")
            .build();
    }

    public static Principal getPrincipal() {
        return () -> "danylo@gmail.com";
    }

    public static Event getOfflineEvent() {
        Event event = new Event();
        Set<User> followers = new HashSet<>();
        followers.add(getUser());
        event.setOpen(true);
        event.setDescription("Description");
        event.setId(1L);
        event.setOrganizer(getUser());
        event.setFollowers(followers);
        event.setTitle("Title");
        List<EventDateLocation> dates = new ArrayList<>();
        dates.add(new EventDateLocation(1L, event,
            ZonedDateTime.of(2098, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            ZonedDateTime.of(2099, 2, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            getAddress(), null));
        event.setDates(dates);
        event.setTags(List.of(getEventTag()));
        event.setTitleImage(AppConstant.DEFAULT_HABIT_IMAGE);
        return event;
    }

    public static Event getOnlineEvent() {
        Event event = new Event();
        Set<User> followers = new HashSet<>();
        followers.add(getUser());
        event.setOpen(true);
        event.setDescription("Description");
        event.setId(1L);
        event.setOrganizer(getUser());
        event.setFollowers(followers);
        event.setTitle("Title");
        List<EventDateLocation> dates = new ArrayList<>();
        dates.add(new EventDateLocation(1L, event,
            ZonedDateTime.of(2098, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            ZonedDateTime.of(2099, 2, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            null, "/url"));
        event.setDates(dates);
        event.setTags(List.of(getEventTag()));
        event.setTitleImage(AppConstant.DEFAULT_HABIT_IMAGE);
        return event;
    }

    public static Event getOnlineOfflineEvent() {
        Event event = new Event();
        Set<User> followers = new HashSet<>();
        followers.add(getUser());
        event.setOpen(true);
        event.setDescription("Description");
        event.setId(1L);
        event.setOrganizer(getUser());
        event.setFollowers(followers);
        event.setTitle("Title");
        List<EventDateLocation> dates = new ArrayList<>();
        dates.add(new EventDateLocation(1L, event,
            ZonedDateTime.of(2098, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            ZonedDateTime.of(2099, 2, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
            getAddress(), "/url"));
        event.setDates(dates);
        event.setTags(List.of(getEventTag()));
        event.setTitleImage(AppConstant.DEFAULT_HABIT_IMAGE);
        return event;
    }

    public static EventDto getEventDto() {
        return EventDto.builder()
            .id(1L)
            .description("Description")
            .organizer(EventAuthorDto.builder()
                .name("User")
                .id(1L)
                .build())
            .title("Title")
            .dates(List.of(new EventDateLocationDto(1L, null,
                ZonedDateTime.of(2023, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
                ZonedDateTime.of(2023, 2, 1, 1, 1, 1, 1, ZoneId.systemDefault()),
                "/url",
                getAddressDto())))
            .tags(List.of(TagUaEnDto.builder().id(1L).nameEn("Social")
                .nameUa("Соціальний").build()))
            .build();
    }

    public static Address getAddress() {
        return Address.builder()
            .latitude(50.4567236)
            .longitude(30.2354469)
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

    public static AddressDto getAddressDto() {
        return AddressDto.builder()
            .latitude(50.4567236)
            .longitude(30.2354469)
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

    public static Tag getEventTag() {
        return new Tag(1L, TagType.EVENT, getEventTagTranslations(), Collections.emptyList(),
            Collections.emptySet(), Collections.emptySet());
    }

    public static List<TagTranslation> getEventTagTranslations() {
        Language language = getLanguage();
        return Arrays.asList(
            TagTranslation.builder().id(1L).name("Соціальний").language(getLanguageUa()).build(),
            TagTranslation.builder().id(2L).name("Social").language(language).build(),
            TagTranslation.builder().id(3L).name("Соціальний").language(language).build());
    }

    public static Language getLanguage() {
        return new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList());
    }

    public static Language getLanguageUa() {
        return new Language(2L, "ua", Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList());
    }
}
