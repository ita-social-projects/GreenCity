package greencity.webcontroller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.google.gson.Gson;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitManagementDto;
import greencity.dto.language.LanguageDTO;
import greencity.service.LanguageService;
import greencity.service.ManagementHabitService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AboutUsControllerTest {
    private static final String managementLink = "/management/aboutus";

    private MockMvc mockMvc;

    @Mock
    private ManagementHabitService managementHabitService;

    @Mock
    private LanguageService languageService;

    @InjectMocks
    AboutUsController aboutUsController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(aboutUsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void goToIndexTest() throws Exception {
        this.mockMvc.perform(get(managementLink))
            .andExpect(view().name("core/about_us"))
            .andExpect(status().isOk());
    }

}
