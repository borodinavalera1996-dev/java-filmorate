package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@SpringBootTest
public class InMemoryUserStorageTest {

    @Autowired
    public UserStorage userStorage;

    @BeforeEach
    public void clearService() {
        userStorage.clear();
    }

    @Test
    public void createUser() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userStorage.create(mockUser);

        Collection<User> all = userStorage.findAll();
        assertEquals(1, all.size());
        assertIterableEquals(List.of(mockUser), all);
    }

    @Test
    public void updateUser() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userStorage.create(mockUser);
        User update = new User(1L, "test1@fg.ru", "test1", "name1", LocalDate.of(1999, 12, 6), null);
        userStorage.update(update);

        Collection<User> all = userStorage.findAll();
        assertEquals(1, all.size());
        assertIterableEquals(List.of(update), all);
    }

    @Test
    public void createUserWhenWithEmptyName() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "lsls", "", LocalDate.of(1999, 12, 6), null);
        userStorage.create(mockUser);

        Collection<User> all = userStorage.findAll();
        assertEquals(1, all.size());
        assertIterableEquals(List.of(mockUser), all);
    }

    @Test
    public void getUser() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "lsls", "ss", LocalDate.of(1999, 12, 6), null);
        userStorage.create(mockUser);

        Optional<User> user = userStorage.get(1L);
        assertEquals(mockUser, user.get());
    }

    @Test
    public void findAll() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userStorage.create(mockUser);
        User mockUser1 = new User(2L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userStorage.create(mockUser1);

        Collection<User> all = userStorage.findAll();
        assertEquals(2, all.size());
        assertIterableEquals(List.of(mockUser, mockUser1), all);
    }

}
