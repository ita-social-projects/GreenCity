package greencity.webcontroller;

import greencity.dto.PageableDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.service.PlaceService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        this.mockMvc.perform(get("/management/places")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(view().name("core/management_places"))
            .andExpect(model().attribute("pageable", adminPlaceDtoPageableDto))
            .andExpect(status().isOk());

        verify(placeService).findAll(pageable);
    }

}