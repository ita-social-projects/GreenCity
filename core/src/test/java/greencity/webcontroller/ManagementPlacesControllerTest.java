package greencity.webcontroller;

import greencity.converters.UserIdArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.category.CategoryDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.specification.SpecificationNameDto;
import greencity.security.jwt.JwtTool;
import greencity.service.CategoryService;
import greencity.service.PlaceService;
import greencity.service.SpecificationService;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManagementPlacesControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ManagementPlacesController managementPlacesController;

    @Mock
    private PlaceService placeService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private SpecificationService specificationService;

    @Mock
    JwtTool jwtTool;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(managementPlacesController)
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(),
                new UserIdArgumentResolver(jwtTool))
            .build();
    }

    @Test
    void getAllPlaces() throws Exception {
        List<AdminPlaceDto> placeDtos = Collections.singletonList(new AdminPlaceDto());
        PageableDto<AdminPlaceDto> adminPlaceDtoPageableDto = new PageableDto<>(placeDtos, 1, 0, 1);
        when(placeService.getFilteredPlacesForAdmin(any(), any())).thenReturn(adminPlaceDtoPageableDto);
        when(categoryService.findAllCategoryDto())
            .thenReturn(Collections.singletonList(new CategoryDto(1L, "test", "test", null)));
        when(specificationService.findAllSpecificationDto())
            .thenReturn(Collections.singletonList(new SpecificationNameDto()));

        this.mockMvc.perform(get("/management/places")
            .param("page", "0")
            .param("size", "1"))
            .andExpect(view().name("core/management_places"))
            .andExpect(model().attribute("pageable", adminPlaceDtoPageableDto))
            .andExpect(status().isOk());

        verify(placeService).getFilteredPlacesForAdmin(any(), any());
        verify(categoryService).findAllCategoryDto();
        verify(specificationService).findAllSpecificationDto();
    }

    @Test
    void getPlaceByIdTest() throws Exception {
        this.mockMvc.perform(get("/management/places/find?id=1")).andExpect(status().isOk());
        verify(placeService).getInfoForUpdatingById(1L);
    }

    @Test
    void savePlace() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        String json = """
            {
                "name": "Тестове місце",
                "address": "Смиків, південна 7",
                "categoryId": "1",
                "openingHoursList": [
                    {
                        "weekDay": "MONDAY",
                        "openTime": "17:34",
                        "closeTime": "19:34",
                        "breakTime": null
                    }
                ]
            }
            """;

        MockMultipartFile addPlaceDto = new MockMultipartFile(
            "addPlaceDto",
            "",
            "application/json",
            (json)
                .getBytes());

        this.mockMvc.perform(multipart("/management/places/")
            .file(addPlaceDto)
            .principal(principal)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isOk());

        verify(placeService).save(any(), any(), any());
    }

    @Test
    void updatePlaceTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        String json = """
            {
                "id": "1",
                "name": "Тестове місце",
                "address": "Смиків, південна 7",
                "categoryId": "1",
                "openingHoursList": [
                    {
                        "weekDay": "MONDAY",
                        "openTime": "17:34",
                        "closeTime": "19:34",
                        "breakTime": null
                    }
                ]
            }
            """;

        MockMultipartFile addPlaceDto = new MockMultipartFile(
                "placeUpdateDto",
                "",
                "application/json",
                (json)
                        .getBytes());

        this.mockMvc.perform(multipart(HttpMethod.PUT, "/management/places/")
                        .file(addPlaceDto)
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());

        verify(placeService).update(any(), any(), any());
    }

    @Test
    void updatePlaceWithoutIdTest() throws Exception {
        String json = """
                {
                    "name": "test",
                    "category": {
                        "name": "Food"
                    },
                    "discountValues": [{"value": 10}],
                    "location": {
                        "address": "address",
                        "lat": 111.1,
                        "lng": 111.1,
                        "addressUa": "адреса"
                    },
                    "openingHoursList": [{"dayOfWeek": "MONDAY", "openTime": "08:00", "closeTime": "22:00"}]
                }
            """;

        mockMvc.perform(multipart("/management/places/")
            .file(new MockMultipartFile(
                "placeUpdateDto",
                "placeUpdateDto.json",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes()))
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isBadRequest());

//        verify(placeService, never()).updateFromUI(any(PlaceUpdateDto.class), any(), any());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/management/places/?id=1"))
            .andExpect(status().isOk());

        verify(placeService).deleteById(1L);

    }

    @Test
    void deleteAll() throws Exception {
        List<Long> listId = Arrays.asList(1L, 2L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/management/places/deleteAll")
            .content("[1,2]")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(placeService).bulkDelete(listId);

    }
}
