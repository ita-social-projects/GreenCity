package greencity.controller;

import greencity.enums.TagType;
import greencity.service.TagsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TagsControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TagsController tagsController;

    @Mock
    private TagsService tagsService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(tagsController).build();
    }

    @Test
    void findByTypeTest() throws Exception {
        mockMvc.perform(get("/tags?type=" + TagType.ECO_NEWS))
            .andExpect(status().isOk());

        verify(tagsService).findByType(TagType.ECO_NEWS);
    }
}