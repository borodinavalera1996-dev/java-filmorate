package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDto validUserDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        validUserDto = new UserDto();
        validUserDto.setId(1L);
        validUserDto.setEmail("user@yandex.ru");
        validUserDto.setLogin("user_login");
        validUserDto.setName("UserName");
        validUserDto.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void create_ShouldReturnCreatedUser_WhenRequestIsValid() throws Exception {
        NewUserRequest request = new NewUserRequest();
        request.setEmail("user@yandex.ru");
        request.setLogin("user_login");
        request.setName("UserName");
        request.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.create(any(NewUserRequest.class))).thenReturn(validUserDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(validUserDto.getId()))
                .andExpect(jsonPath("$.email").value(validUserDto.getEmail()))
                .andExpect(jsonPath("$.login").value(validUserDto.getLogin()));

        verify(userService, times(1)).create(any(NewUserRequest.class));
    }

    @Test
    void update_ShouldReturnUpdatedUser_WhenRequestIsValid() throws Exception {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setId(1L);
        updateRequest.setEmail("updated@yandex.ru");
        updateRequest.setLogin("updated_login");
        updateRequest.setName("UpdatedName");
        updateRequest.setBirthday(LocalDate.of(2000, 1, 1));

        validUserDto.setEmail("updated@yandex.ru");
        validUserDto.setLogin("updated_login");
        validUserDto.setName("UpdatedName");

        when(userService.update(any(UpdateUserRequest.class))).thenReturn(validUserDto);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@yandex.ru"))
                .andExpect(jsonPath("$.login").value("updated_login"))
                .andExpect(jsonPath("$.name").value("UpdatedName"));

        verify(userService, times(1)).update(any(UpdateUserRequest.class));
    }

    @Test
    void getUsers_ShouldReturnListOfUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(validUserDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(validUserDto.getId()));

        verify(userService, times(1)).findAll();
    }

    @Test
    void getUser_ShouldReturnUser_WhenIdIsValid() throws Exception {
        when(userService.getUser(1L)).thenReturn(validUserDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.login").value("user_login"));

        verify(userService, times(1)).getUser(1L);
    }

    @Test
    void addFriend_ShouldReturnOk_WhenParametersAreValid() throws Exception {
        doNothing().when(userService).addFriend(anyLong(), anyLong());

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());

        verify(userService, times(1)).addFriend(1L, 2L);
    }

    @Test
    void deleteFriend_ShouldReturnOk_WhenParametersAreValid() throws Exception {
        doNothing().when(userService).deleteFriend(anyLong(), anyLong());

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteFriend(1L, 2L);
    }

    @Test
    void getFriends_ShouldReturnFriendsCollection() throws Exception {
        when(userService.getFriends(1L)).thenReturn(List.of(validUserDto));

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(userService, times(1)).getFriends(1L);
    }

    @Test
    void getCommonFriends_ShouldReturnCommonFriendsList() throws Exception {
        UserDto commonFriend = new UserDto();
        commonFriend.setId(3L);
        commonFriend.setLogin("common_friend");

        when(userService.getCommonFriends(1L, 2L)).thenReturn(List.of(commonFriend));

        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3L));

        verify(userService, times(1)).getCommonFriends(1L, 2L);
    }
}
