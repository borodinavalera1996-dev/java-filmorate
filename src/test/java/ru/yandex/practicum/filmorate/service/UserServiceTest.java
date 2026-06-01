package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserStorage userStorage;

    private User validUser;
    private User friendUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(userStorage);

        validUser = new User();
        validUser.setId(1L);
        validUser.setEmail("user@yandex.ru");
        validUser.setLogin("user_login");
        validUser.setName("UserName");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));

        friendUser = new User();
        friendUser.setId(2L);
        friendUser.setEmail("friend@yandex.ru");
        friendUser.setLogin("friend_login");
        friendUser.setName("FriendName");
        friendUser.setBirthday(LocalDate.of(2001, 2, 2));
    }

    @Test
    void findAll_ShouldReturnMappedUserDtos() {
        when(userStorage.findAll()).thenReturn(List.of(validUser));

        Collection<UserDto> result = userService.findAll();

        assertThat(result).hasSize(1);
        verify(userStorage, times(1)).findAll();
    }

    @Test
    void create_ShouldSaveUser_WhenRequestIsValid() {
        NewUserRequest request = new NewUserRequest();
        request.setEmail("user@yandex.ru");
        request.setLogin("user_login");
        request.setName("UserName");
        request.setBirthday(LocalDate.of(2000, 1, 1));

        when(userStorage.create(any(User.class))).thenReturn(validUser);

        UserDto createdDto = userService.create(request);

        assertThat(createdDto).isNotNull();
        assertThat(createdDto.getLogin()).isEqualTo("user_login");
        verify(userStorage, times(1)).create(any(User.class));
    }

    @Test
    void create_ShouldUseLoginAsName_WhenNameIsEmptyOrBlank() {
        NewUserRequest request = new NewUserRequest();
        request.setEmail("user@yandex.ru");
        request.setLogin("no_name_login");
        request.setName(""); // Пустое имя должно замениться на логин
        request.setBirthday(LocalDate.of(2000, 1, 1));

        User mockReturnedUser = new User();
        mockReturnedUser.setId(3L);
        mockReturnedUser.setLogin("no_name_login");
        mockReturnedUser.setName("no_name_login");
        mockReturnedUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userStorage.create(any(User.class))).thenReturn(mockReturnedUser);

        UserDto createdDto = userService.create(request);

        assertThat(createdDto.getName()).isEqualTo("no_name_login");
    }

    @Test
    void create_ShouldThrowValidationException_WhenLoginContainsSpaces() {
        NewUserRequest request = new NewUserRequest();
        request.setLogin("invalid login");
        request.setEmail("user@yandex.ru");

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Логин не может быть пустым и содержать пробелы");

        verify(userStorage, never()).create(any());
    }

    @Test
    void getUser_ShouldReturnUserDto_WhenIdExists() {
        when(userStorage.get(1L)).thenReturn(Optional.of(validUser));

        UserDto result = userService.getUser(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getUser_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        when(userStorage.get(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден с ID: 999");
    }

    @Test
    void update_ShouldModifyFields_WhenUserExists() {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setId(1L);
        updateRequest.setEmail("new@yandex.ru");
        updateRequest.setLogin("new_login");

        when(userStorage.get(1L)).thenReturn(Optional.of(validUser));
        when(userStorage.update(any(User.class))).thenReturn(validUser);

        UserDto result = userService.update(updateRequest);

        assertThat(result).isNotNull();
        verify(userStorage, times(1)).update(any(User.class));
    }

    @Test
    void addFriend_ShouldCallStorage_WhenUsersAreDifferent() {
        when(userStorage.get(1L)).thenReturn(Optional.of(validUser));
        when(userStorage.get(2L)).thenReturn(Optional.of(friendUser));

        userService.addFriend(1L, 2L);

        verify(userStorage, times(1)).addFriend(1L, 2L);
    }

    @Test
    void addFriend_ShouldNotCallStorage_WhenUserAddsThemselves() {
        when(userStorage.get(1L)).thenReturn(Optional.of(validUser));

        userService.addFriend(1L, 1L);

        verify(userStorage, never()).addFriend(1L, 1L);
    }

    @Test
    void getFriends_ShouldReturnFriendList() {
        when(userStorage.get(1L)).thenReturn(Optional.of(validUser));
        when(userStorage.getFriends(1L)).thenReturn(List.of(friendUser));

        List<UserDto> friends = userService.getFriends(1L);

        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getId()).isEqualTo(2L);
    }

    @Test
    void getCommonFriends_ShouldReturnIntersection() {
        User common = new User();
        common.setId(3L);
        common.setLogin("common");

        when(userStorage.getCommonFriends(1L, 2L)).thenReturn(List.of(common));

        List<UserDto> commonFriends = userService.getCommonFriends(1L, 2L);

        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.get(0).getId()).isEqualTo(3L);
    }
}
