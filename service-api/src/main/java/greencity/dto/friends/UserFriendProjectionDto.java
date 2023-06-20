package greencity.dto.friends;

import java.math.BigInteger;

/**
 * Interface needed to retrieve UserFriendDto from native query.
 */
public interface UserFriendProjectionDto {
    /**
     * Retrieve id.
     *
     * @return User id
     */
    Long getId();

    /**
     * Retrieve name.
     *
     * @return User name
     */
    String getName();

    /**
     * Retrieve city.
     *
     * @return User city
     */
    String getCity();

    /**
     * Retrieve rating.
     *
     * @return User rating
     */
    Double getRating();

    /**
     * Retrieve count of User mutual friends.
     *
     * @return count of User mutual friends
     */
    Long getMutualFriends();

    /**
     * Retrieve profile picture path.
     *
     * @return User profile picture path
     */
    String getProfilePicturePath();

    /**
     * Retrieve room id.
     *
     * @return room id
     */
    BigInteger getRoomId();
}
