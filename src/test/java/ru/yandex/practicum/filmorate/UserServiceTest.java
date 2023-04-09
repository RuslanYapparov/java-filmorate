package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.UserRestCommand;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.impl.UserStorage;

public class UserServiceTest {
    private static UserService service;
    private static UserStorage userStorage;
    private static User user;

    private static User friend;

    @BeforeAll
    public static void initialize() {
        userStorage = new UserStorage();
        userStorage.save(User.builder()
                .email("cat@dog.ru")
                .login("kill_all_human")
                .name("Василий")
                .birthday(LocalDate.of(1999, 1, 1))
                .build());
        userStorage.save(User.builder()
                .email("job@dog.ru")
                .login("kiss_all_human")
                .name("Василиса")
                .birthday(LocalDate.of(1950, 1, 1))
                .build());
        service = new UserServiceImpl(userStorage);
        user = userStorage.getById(1);
        friend = userStorage.getById(2);
    }

    @Test
    public void shouldMakeAndUnmakeFriends() {
        service.addUserToAnotherUserFriendsSet(user.getId(), friend.getId());
        assertEquals(1, user.getFriends().size());
        assertEquals(1, friend.getFriends().size());
        assertTrue(user.getFriends().contains(friend.getId()));
        assertTrue(friend.getFriends().contains(user.getId()));
        service.removeUserFromAnotherUserFriendsSet(user.getId(), friend.getId());
        assertEquals(0, user.getFriends().size());
        assertEquals(0, friend.getFriends().size());
        assertFalse(user.getFriends().contains(friend.getId()));
        assertFalse(friend.getFriends().contains(user.getId()));
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
        User commonFriend1 = User.builder()
                .email("pig@dog.ru")
                .login("miss_all_human")
                .name("Иннокентий")
                .birthday(LocalDate.of(1989, 1, 1))
                .build();
        commonFriend1 = userStorage.save(commonFriend1);
        service.addUserToAnotherUserFriendsSet(user.getId(), commonFriend1.getId());
        service.addUserToAnotherUserFriendsSet(friend.getId(), commonFriend1.getId());
        List<UserRestCommand> commonFriendsList = service.getCommonFriendsOfTwoUsers(user.getId(), friend.getId());
        assertEquals(1, commonFriendsList.size());
        assertEquals(commonFriend1, commonFriendsList.get(0).convertToDomainObject());

        service.addUserToAnotherUserFriendsSet(user.getId(), friend.getId());
        commonFriendsList = service.getCommonFriendsOfTwoUsers(user.getId(), commonFriend1.getId());
        assertEquals(1, commonFriendsList.size());
        assertEquals(friend, commonFriendsList.get(0).convertToDomainObject());
        service.removeUserFromAnotherUserFriendsSet(user.getId(), friend.getId());

        User commonFriend2 = User.builder()
                .email("cow@dog.ru")
                .login("lick_all_human")
                .name("Тамара")
                .birthday(LocalDate.of(2010, 1, 1))
                .build();
        commonFriend2 = userStorage.save(commonFriend2);
        service.addUserToAnotherUserFriendsSet(friend.getId(), commonFriend2.getId());
        service.addUserToAnotherUserFriendsSet(user.getId(), commonFriend2.getId());
        commonFriendsList = service.getCommonFriendsOfTwoUsers(user.getId(), friend.getId());
        assertEquals(2, commonFriendsList.size());
        assertTrue(commonFriendsList.contains(new UserRestCommand(commonFriend1)));
        assertTrue(commonFriendsList.contains(new UserRestCommand(commonFriend2)));

        service.removeUserFromAnotherUserFriendsSet(user.getId(), commonFriend1.getId());
        service.removeUserFromAnotherUserFriendsSet(user.getId(), commonFriend2.getId());
        service.removeUserFromAnotherUserFriendsSet(friend.getId(), commonFriend1.getId());
        service.removeUserFromAnotherUserFriendsSet(friend.getId(), commonFriend2.getId());
    }

    @Test
    public void shouldReturnListOfFriends() {
        List<UserRestCommand> friendsList = service.getUsersFriendsSet(user.getId());
        assertNotNull(friendsList);
        assertTrue(friendsList.isEmpty());
    }

}