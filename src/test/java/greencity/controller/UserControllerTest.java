package greencity.controller;

import greencity.service.UserService;
import greencity.service.UserValidationService;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserValidationService userValidationService;

    @Test
    @WithMockUser
    public void bulkDeleteUserGoalsWithEmptyInputIdsTest() throws Exception {
        mockMvc.perform(delete("/user/1/userGoals").param("ids", ""))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void bulkDeleteUserGoalsWithInvalidInputIdsTest() throws Exception {
        mockMvc.perform(delete("/user/1/userGoals").param("ids", "foo"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void bulkDeleteUserGoalsWithTrailingCommaInputIdsTest() throws Exception {
        mockMvc.perform(delete("/user/1/userGoals").param("ids", "1,2,3,"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void bulkDeleteUserGoalsWithValidInputIdTest() throws Exception {
        when(userValidationService.userValidForActions(any(), anyLong())).thenReturn(null);
        when(userService.deleteUserGoals("1")).thenReturn(Collections.emptyList());
        mockMvc.perform(delete("/user/1/userGoals").param("ids", "1"))
            .andExpect(status().isOk());
    }
}
