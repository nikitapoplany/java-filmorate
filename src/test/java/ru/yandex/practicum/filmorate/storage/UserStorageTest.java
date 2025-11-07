package ru.yandex.practicum.filmorate.storage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
public class UserStorageTest {
    private final UserDbStorage storage;

    @Test
    public void testFindById() {
        User user = storage.findById(1);
        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("email", "ivan.petrov@example.com")
                .hasFieldOrPropertyWithValue("login", "ivan_p")
                .hasFieldOrPropertyWithValue("name", "Иван Петров")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 5, 15));
    }

    @Test
    public void testFindAll() {
        List<User> users = storage.findAll().stream().toList();
        assertThat(users)
                .hasSize(3)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(1, 2, 3);

        users.forEach(user -> {
            if (user.getId() == 2) {
                assertThat(user).hasFieldOrPropertyWithValue("id", 2)
                        .hasFieldOrPropertyWithValue("email", "maria.sidorova@example.com")
                        .hasFieldOrPropertyWithValue("login", "maria_s")
                        .hasFieldOrPropertyWithValue("name", "Мария Сидорова")
                        .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1995, 8, 20));
            }
        });
    }

    @Test
    public void testUserCreate(){
        User user = User.builder()
                .email("example@mail.com")
                .login("test")
                .name("TEST")
                .birthday(LocalDate.of(1992, 1, 12))
                .build();
        User newUser = storage.create(user);

        assertThat(newUser).isEqualTo(user);
    }

    @Test
    public void testUserUpdate(){
        User original = storage.findById(2);
        User update = User.builder()
                .id(2)
                .login("maria_s")
                .name("Updated")
                .birthday(LocalDate.of(1992, 1, 12))
                .build();
        storage.update(update, original);

        assertThat(update)
                .isNotEqualTo(original)
                .hasFieldOrPropertyWithValue("name", "Updated")
                .hasFieldOrPropertyWithValue("email", "maria.sidorova@example.com");
    }

    @Test
    public void testUserDelete() {
        Assertions.assertDoesNotThrow(() -> storage.findById(1));
        storage.delete(1);
        Assertions.assertThrows(NotFoundException.class, () -> storage.findById(1));
    }

    @Test
    public void testGetFriends() {
        List<Integer> friendIds = storage.getFriends(1).stream()
                .mapToInt(User::getId)
                .boxed()
                .toList();
        assertThat(friendIds).containsExactlyInAnyOrder(2, 3);
    }

    @Test
    public void testGetCommonFriends() {
        List<Integer> commonFriends = storage.getCommonFriends(1, 3).stream()
                .mapToInt(User::getId)
                .boxed()
                .toList();
        assertThat(commonFriends).containsExactly(2);
    }

    @Test
    public void testAddFriend() {
        assertThat(storage.getFriends(3)).extracting(User::getId).doesNotContain(1);
        storage.addFriend(3, 1);
        assertThat(storage.getFriends(3)).extracting(User::getId).contains(1);
    }

    @Test
    public void testRemoveFriend() {
        assertThat(storage.getFriends(1)).extracting(User::getId).contains(2);
        storage.removeFriend(1, 2);
        assertThat(storage.getFriends(1)).extracting(User::getId).doesNotContain(2);
    }
}
