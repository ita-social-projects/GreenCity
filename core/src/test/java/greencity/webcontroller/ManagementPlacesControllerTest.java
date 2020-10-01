package greencity.webcontroller;

import greencity.dto.PageableDto;
import greencity.dto.category.CategoryDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.specification.SpecificationNameDto;
import greencity.service.CategoryService;
import greencity.service.PlaceService;
import greencity.service.SpecificationService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(managementPlacesController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void getAllPlaces() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<AdminPlaceDto> placeDtos = Collections.singletonList(new AdminPlaceDto());
        PageableDto<AdminPlaceDto>
            adminPlaceDtoPageableDto = new PageableDto<>(placeDtos, 1, 0, 1);
        when(placeService.findAll(pageable)).thenReturn(adminPlaceDtoPageableDto);
        when(categoryService.findAllCategoryDto()).thenReturn(Collections.singletonList(new CategoryDto("test")));
        when(specificationService.findAllSpecificationDto())
            .thenReturn(Collections.singletonList(new SpecificationNameDto()));

        this.mockMvc.perform(get("/management/places")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(view().name("core/management_places"))
            .andExpect(model().attribute("pageable", adminPlaceDtoPageableDto))
            .andExpect(status().isOk());

        verify(placeService).findAll(pageable);
        verify(categoryService).findAllCategoryDto();
        verify(specificationService).findAllSpecificationDto();
    }

    @Test
    void savePlace() throws Exception {
        this.mockMvc.perform(post("/management/places/")
            .content("{\n" +
                "  \"category\": {\n" +
                "   \"name\": \"Food\"\n" +
                "  },\n" +
                "  \"status\": \"APPROVED\",\n" +
                "  \"discountValues\": null,\n" +
                "  \"location\": {\n" +
                "    \"address\": \"string\",\n" +
                "    \"lat\": 111,\n" +
                "    \"lng\": 111\n" +
                "  },\n" +
                "  \"name\": \"string\",\n" +
                "  \"openingHoursList\":null,\n" +
                "  \"photos\": null\n" +
                "}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(placeService).save(any(), any());
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