package greencity.controller;

import greencity.service.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FriendControllerTest {
    private MockMvc mockMvc;

    @Mock
    FriendService friendService;

    @InjectMocks
    FriendController friendController;

    private static final String FRIEND_LINK = "/friends";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(friendController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void deleteUserFriendTest() throws Exception {
        Long friendId = 1L;
        mockMvc.perform(delete(FRIEND_LINK + "/{friendId}", friendId))
            .andExpect(status().isOk());

        verify(friendService).deleteUserFriendById(null, friendId);
    }

    @Test
    void addNewFriendTest() throws Exception {
        Long friendId = 1L;
        mockMvc.perform(post(FRIEND_LINK + "/{friendId}", friendId))
            .andExpect(status().isOk());

        verify(friendService).addNewFriend(null, friendId);
    }

    @Test
    void acceptFriendRequestTest() throws Exception {
        Long friendId = 1L;
        mockMvc.perform(post(FRIEND_LINK + "/{friendId}", friendId))
                .andExpect(status().isOk());

        verify(friendService).acceptFriendRequest(null, friendId);
    }
}
