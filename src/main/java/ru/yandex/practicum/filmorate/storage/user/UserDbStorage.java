package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("userDb")
public class UserDbStorage extends BaseStorage<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT u.*, \n" +
            "FROM users u\n" +
            "WHERE u.id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";

    private static final String INSERT_FRIEND_QUERY = "INSERT INTO user_friends(user_id, friend_id) " +
            "VALUES (?, ?)";
    private static final String GET_FRIENDS_QUERY = "SELECT friend_id " +
            "FROM user_friends " +
            "WHERE user_id = ?";
    private static final String GET_COMMON_FRIENDS_QUERY = "SELECT f1.friend_id " +
            " FROM user_friends f1 " +
            " JOIN user_friends f2 ON f1.friend_id = f2.friend_id " +
            " WHERE f1.user_id = ? AND f2.user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User create(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_FRIEND_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, userId);
            ps.setObject(2, friendId);
            return ps;
        }, keyHolder);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        jdbc.update(DELETE_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public List<Long> getFriends(long id) {
        List<Long> users = jdbc.queryForList(GET_FRIENDS_QUERY, Long.class, id);

        return users;
    }

    @Override
    public List<Long> getCommonFriends(long id, long otherId) {
        List<Long> users = jdbc.queryForList(GET_COMMON_FRIENDS_QUERY, Long.class, id, otherId);
        return users;
    }

    @Override
    public Optional<User> get(long id) {
        Optional<User> user = findOne(FIND_BY_ID_QUERY, id);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public void clear() {

    }
}
