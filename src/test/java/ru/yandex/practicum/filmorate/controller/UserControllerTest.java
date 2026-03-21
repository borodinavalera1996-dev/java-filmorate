package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void shouldReturnUsers() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        when(userService.findAll()).thenReturn(List.of(mockUser));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\n" +
                        "    \"id\": 1,\n" +
                        "    \"email\": \"test@fg.ru\",\n" +
                        "    \"login\": \"test\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"birthday\": \"1999-12-06\"\n" +
                        "}]"));
    }

    @Test
    public void shouldCreateUser() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        when(userService.create(mockUser)).thenReturn(mockUser);

        mockMvc.perform(post("/users")
                        .content("{\n" +
                                "    \"id\": 1,\n" +
                                "    \"email\": \"test@fg.ru\",\n" +
                                "    \"login\": \"test\",\n" +
                                "    \"name\": \"name\",\n" +
                                "    \"birthday\": \"1999-12-06\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"email\": \"test@fg.ru\",\n" +
                        "    \"login\": \"test\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"birthday\": \"1999-12-06\"\n" +
                        "}"));
    }

    @Test
    public void shouldCreateUserWithWrongEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .content("{\n" +
                                "    \"id\": 1,\n" +
                                "    \"email\": \"testfg.ru\",\n" +
                                "    \"login\": \"test\",\n" +
                                "    \"name\": \"name\",\n" +
                                "    \"birthday\": \"1999-12-06\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldCreateUserWithEmptyEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .content("{\n" +
                                "    \"id\": 1,\n" +
                                "    \"login\": \"test\",\n" +
                                "    \"name\": \"name\",\n" +
                                "    \"birthday\": \"1999-12-06\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldCreateUserWithEmptyLogin() throws Exception {
        mockMvc.perform(post("/users")
                        .content("{\n" +
                                "    \"id\": 1,\n" +
                                "    \"email\": \"test@fg.ru\",\n" +
                                "    \"name\": \"name\",\n" +
                                "    \"birthday\": \"1999-12-06\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldCreateUserWithWrongDate() throws Exception {
        mockMvc.perform(post("/users")
                        .content("{\n" +
                                "    \"id\": 1,\n" +
                                "    \"email\": \"test@fg.ru\",\n" +
                                "    \"login\": \"test\",\n" +
                                "    \"name\": \"name\",\n" +
                                "    \"birthday\": \"199912-06\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        when(userService.update(mockUser)).thenReturn(mockUser);

        mockMvc.perform(put("/users")
                        .content("{\n" +
                                "    \"id\": 1,\n" +
                                "    \"email\": \"test@fg.ru\",\n" +
                                "    \"login\": \"test\",\n" +
                                "    \"name\": \"name\",\n" +
                                "    \"birthday\": \"1999-12-06\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"email\": \"test@fg.ru\",\n" +
                        "    \"login\": \"test\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"birthday\": \"1999-12-06\"\n" +
                        "}"));
    }

    @Test
    public void shouldGetUser() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        when(userService.getUser(1)).thenReturn(mockUser);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"email\": \"test@fg.ru\",\n" +
                        "    \"login\": \"test\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"birthday\": \"1999-12-06\"\n" +
                        "}"));
    }

    @Test
    public void shouldAddFriend() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        mockUser.getFriends().add(2L);

        when(userService.addFriend(1L, 2L)).thenReturn(mockUser);

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"email\": \"test@fg.ru\",\n" +
                        "    \"login\": \"test\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"birthday\": \"1999-12-06\"\n" +
                        "}"));
    }

    @Test
    public void shouldDeleteFriend() throws Exception {
        doNothing().when(userService).deleteFriend(1L, 1L);

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetFriends() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        when(userService.getFriends(1)).thenReturn(List.of(mockUser));

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\n" +
                        "    \"id\": 1,\n" +
                        "    \"email\": \"test@fg.ru\",\n" +
                        "    \"login\": \"test\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"birthday\": \"1999-12-06\"\n" +
                        "}]"));
    }

    @Test
    public void shouldCommonFriends() throws Exception {
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        when(userService.getCommonFriends(1, 2)).thenReturn(List.of(mockUser));

        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\n" +
                        "    \"id\": 1,\n" +
                        "    \"email\": \"test@fg.ru\",\n" +
                        "    \"login\": \"test\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"birthday\": \"1999-12-06\"\n" +
                        "}]"));
    }

}
