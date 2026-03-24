package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

//методы добавления, удаления и модификации объектов.
public interface UserStorage {

    User create(User user);

    Optional<User> get(long id);

    Collection<User> findAll();

    User update(User newUser);

    void clear();
}
