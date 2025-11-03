package greencity;

import greencity.dto.event.AddressDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.EventDto;
import greencity.dto.tag.TagUkEnDto;
import greencity.entity.User;
import greencity.enums.EventType;

import java.time.*;
import java.util.List;

public class ModelUtils {
    public static User getUser() {
        return User.builder()
            .id(1L)
            .name("Taras")
            .email("taras@gmail.com")
            .build();
    }

    public static List<EventDto> getListEventDto() {
        return List.of(
            EventDto.builder()
                .id(3L)
                .title("test3")
                .organizer(EventAuthorDto.builder().id(1L).name("Test3").build())
                .creationDate(LocalDate.now().plus(Period.ofDays(12)))
                .dates(List.of(
                    EventDateLocationDto.builder()
                        .startDate(ZonedDateTime.now().plus(Period.ofDays(15)))
                        .finishDate(ZonedDateTime.now().plus(Period.ofDays(20)))
                        .onlineLink("testtesttesttest")
                        .coordinates(AddressDto.builder()
                            .latitude(0.0)
                            .longitude(1.0)
                            .cityEn("Kyiv")
                            .build())
                        .build()))
                .tags(List.of(TagUkEnDto.builder()
                    .id(2L)
                    .nameUk("Соціальний1")
                    .nameEn("Social1")
                    .build()))
                .titleImage("image.png")
                .isOpen(true)
                .isSubscribed(true)
                .isFavorite(true)
                .isRelevant(true)
                .likes(0)
                .countComments(2)
                .isOrganizedByFriend(false)
                .eventRate(3.5)
                .description("123")
                .type(EventType.ONLINE)
                .build(),
            EventDto.builder()
                .id(1L)
                .title("test1")
                .organizer(EventAuthorDto.builder().id(1L).name("Test").build())
                .creationDate(LocalDate.now().minus(Period.ofDays(12)))
                .dates(List.of(
                    EventDateLocationDto.builder()
                        .startDate(
                            ZonedDateTime.now().minus(Period.ofDays(10)))
                        .finishDate(
                            ZonedDateTime.now().minus(Period.ofDays(5)))
                        .onlineLink("testtesttesttest")
                        .coordinates(AddressDto.builder()
                            .latitude(0.0)
                            .longitude(1.0)
                            .cityEn("Kyiv")
                            .build())
                        .build()))
                .tags(List.of(TagUkEnDto.builder()
                    .id(1L)
                    .nameUk("Соціальний")
                    .nameEn("Social")
                    .build()))
                .titleImage("image.png")
                .isOpen(true)
                .isSubscribed(true)
                .isFavorite(true)
                .isRelevant(true)
                .likes(0)
                .countComments(2)
                .isOrganizedByFriend(false)
                .eventRate(3.5)
                .description("123")
                .type(EventType.ONLINE)
                .build());
    }
}
