package greencity.dto.friends;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@SuppressWarnings("java:S107")
public class UserFriendDto {
    private Long id;
    private String name;
    private String email;
    private String city;
    private Double rating;
    private Long mutualFriends;
    private String profilePicturePath;
    private Long chatId;
    private String friendStatus;

    /**
     * Constructor is needed for SqlResultSetMapping.
     */
    public UserFriendDto(Long id, String name, String email, String city, Double rating, Long mutualFriends,
        String profilePicturePath, Long chatId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.city = city;
        this.rating = rating;
        this.mutualFriends = mutualFriends;
        this.profilePicturePath = profilePicturePath;
        this.chatId = chatId;
    }
}
