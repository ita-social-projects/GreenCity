package greencity.dto.user;

/**
 * Dto for getting information about User's recommended friends.
 */
public interface UsersFriendDto {
    /**
     * Get user's id.
     *
     * @return id
     */
    Long getId();

    /**
     * Get user's name.
     *
     * @return name
     */
    String getName();

    /**
     * Get user's profile picture.
     *
     * @return profile_picture
     */
    String getProfilePicture();
}
