package greencity.dto.friends;

import greencity.dto.location.UserLocationDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        Long requesterId = 3L;
        UserFriendDto userFriendDto = new UserFriendDto(id, name, rating, uLocation.getId(),
            uLocation.getCityEn(), uLocation.getCityUk(), uLocation.getRegionEn(), uLocation.getRegionUk(),
            uLocation.getCountryEn(), uLocation.getCountryUk(), uLocation.getLatitude(), uLocation.getLongitude(),
            mutualFriends, profilePicture, friendStatus, requesterId);
        userFriendDto.setEmail(email);

        assertEquals(id, userFriendDto.getId());
        assertEquals(name, userFriendDto.getName());
        assertEquals(email, userFriendDto.getEmail());
        assertEquals(uLocation, userFriendDto.getUserLocationDto());
        assertEquals(rating, userFriendDto.getRating());
        assertEquals(mutualFriends, userFriendDto.getMutualFriends());
        assertEquals(profilePicture, userFriendDto.getProfilePicturePath());
        assertEquals(friendStatus, userFriendDto.getFriendStatus());
        assertEquals(requesterId, userFriendDto.getRequesterId());
    }

    @Test
    void testUserFriendDtoConstructorWithoutUserLocation() {
        Long id = 1L;
        String name = "name";
        Double rating = 1.0;
        Long ulId = null;
        Long mutualFriends = 2L;
        String profilePicturePath = "/path/to/profile/picture";
        Long chatId = 4L;
        String friendStatus = "FRIEND";
        Long requesterId = 3L;

        UserFriendDto userFriendDto = new UserFriendDto(
            id, name, rating, ulId, null, null, null, null, null, null,
            null, null, mutualFriends, profilePicturePath, friendStatus, requesterId);

        assertNull(userFriendDto.getUserLocationDto());
    }

}
