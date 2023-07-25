package greencity.dto.friends;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserFriendDtoTest {
    @Test
    void userFriendDtoConstructorTest() {
        Long id = 1L;
        String name = "name";
        String email = "email";
        String city = "city";
        Double rating = 1.0;
        Long mutualFriends = 2L;
        String profilePicture = "profilePicture";
        Long chatId = 4L;
        UserFriendDto userFriendDto = new UserFriendDto(id, name, email, city, rating, mutualFriends,
            profilePicture, chatId);
        assertEquals(id, userFriendDto.getId());
        assertEquals(name, userFriendDto.getName());
        assertEquals(city, userFriendDto.getCity());
        assertEquals(rating, userFriendDto.getRating());
        assertEquals(mutualFriends, userFriendDto.getMutualFriends());
        assertEquals(profilePicture, userFriendDto.getProfilePicturePath());
        assertEquals(chatId, userFriendDto.getChatId());
    }
}
