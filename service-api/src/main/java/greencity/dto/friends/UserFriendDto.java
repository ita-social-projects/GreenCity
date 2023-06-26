package greencity.dto.friends;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserFriendDto {
    private Long id;
    private String name;
    private String city;
    private Double rating;
    private Long mutualFriends;
    private String profilePicturePath;
    private String friendStatus;
    private FriendsChatDto friendsChatDto;

    /**
     * Constructor is needed for SqlResultSetMapping.
     */
    public UserFriendDto(Long id, String name, String city, Double rating, Long mutualFriends,
        String profilePicturePath, Long chatId) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.rating = rating;
        this.mutualFriends = mutualFriends;
        this.profilePicturePath = profilePicturePath;
        this.friendsChatDto = new FriendsChatDto(chatId, chatId != null);
    }
}
