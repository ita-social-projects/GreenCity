package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import greencity.ModelUtils;
import greencity.dto.habit.CustomHabitDtoRequest;
import greencity.dto.user.UserVO;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitService;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import greencity.service.TagsService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static greencity.ModelUtils.getPrincipal;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

@ExtendWith(MockitoExtension.class)
class HabitControllerTest {

    private MockMvc mockMvc;

    @Mock
    HabitService habitService;

    @Mock
    TagsService tagsService;

    @InjectMocks
    HabitController habitController;

    @Mock
    private Validator mockValidator;

    private static final String habitLink = "/habit";

    private final Principal principal = getPrincipal();

    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
            .setValidator(mockValidator)
            .build();
    }

    @Test
    void getAll() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        UserVO userVO = new UserVO();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(habitLink + "?page=1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitService).getAllHabitsByLanguageCode(userVO, pageable);
    }

    @Test
    void getAllByTagsAndLanguageCode() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Locale locale = Locale.of("en");
        List<String> tags = Arrays.asList("News", "Education");

        mockMvc.perform(get(habitLink + "/tags/search?page=" + pageNumber +
            "&lang=" + locale.getLanguage() + "&tags=News,Education")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitService).getAllByTagsAndLanguageCode(pageable, tags, locale.getLanguage());
    }

    @Test
    void findAllHabitsTags() throws Exception {
        Locale locale = Locale.of("en");
        Gson gson = new Gson();

        mockMvc.perform(get(habitLink + "/tags")
            .content(gson.toJson(locale))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(tagsService).findAllHabitsTags(locale.getLanguage());
    }

    private Pageable createPageRequest() {
        int pageNumber = 0;
        int pageSize = 20;
        return PageRequest.of(pageNumber, pageSize);
    }

    @Test
    void findByDifferentParameters() throws Exception {
        Locale locale = Locale.of("en");
        Gson gson = new Gson();
        UserVO userVO = new UserVO();

        mockMvc.perform(get(habitLink + "/search")
            .param("tags", "reusable")
            .param("complexities", "1")
            .param("isCustomHabit", "true")
            .content(gson.toJson(locale))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitService).getAllByDifferentParameters(userVO, createPageRequest(), Optional.of(List.of("reusable")),
            Optional.of(true),
            Optional.of(List.of(1)),
            locale.getLanguage());
    }

    @Test
    void findByDifferentParametersWithComplexityAndTags() throws Exception {
        Locale locale = Locale.of("en");
        Gson gson = new Gson();
        UserVO userVO = new UserVO();
        mockMvc.perform(get(habitLink + "/search")
            .param("tags", "reusable")
            .param("complexities", "1")
            .content(gson.toJson(locale))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitService).getAllByDifferentParameters(userVO, createPageRequest(), Optional.of(List.of("reusable")),
            Optional.empty(),
            Optional.of(List.of(1)),
            locale.getLanguage());
    }

    @Test
    void findByDifferentParametersWithComplexityAndIsCustomHabit() throws Exception {
        Locale locale = Locale.of("en");
        Gson gson = new Gson();
        UserVO userVO = new UserVO();
        mockMvc.perform(get(habitLink + "/search")
            .param("complexities", "1")
            .param("isCustomHabit", "true")
            .content(gson.toJson(locale))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitService).getAllByDifferentParameters(userVO, createPageRequest(), Optional.empty(),
            Optional.of(true),
            Optional.of(List.of(1)),
            locale.getLanguage());
    }

    @Test
    void findByDifferentParametersWithTagsAndIsCustomHabit() throws Exception {
        Locale locale = Locale.of("en");
        Gson gson = new Gson();
        UserVO userVO = new UserVO();
        mockMvc.perform(get(habitLink + "/search")
            .param("tags", "reusable")
            .param("isCustomHabit", "true")
            .content(gson.toJson(locale))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitService).getAllByDifferentParameters(userVO, createPageRequest(), Optional.of(List.of("reusable")),
            Optional.of(true),
            Optional.empty(),
            locale.getLanguage());
    }

    @Test
    void findByDifferentParametersWithComplexity() throws Exception {
        Locale locale = Locale.of("en");
        Gson gson = new Gson();
        UserVO userVO = new UserVO();
        mockMvc.perform(get(habitLink + "/search")
            .param("complexities", "1")
            .content(gson.toJson(locale))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitService).getAllByDifferentParameters(userVO, createPageRequest(), Optional.empty(),
            Optional.empty(),
            Optional.of(List.of(1)),
            locale.getLanguage());
    }

    @Test
    void findByDifferentParametersWithIsCustomHabit() throws Exception {
        Locale locale = Locale.of("en");
        Gson gson = new Gson();
        UserVO userVO = new UserVO();
        mockMvc.perform(get(habitLink + "/search")
            .param("isCustomHabit", "true")
            .content(gson.toJson(locale))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitService).getAllByDifferentParameters(userVO, createPageRequest(), Optional.empty(),
            Optional.of(true),
            Optional.empty(),
            locale.getLanguage());
    }

    @Test
    void findByDifferentParametersWithTags() throws Exception {
        Locale locale = Locale.of("en");
        Gson gson = new Gson();
        UserVO userVO = new UserVO();
        mockMvc.perform(get(habitLink + "/search")
            .param("tags", "reusable")
            .content(gson.toJson(locale))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitService).getAllByDifferentParameters(userVO, createPageRequest(), Optional.of(List.of("reusable")),
            Optional.empty(),
            Optional.empty(),
            locale.getLanguage());
    }

    @Test
    void findByDifferentParametersBadRequest() throws Exception {
        Locale locale = Locale.of("en");
        Gson gson = new Gson();
        mockMvc.perform(get(habitLink + "/search")
            .content(gson.toJson(locale))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findByDifferentParametersBadRequestWithEmptyList() throws Exception {
        Locale locale = Locale.of("en");
        Gson gson = new Gson();
        mockMvc.perform(get(habitLink + "/search")
            .param("tags", "")
            .content(gson.toJson(locale))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
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
        CustomHabitDtoRequest dto = ModelUtils.getAddCustomHabitDtoRequest();
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

    @Test
    void getFriendsAssignedToHabitProfilePictures() throws Exception {
        Long habitId = 1L;

        mockMvc.perform(get(habitLink + "/{habitId}/friends/profile-pictures", habitId))
            .andExpect(status().isOk());

        verify(habitService).getFriendsAssignedToHabitProfilePictures(habitId, null);
    }

    @Test
    @SneakyThrows
    void updateCustomHabit() {
        Long habitId = 1L;
        byte[] imageContent = "sampleImageData".getBytes();
        MockMultipartFile imageFile = new MockMultipartFile("image", imageContent);
        CustomHabitDtoRequest dto = ModelUtils.getAddCustomHabitDtoRequest();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        String requestedJson = objectMapper.writeValueAsString(dto);
        MockMultipartFile jsonFile = new MockMultipartFile("request", "",
            "application/json", requestedJson.getBytes());

        MockMultipartHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.multipart(habitLink + "/update/{habitId}", habitId);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });
        mockMvc.perform(builder
            .file(jsonFile)
            .file(imageFile)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(habitService).updateCustomHabit(dto, habitId, principal.getName(), imageFile);
    }

    @Test
    void deleteHabitAssignTest() throws Exception {
        Long customHabitId = 1L;
        Principal principal = () -> "email@ukr.net";
        mockMvc.perform(delete("/habit/delete/{customHabitId}", customHabitId)
            .principal(principal)).andExpect(status().isOk());

        verify(habitService).deleteCustomHabit(customHabitId, principal.getName());
    }
}
