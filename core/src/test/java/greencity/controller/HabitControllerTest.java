package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import greencity.ModelUtils;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.service.HabitService;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import greencity.service.TagsService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static greencity.ModelUtils.getPrincipal;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class HabitControllerTest {

    private MockMvc mockMvc;

    @Mock
    HabitService habitService;

    @Mock
    TagsService tagsService;

    @InjectMocks
    HabitController habitController;

    private static final String habitLink = "/habit";

    private final Principal principal = getPrincipal();

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

    @Test
    void getAllByTagsAndLanguageCode() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Locale locale = new Locale("en");
        List<String> tags = Arrays.asList("News", "Education");

        mockMvc.perform(get(habitLink + "/tags/search?page=" + pageNumber +
            "&lang=" + locale.getLanguage() + "&tags=News,Education")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitService).getAllByTagsAndLanguageCode(pageable, tags, locale.getLanguage());
    }

    @Test
    void findAllHabitsTags() throws Exception {
        Locale locale = new Locale("en");
        Gson gson = new Gson();

        mockMvc.perform(get(habitLink + "/tags")
            .content(gson.toJson(locale))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(tagsService).findAllHabitsTags(locale.getLanguage());
    }

    @Test
    void getHabitById() throws Exception {

        mockMvc.perform(get(habitLink + "/{id}", 1L))
            .andExpect(status().isOk());

        verify(habitService).getByIdAndLanguageCode(1L, "en");
    }

    @Test
    void getShoppingListItems() throws Exception {

        mockMvc.perform(get(habitLink + "/{id}/shopping-list", 1L))
            .andExpect(status().isOk());

        verify(habitService).getShoppingListForHabit(1L, "en");
    }

    @Test
    void postCustomHabit() throws Exception {
        AddCustomHabitDtoRequest dto = ModelUtils.getAddCustomHabitDtoRequest();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        String requestedJson = objectMapper.writeValueAsString(dto);

        MockMultipartFile jsonFile =
            new MockMultipartFile("request", "", "application/json", requestedJson.getBytes());

        mockMvc.perform(multipart(habitLink + "/custom")
            .file(jsonFile)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
        verify(habitService).addCustomHabit(dto, null, principal.getName());
    }
}
