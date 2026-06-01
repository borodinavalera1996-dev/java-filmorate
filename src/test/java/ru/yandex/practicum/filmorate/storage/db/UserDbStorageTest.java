package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class UserDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UserDbStorage userStorage;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    };

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM user_friends");
        jdbcTemplate.execute("DELETE FROM users");

        userStorage = new UserDbStorage(jdbcTemplate, userRowMapper);
    }

    @Test
    void create_ShouldSaveUserAndGenerateId() {
        User user = new User();
        user.setEmail("test@yandex.ru");
        user.setLogin("test_login");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userStorage.create(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isPositive();
        assertThat(createdUser.getEmail()).isEqualTo("test@yandex.ru");
    }

    @Test
    void get_ShouldReturnUser_WhenIdExists() {
        User user = new User();
        user.setEmail("get@yandex.ru");
        user.setLogin("get_login");
        user.setName("Get User");
        user.setBirthday(LocalDate.of(1995, 5, 5));
        User savedUser = userStorage.create(user);

        Optional<User> foundUserOpt = userStorage.get(savedUser.getId());

        assertThat(foundUserOpt).isPresent();
        assertThat(foundUserOpt.get().getLogin()).isEqualTo("get_login");
    }

    @Test
    void get_ShouldReturnEmptyOptional_WhenIdDoesNotExist() {
        Optional<User> foundUserOpt = userStorage.get(999L);

        assertThat(foundUserOpt).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllSavedUsers() {
        User user1 = new User();
        user1.setEmail("one@yandex.ru");
        user1.setLogin("one");
        user1.setName("One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("two@yandex.ru");
        user2.setLogin("two");
        user2.setName("Two");
        user2.setBirthday(LocalDate.of(1992, 2, 2));

        userStorage.create(user1);
        userStorage.create(user2);

        Collection<User> users = userStorage.findAll();

        assertThat(users).hasSize(2);
    }

    @Test
    void update_ShouldModifyExistingUserFields() {
        User user = new User();
        user.setEmail("before@yandex.ru");
        user.setLogin("before");
        user.setName("Before Name");
        user.setBirthday(LocalDate.of(1980, 10, 10));
        User savedUser = userStorage.create(user);

        savedUser.setEmail("after@yandex.ru");
        savedUser.setName("After Name");

        User updatedUser = userStorage.update(savedUser);
        Optional<User> dbUser = userStorage.get(updatedUser.getId());

        assertThat(dbUser).isPresent();
        assertThat(dbUser.get().getEmail()).isEqualTo("after@yandex.ru");
        assertThat(dbUser.get().getName()).isEqualTo("After Name");
    }

    @Test
    void addFriend_And_getFriends_ShouldManageFriendshipsCorrectly() {
        User user = new User();
        user.setEmail("user@yandex.ru");
        user.setLogin("user");
        user.setName("User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User friend = new User();
        friend.setEmail("friend@yandex.ru");
        friend.setLogin("friend");
        friend.setName("Friend");
        friend.setBirthday(LocalDate.of(2001, 1, 1));

        User savedUser = userStorage.create(user);
        User savedFriend = userStorage.create(friend);

        userStorage.addFriend(savedUser.getId(), savedFriend.getId());

        List<User> friends = userStorage.getFriends(savedUser.getId());

        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getId()).isEqualTo(savedFriend.getId());
    }

    @Test
    void deleteFriend_ShouldRemoveFriendshipConnection() {
        User user = new User();
        user.setEmail("u@yandex.ru");
        user.setLogin("u");
        user.setName("U");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User friend = new User();
        friend.setEmail("f@yandex.ru");
        friend.setLogin("f");
        friend.setName("F");
        friend.setBirthday(LocalDate.of(2001, 1, 1));

        User savedUser = userStorage.create(user);
        User savedFriend = userStorage.create(friend);

        userStorage.addFriend(savedUser.getId(), savedFriend.getId());
        userStorage.deleteFriend(savedUser.getId(), savedFriend.getId());

        List<User> friends = userStorage.getFriends(savedUser.getId());

        assertThat(friends).isEmpty();
    }

    @Test
    void getCommonFriends_ShouldReturnIntersectionOfFriends() {
        User user1 = createDummyUser("u1@yandex.ru", "u1");
        User user2 = createDummyUser("u2@yandex.ru", "u2");
        User commonFriend = createDummyUser("common@yandex.ru", "common");

        user1 = userStorage.create(user1);
        user2 = userStorage.create(user2);
        commonFriend = userStorage.create(commonFriend);

        userStorage.addFriend(user1.getId(), commonFriend.getId());
        userStorage.addFriend(user2.getId(), commonFriend.getId());

        List<User> commonFriends = userStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.get(0).getId()).isEqualTo(commonFriend.getId());
    }

    private User createDummyUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }
}
