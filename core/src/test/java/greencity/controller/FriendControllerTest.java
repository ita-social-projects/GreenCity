package greencity.controller;

import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FriendControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FriendService friendService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    FriendController friendController;

    private static final String FRIEND_LINK = "/friends";
    private final Principal principal = ModelUtils.getPrincipal();
    private final UserVO userVO = ModelUtils.getUserVO();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(friendController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .build();
    }

    @Test
    void deleteUserFriendTest() throws Exception {
        long friendId = 1L;

        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        mockMvc.perform(delete(FRIEND_LINK + "/{friendId}", friendId)
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail(principal.getName());
        verify(friendService).deleteUserFriendById(userVO.getId(), friendId);
    }

    @Test
    void addNewFriendTest() throws Exception {
        long friendId = 1L;

        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        mockMvc.perform(post(FRIEND_LINK + "/{friendId}", friendId)
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail(principal.getName());
        verify(friendService).addNewFriend(userVO.getId(), friendId);
    }

    @Test
    void acceptFriendRequestTest() throws Exception {
        long friendId = 1L;

        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        mockMvc.perform(patch(FRIEND_LINK + "/{friendId}/acceptFriend", friendId)
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail(principal.getName());
        verify(friendService).acceptFriendRequest(userVO.getId(), friendId);
    }

    @Test
    void declineFriendRequestTest() throws Exception {
        long friendId = 1L;

        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        mockMvc.perform(delete(FRIEND_LINK + "/{friendId}/declineFriend", friendId)
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail(principal.getName());
        verify(friendService).declineFriendRequest(userVO.getId(), friendId);
    }

    @Test
    void findUserFriendsByUserIdTest() throws Exception {
        long userId = 1L;

        mockMvc.perform(get(FRIEND_LINK + "/user/{userId}", userId))
            .andExpect(status().isOk());

        verify(friendService).findUserFriendsByUserId(userId);
    }

    @Test
    void findAllUsersExceptMainUserAndUsersFriend() throws Exception {
        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        mockMvc.perform(get(FRIEND_LINK + "/not-friends-yet")
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail(principal.getName());
        verify(friendService).findAllUsersExceptMainUserAndUsersFriend(userVO.getId(), null, PageRequest.of(0, 20));
    }

    @Test
    void findAllUsersByFriendsOfFriends() throws Exception {
        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        mockMvc.perform(get(FRIEND_LINK + "/recommended-friends-of-friends")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(userService).findByEmail(principal.getName());
        verify(friendService).findAllUsersByFriendsOfFriends(userVO.getId(), PageRequest.of(0, 20));
    }

    @Test
    void getAllUserFriendsRequestsTest() throws Exception {
        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        mockMvc.perform(get(FRIEND_LINK + "/friendRequests")
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail(principal.getName());
        verify(friendService).getAllUserFriendRequests(userVO.getId(), PageRequest.of(0, 20));
    }

    @Test
    void findAllFriendsOfUserTest() throws Exception {
        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        String name = "name";
        mockMvc.perform(get(FRIEND_LINK + "?name=" + name)
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail(principal.getName());
        verify(friendService).findAllFriendsOfUser(userVO.getId(), name, PageRequest.of(0, 20));
    }
}
