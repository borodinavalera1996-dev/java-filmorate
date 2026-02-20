package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6));
        userService.create(mockUser);

        Collection<User> all = userService.findAll();
        assertEquals(1, all.size());
        assertIterableEquals(List.of(mockUser), all);
    }

    @Test
    public void shouldCreateUser() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6));
        User user = userService.create(mockUser);

        assertEquals(user, mockUser);
    }

    @Test
    public void shouldCreateUserWithLoginWithSpace() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", " ", "name", LocalDate.of(1999, 12, 6));
        assertThrows(ValidationException.class, () -> userService.create(mockUser));
    }

    @Test
    public void shouldCreateUserWithEmptyName() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "name", "", LocalDate.of(1999, 12, 6));
        User mockUser1 = new User(1L, "test@fg.ru", "name", "name", LocalDate.of(1999, 12, 6));
        User user = userService.create(mockUser);

        assertEquals(user, mockUser1);
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "name", "name", LocalDate.of(1999, 12, 6));
        User user = userService.create(mockUser);
        User mockUser1 = new User(1L, "test1@fg.ru", "name1", "name1", LocalDate.of(1999, 12, 6));
        userService.update(mockUser1);
        assertEquals(user, mockUser1);
    }
}
