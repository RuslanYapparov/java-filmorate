package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapperImpl;
import ru.yandex.practicum.filmorate.model.dto.restview.UserRestView;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.UserModel;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.impl.UserStorage;

public class UserlServiceTest {
    private static UserService service;
    private static UserStorage userStorage;
    private static UserMapper userMapper;
    private static UserModel userModel;
    private static UserModel friend;

    @BeforeAll
    public static void initialize() {
        userStorage = new UserStorage();
        userStorage.save(UserModel.builder()
                .email("cat@dog.ru")
                .login("kill_all_human")
                .name("Василий")
                .birthday(LocalDate.of(1999, 1, 1))
                .build());
        userStorage.save(UserModel.builder()
                .email("job@dog.ru")
                .login("kiss_all_human")
                .name("Василиса")
                .birthday(LocalDate.of(1950, 1, 1))
                .build());
        userMapper = new UserMapperImpl();
        service = new UserServiceImpl(userStorage, userMapper);
        userModel = userStorage.getById(1);
        friend = userStorage.getById(2);
    }

    @Test
    public void shouldMakeAndUnmakeFriends() {
        service.addUserToAnotherUserFriendsSet(userModel.getId(), friend.getId());
        assertEquals(1, userModel.getFriends().size());
        assertEquals(1, friend.getFriends().size());
        assertTrue(userModel.getFriends().contains(friend.getId()));
        assertTrue(friend.getFriends().contains(userModel.getId()));
        service.removeUserFromAnotherUserFriendsSet(userModel.getId(), friend.getId());
        assertEquals(0, userModel.getFriends().size());
        assertEquals(0, friend.getFriends().size());
        assertFalse(userModel.getFriends().contains(friend.getId()));
        assertFalse(friend.getFriends().contains(userModel.getId()));
    }

    @Test
    public void shouldThrowExceptionWhenComeIncorrectId() {
        assertThrows(ObjectNotFoundInStorageException.class,
                () -> service.addUserToAnotherUserFriendsSet(1, 7));
        assertThrows(ObjectNotFoundInStorageException.class,
                () -> service.removeUserFromAnotherUserFriendsSet(0, 1));
        assertThrows(ObjectNotFoundInStorageException.class,
                () -> service.getCommonFriendsOfTwoUsers(2, -1));
    }

    @Test
    public void shouldReturnListWithCommonFriend() {
        UserModel commonFriend1 = UserModel.builder()
                .email("pig@dog.ru")
                .login("miss_all_human")
                .name("Иннокентий")
                .birthday(LocalDate.of(1989, 1, 1))
                .build();
        commonFriend1 = userStorage.save(commonFriend1);
        service.addUserToAnotherUserFriendsSet(userModel.getId(), commonFriend1.getId());
        service.addUserToAnotherUserFriendsSet(friend.getId(), commonFriend1.getId());
        List<UserRestView> commonFriendsList = service.getCommonFriendsOfTwoUsers(userModel.getId(), friend.getId());
        assertEquals(1, commonFriendsList.size());
        assertEquals(commonFriendsList.get(0), userMapper.toRestView(commonFriend1));
        service.addUserToAnotherUserFriendsSet(userModel.getId(), friend.getId());
        commonFriendsList = service.getCommonFriendsOfTwoUsers(userModel.getId(), commonFriend1.getId());
        assertEquals(1, commonFriendsList.size());
        assertEquals(commonFriendsList.get(0), userMapper.toRestView(friend));
        service.removeUserFromAnotherUserFriendsSet(userModel.getId(), friend.getId());

        UserModel commonFriend2 = UserModel.builder()
                .email("cow@dog.ru")
                .login("lick_all_human")
                .name("Тамара")
                .birthday(LocalDate.of(2010, 1, 1))
                .build();
        commonFriend2 = userStorage.save(commonFriend2);
        service.addUserToAnotherUserFriendsSet(friend.getId(), commonFriend2.getId());
        service.addUserToAnotherUserFriendsSet(userModel.getId(), commonFriend2.getId());
        commonFriendsList = service.getCommonFriendsOfTwoUsers(userModel.getId(), friend.getId());
        assertEquals(2, commonFriendsList.size());
        assertTrue(commonFriendsList.contains(userMapper.toRestView(commonFriend1)));
        assertTrue(commonFriendsList.contains(userMapper.toRestView(commonFriend2)));

        service.removeUserFromAnotherUserFriendsSet(userModel.getId(), commonFriend1.getId());
        service.removeUserFromAnotherUserFriendsSet(userModel.getId(), commonFriend2.getId());
        service.removeUserFromAnotherUserFriendsSet(friend.getId(), commonFriend1.getId());
        service.removeUserFromAnotherUserFriendsSet(friend.getId(), commonFriend2.getId());
    }

    @Test
    public void shouldReturnListOfFriends() {
        List<UserRestView> friendsList = service.getUsersFriendsSet(userModel.getId());
        assertNotNull(friendsList);
        assertTrue(friendsList.isEmpty());
    }

}