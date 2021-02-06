package greencity.repository;

import static greencity.enums.UserStatus.ACTIVATED;
import static greencity.enums.UserStatus.DEACTIVATED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Sql("classpath:sql/populate_users_for_test.sql")
class UserRepoTest {
    private final User testUser =
        User.builder()
            .id(1L)
            .dateOfRegistration(LocalDateTime.parse("2019-09-30T00:00"))
            .email("test@email.com")
            .emailNotification(EmailNotification.DISABLED)
            .name("SuperTest")
            .lastVisit(LocalDateTime.parse("2020-09-30T00:00"))
            .role(Role.ROLE_USER)
            .userStatus(ACTIVATED)
            .refreshTokenKey("secret")
            .city("New York")
            .build();
    private final User testUser2 =
        User.builder()
            .id(2L)
            .dateOfRegistration(LocalDateTime.parse("2019-09-29T00:00"))
            .email("test2@email.com")
            .emailNotification(EmailNotification.DISABLED)
            .name("SuperTest2")
            .lastVisit(LocalDateTime.parse("2020-09-29T00:00"))
            .role(Role.ROLE_ADMIN)
            .userStatus(ACTIVATED)
            .refreshTokenKey("secret2")
            .city("Kyiv")
            .build();

    @Autowired
    private UserRepo userRepo;

    @Test
    void findByEmailTest() {
        User user = userRepo.findByEmail("test@email.com").get();
        assertEquals(testUser.getId(), user.getId());
        assertEquals(testUser.getName(), user.getName());
    }

    @Test
    void findAllTest() {
        List<User> users = List.of(testUser, testUser2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> all = userRepo.findAll(pageable);
        List<User> collect = all.get().collect(Collectors.toList());
        assertEquals(users.get(0).getId(), collect.get(0).getId());
        assertEquals(users.get(0).getName(), collect.get(0).getName());
        assertEquals(users.get(0).getEmail(), collect.get(0).getEmail());
        assertEquals(users.get(1).getId(), collect.get(1).getId());
        assertEquals(users.get(1).getName(), collect.get(1).getName());
        assertEquals(users.get(1).getEmail(), collect.get(1).getEmail());
    }

    @Test
    void findIdByEmailTest() {
        Long id = userRepo.findIdByEmail("test@email.com").get();
        assertEquals(testUser.getId(), id);
    }

    @Test
    void findNotDeactivatedByEmailTest() {
        User user = userRepo.findNotDeactivatedByEmail("test@email.com").get();
        assertEquals(testUser.getId(), user.getId());
        assertEquals(testUser.getEmail(), user.getEmail());
    }

    @Test
    void findAllByEmailNotificationTest() {
        List<User> users =
            userRepo.findAllByEmailNotification(EmailNotification.DISABLED);
        assertEquals(testUser.getId(), users.get(0).getId());
        assertEquals(testUser.getEmail(), users.get(0).getEmail());
    }

    @Test
    void updateUserRefreshTokenTest() {
        userRepo.updateUserRefreshToken("newToken", 1L);
        User user = userRepo.findById(1L).get();
        String refreshTokenKey = user.getRefreshTokenKey();
        assertEquals("newToken", refreshTokenKey);
    }

    @Test
    void countAllByUserStatusTest() {
        long count = userRepo.countAllByUserStatus(ACTIVATED);
        assertEquals(10, count);
    }

    @Test
    void getProfilePicturePathByUserIdTest() {
        testUser.setProfilePicturePath("someSecretPathToPicture");
        String path = userRepo.getProfilePicturePathByUserId(1L).get();
        assertEquals(testUser.getProfilePicturePath(), path);
    }

    @Test
    void getAllUserFriendsTest() {
        testUser.setUserFriends(List.of(testUser2));
        List<User> userFriends = userRepo.getAllUserFriends(1L);
        User friend = testUser.getUserFriends().get(0);
        assertEquals(friend.getId(), userFriends.get(0).getId());
        assertEquals(friend.getEmail(), userFriends.get(0).getEmail());
    }

    @Test
    void getAllUserFriendsByPageTest() {
        testUser.setUserFriends(List.of(testUser2));
        Pageable pageable = PageRequest.of(0, 1);
        Page<User> page = userRepo.getAllUserFriends(1L, pageable);
        List<User> userFriends = page.get().collect(Collectors.toList());
        User friend = testUser.getUserFriends().get(0);
        assertEquals(friend.getId(), userFriends.get(0).getId());
        assertEquals(friend.getEmail(), userFriends.get(0).getEmail());
    }

    @Test
    void deleteUserFriendByIdTest() {
        userRepo.deleteUserFriendById(1L, 2L);
        User user = userRepo.findById(1L).get();
        assertEquals(6, user.getUserFriends().size());
    }

    @Test
    void addNewFriendTest() {
        userRepo.addNewFriend(9L, 1L);
        List<User> userFriends = userRepo.getAllUserFriendRequests(1L);

        assertEquals(9L, userFriends.get(0).getId());
    }

    @Test
    void getSixFriendsWithTheHighestRatingTest() {
        List<User> friends = userRepo.getSixFriendsWithTheHighestRating(1L);
        assertEquals(6, friends.size());
        assertEquals(90, friends.get(0).getRating());
        assertEquals(80, friends.get(1).getRating());
    }

    @Test
    void updateUserLastActivityTimeTest() {
        Date date = Calendar.getInstance().getTime();
        userRepo.updateUserLastActivityTime(1L, date);
        User user = userRepo.findById(1L).get();
        Date lastActivityTime = Date.from(user.getLastActivityTime().atZone(ZoneId.systemDefault()).toInstant());
        assertEquals(date, lastActivityTime);
    }

    @Test
    void deactivateSelectedUsersTest() {
        userRepo.deactivateSelectedUsers(List.of(2L));
        User user = userRepo.findById(2L).get();
        assertEquals(DEACTIVATED, user.getUserStatus());
    }

    @Test
    void searchByTest() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<User> page = userRepo.searchBy(pageable, "SuperTest");
        List<User> collect = page.get().collect(Collectors.toList());
        assertEquals(testUser.getName(), collect.get(0).getName());
    }

    @Test
    void findAllUsersCitiesTest() {
        List<String> cities = userRepo.findAllUsersCities();
        assertEquals(testUser.getCity(), cities.get(0));
        assertEquals(testUser2.getCity(), cities.get(1));
    }
}
