package greencity.controller;

import com.google.gson.Gson;
import greencity.service.TagsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class TagsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TagsService tagsService;

    @InjectMocks
    private TagsController tagsController;

    private static final String TAGS_LINK = "/tags";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(tagsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void findAllHabitsTags() throws Exception {
        String language = "en";

        mockMvc.perform(get(TAGS_LINK + "?lang=" + language)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(tagsService).findAllTags(language);
    }
}