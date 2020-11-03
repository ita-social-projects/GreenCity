package greencity.controller;

import com.google.gson.Gson;
import static greencity.ModelUtils.getPrincipal;
import greencity.service.HabitService;
import java.security.Principal;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class HabitControllerTest {

    private MockMvc mockMvc;

    @Mock
    HabitService habitService;

    @InjectMocks
    HabitController habitController;

    private Principal principal = getPrincipal();

    private static final String habitLink = "/habit";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void getAll() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Locale locale = new Locale("en");
        Gson gson = new Gson();
        String json = gson.toJson(locale);
        mockMvc.perform(get(habitLink + "?page=1")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitService).getAllHabitsByLanguageCode(pageable, locale.getLanguage());
    }
}
