package greencity.dto.friends;

import greencity.dto.location.UserLocationDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserFriendDtoTest {
    @Test
    void userFriendDtoConstructorTest() {
        Long id = 1L;
        String name = "name";
        String email = "email";
        UserLocationDto uLocation = new UserLocationDto(1L, "Lviv", "Львів", "Lvivska",
            "Львівська", "Ukraine", "Україна", 12.345678, 12.345678);
        Double rating = 1.0;
        Long mutualFriends = 2L;
        String profilePicture = "profilePicture";
        Long chatId = 4L;
        String friendStatus = "FRIEND";
        UserFriendDto userFriendDto = new UserFriendDto(id, name, email, rating, uLocation.getId(),
            uLocation.getCityEn(), uLocation.getCityUa(), uLocation.getRegionEn(), uLocation.getRegionUa(),
            uLocation.getCountryEn(), uLocation.getCountryUa(), uLocation.getLatitude(), uLocation.getLongitude(),
            mutualFriends, profilePicture, chatId, friendStatus);

        assertEquals(id, userFriendDto.getId());
        assertEquals(name, userFriendDto.getName());
        assertEquals(uLocation, userFriendDto.getUserLocationDto());
        assertEquals(rating, userFriendDto.getRating());
        assertEquals(mutualFriends, userFriendDto.getMutualFriends());
        assertEquals(profilePicture, userFriendDto.getProfilePicturePath());
        assertEquals(chatId, userFriendDto.getChatId());
        assertEquals(friendStatus, userFriendDto.getFriendStatus());
    }
}
