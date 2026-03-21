package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

//методы добавления, удаления и модификации объектов.
public interface UserStorage {

    User create(User user);

    User get(long id);

    Collection<User> findAll();

    User update(User newUser);

    void clear();
}
