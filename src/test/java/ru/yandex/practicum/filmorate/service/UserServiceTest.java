package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    public UserService userService;

    @AfterEach
    public void clearService() {
        userService.clear();
    }

    @Test
    public void shouldReturnUsers() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userService.create(mockUser);

        Collection<User> all = userService.findAll();
        assertEquals(1, all.size());
        assertIterableEquals(List.of(mockUser), all);
    }

    @Test
    public void shouldCreateUser() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        User user = userService.create(mockUser);

        assertEquals(user, mockUser);
    }

    @Test
    public void shouldCreateUserWithLoginWithSpace() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", " ", "name", LocalDate.of(1999, 12, 6), null);
        assertThrows(ValidationException.class, () -> userService.create(mockUser));
    }

    @Test
    public void shouldCreateUserWithEmptyName() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "name", "", LocalDate.of(1999, 12, 6), null);
        User mockUser1 = new User(1L, "test@fg.ru", "name", "name", LocalDate.of(1999, 12, 6), null);
        User user = userService.create(mockUser);

        assertEquals(user, mockUser1);
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "name", "name", LocalDate.of(1999, 12, 6), null);
        User user = userService.create(mockUser);
        User mockUser1 = new User(1L, "test1@fg.ru", "name1", "name1", LocalDate.of(1999, 12, 6), null);
        userService.update(mockUser1);
        assertEquals(user, mockUser1);
    }

    @Test
    public void shouldGetUser() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userService.create(mockUser);

        User user = userService.getUser(1L);
        assertEquals(user, mockUser);
    }

    @Test
    public void shouldGetUserNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> userService.getUser(1L));
    }

    @Test
    public void shouldAddFriend() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userService.create(mockUser);
        User mockUser1 = new User(2L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userService.create(mockUser1);
        userService.addFriend(1L, 2L);

        Collection<User> all = userService.findAll();
        assertEquals(2, all.size());
        assertEquals(all.iterator().next().getFriends().size(), 1);
    }

    @Test
    public void shouldAddFriendNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> userService.addFriend(1L, 2L));
    }

    @Test
    public void shouldDeleteFriend() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userService.create(mockUser);
        User mockUser1 = new User(2L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userService.create(mockUser1);
        userService.addFriend(1L, 2L);

        userService.deleteFriend(1L, 2L);

        Collection<User> all = userService.findAll();
        assertEquals(2, all.size());
        assertEquals(all.iterator().next().getFriends().size(), 0);
    }

    @Test
    public void shouldDeleteFriendNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> userService.deleteFriend(1L, 2L));
    }

    @Test
    public void shouldGetFriends() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userService.create(mockUser);
        User mockUser1 = new User(2L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userService.create(mockUser1);
        userService.addFriend(1L, 2L);

        List<User> friends = userService.getFriends(1L);

        assertEquals(1, friends.size());
    }

    @Test
    public void shouldGetFriendsNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> userService.getFriends(1L));
    }

    @Test
    public void shouldGetCommonFriends() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userService.create(mockUser);
        User mockUser1 = new User(2L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userService.create(mockUser1);
        User mockUser2 = new User(3L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userService.create(mockUser2);
        userService.addFriend(1L, 2L);
        userService.addFriend(1L, 3L);
        userService.addFriend(2L, 3L);

        List<User> friends = userService.getCommonFriends(1L, 2L);

        assertEquals(1, friends.size());
    }
}
