package greencity.repository;

import greencity.GreenCityApplication;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static greencity.entity.enums.UserStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GreenCityApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserRepoTest {
    @Autowired
    private UserRepo userRepo;

    private final User testUser =
        User.builder()
            .id(1L)
            .dateOfRegistration(LocalDateTime.parse("2019-09-30T00:00"))
            .email("test@email.com")
            .emailNotification(EmailNotification.DISABLED)
            .name("SuperTest")
            .lastVisit(LocalDateTime.parse("2020-09-30T00:00"))
            .role(ROLE.ROLE_USER)
            .userStatus(ACTIVATED)
            .refreshTokenKey("secret")
            .build();

    private final User testUser2 =
        User.builder()
            .id(2L)
            .dateOfRegistration(LocalDateTime.parse("2019-09-29T00:00"))
            .email("test2@email.com")
            .emailNotification(EmailNotification.DISABLED)
            .name("SuperTest2")
            .lastVisit(LocalDateTime.parse("2020-09-29T00:00"))
            .role(ROLE.ROLE_ADMIN)
            .userStatus(ACTIVATED)
            .refreshTokenKey("secret2")
            .build();

    @Test
    @Sql("classpath:sql/populate_users_for_test.sql")
    void findByEmailTest() {
        Optional<User> userByEmail = userRepo.findByEmail("test@email.com");
        User user = userByEmail.get();
        assertEquals(testUser.getId(), user.getId());
        assertEquals(testUser.getName(), user.getName());
    }

    @Test
    @Sql("classpath:sql/populate_users_for_test.sql")
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
    @Sql("classpath:sql/populate_users_for_test.sql")
    void findIdByEmailTest() {
        Optional<Long> idByEmail = userRepo.findIdByEmail("test@email.com");
        Long id = idByEmail.get();
        assertEquals(testUser.getId(), id);
    }

    @Test
    @Sql("classpath:sql/populate_users_for_test.sql")
    void findNotDeactivatedByEmailTest() {
        Optional<User> optionalUser = userRepo.findNotDeactivatedByEmail("test@email.com");
        User user = optionalUser.get();
        assertEquals(testUser.getId(), user.getId());
        assertEquals(testUser.getEmail(), user.getEmail());
    }
    //todo: check deactivated user test

    @Test
    @Sql("classpath:sql/populate_users_for_test.sql")
    void findAllByEmailNotificationTest() {
        List<User> users =
            userRepo.findAllByEmailNotification(EmailNotification.DISABLED);
        assertEquals(testUser.getId(), users.get(0).getId());
        assertEquals(testUser.getEmail(), users.get(0).getEmail());
    }

    //todo: empty list

    @Test
    @Sql("classpath:sql/populate_users_for_test.sql")
    void updateUserRefreshTokenTest() {
        userRepo.updateUserRefreshToken("newToken", 1L);
        Optional<User> byId = userRepo.findById(1L);
        String refreshTokenKey = byId.get().getRefreshTokenKey();
        assertEquals("newToken", refreshTokenKey);
    }

    @Test
    @Sql("classpath:sql/populate_users_for_test.sql")
    void countAllByUserStatusTest() {
        long count = userRepo.countAllByUserStatus(ACTIVATED);
        assertEquals(5, count);
    }

    @Test
    @Sql("classpath:sql/populate_users_for_test.sql")
    void getProfilePicturePathByUserIdTest() {
        testUser.setProfilePicturePath("someSecretPathToPicture");
        String path = userRepo.getProfilePicturePathByUserId(1L).get();
        assertEquals(testUser.getProfilePicturePath(), path);
    }

    @Test
    @Sql("classpath:sql/populate_users_for_test.sql")
    void getAllUserFriendsTest() {
        testUser.setUserFriends(List.of(testUser2));
        List<User> userFriends = userRepo.getAllUserFriends(1L);
        User friend = testUser.getUserFriends().get(0);
        assertEquals(friend.getId(), userFriends.get(0).getId());
        assertEquals(friend.getEmail(), userFriends.get(0).getEmail());
    }

    @Test
    @Sql("classpath:sql/populate_users_for_test.sql")
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
    @Sql("classpath:sql/populate_users_for_test.sql")
    void deleteUserFriendByIdTest() {
        userRepo.deleteUserFriendById(1L, 2L);
        Optional<User> optionalUser = userRepo.findById(1L);
        User user = optionalUser.get();
        assertEquals(Collections.emptyList(), user.getUserFriends());
    }

    @Test
    @Sql("classpath:sql/populate_users_for_test.sql")
    void addNewFriendTest() {
        List<User> list = List.of(testUser2, User.builder().id(3L).build());
        userRepo.addNewFriend(1L, 3L);
        Optional<User> optionalUser = userRepo.findById(1L);
        User user = optionalUser.get();
        List<User> userFriends = user.getUserFriends();
        assertEquals(list.get(1).getId(), userFriends.get(1).getId());
    }

    @Test
    @Sql("classpath:sql/populate_users_for_test.sql")
    void getSixFriendsWithTheHighestRatingTest() {
        List<User> friends = userRepo.getSixFriendsWithTheHighestRating(1L);
        assertEquals(6, friends.size());
        assertEquals(90, friends.get(0).getRating());
        assertEquals(80, friends.get(1).getRating());
    }

    @Test
    @Sql("classpath:sql/populate_users_for_test.sql")
    void updateUserLastActivityTimeTest() {
        Date date = Calendar.getInstance().getTime();
        userRepo.updateUserLastActivityTime(1L, date);
        User user = userRepo.findById(1L).get();
        Date lastActivityTime = Date.from(user.getLastActivityTime().atZone(ZoneId.systemDefault()).toInstant());
        assertEquals(date, lastActivityTime);
    }

    @Test
    @Sql("classpath:sql/populate_users_for_test.sql")
    void deactivateSelectedUsersTest() {
        userRepo.deactivateSelectedUsers(List.of(2L));
        User user = userRepo.findById(2L).get();
        assertEquals(UserStatus.DEACTIVATED, user.getUserStatus());
    }
}
