package greencity.webcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceVO;
import greencity.dto.advice.AdviceViewDto;
import greencity.dto.language.LanguageDTO;
import greencity.service.AdviceService;
import greencity.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManagementAdvicesControllerTest {
    private static final String managementAdvicesLink = "/management/advices";

    private MockMvc mockMvc;

    @Mock
    private AdviceService adviceService;

    @Mock
    private LanguageService languageService;

    @Mock
    private Validator mockValidator;

    @InjectMocks
    private ManagementAdvicesController advicesController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(advicesController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(mockValidator)
            .build();
    }

    @Test
    void findAllAdvices() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        List<AdviceVO> adviceVOs = Collections.singletonList(new AdviceVO());
        PageableDto<AdviceVO> pageableDto = new PageableDto<>(adviceVOs, 1, 0, 1);
        when(adviceService.getAllAdvicesWithFilter(pageable, null)).thenReturn(pageableDto);
        List<LanguageDTO> languageDTOS = Collections.emptyList();
        when(languageService.getAllLanguages()).thenReturn(languageDTOS);

        mockMvc.perform(get(managementAdvicesLink + "?page=0&size=20"))
            .andExpect(view().name("core/management_advices"))
            .andExpect(model().attribute("pageable", pageableDto))
            .andExpect(model().attribute("languages", languageDTOS))
            .andExpect(status().isOk());

        verify(adviceService).getAllAdvicesWithFilter(pageable, null);
        verify(languageService).getAllLanguages();
    }

    @Test
    void filterAdvices() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        AdviceViewDto adviceViewDto = new AdviceViewDto("1", "", "Pro");
        String adviceViewDtoAsJson = objectMapper.writeValueAsString(adviceViewDto);

        mockMvc.perform(post(managementAdvicesLink + "/filter")
            .contentType(MediaType.APPLICATION_JSON)
            .content(adviceViewDtoAsJson))
            .andExpect(status().isOk());

        verify(adviceService).getFilteredAdvices(eq(pageable), any(AdviceViewDto.class));
    }

    @Test
    void findAllAdvicesWithQuery() throws Exception {
        String filter = "Pro";
        Pageable pageable = PageRequest.of(0, 20);
        List<AdviceVO> adviceVOs = Collections.singletonList(new AdviceVO());
        PageableDto<AdviceVO> pageableDto = new PageableDto<>(adviceVOs, 1, 0, 1);
        when(adviceService.getAllAdvicesWithFilter(pageable, filter)).thenReturn(pageableDto);
        List<LanguageDTO> languageDTOS = Collections.emptyList();
        when(languageService.getAllLanguages()).thenReturn(languageDTOS);

        mockMvc.perform(get(managementAdvicesLink + "?page=0&size=20&filter=" + filter))
            .andExpect(view().name("core/management_advices"))
            .andExpect(model().attribute("pageable", pageableDto))
            .andExpect(model().attribute("languages", languageDTOS))
            .andExpect(status().isOk());

        verify(adviceService).getAllAdvicesWithFilter(pageable, filter);
        verify(languageService).getAllLanguages();
    }

    @Test
    void findAdviceById() throws Exception {
        Long adviceId = 1L;
        mockMvc.perform(get(managementAdvicesLink + "/" + adviceId))
            .andExpect(status().isOk());

        verify(adviceService).getAdviceById(adviceId);
    }

    @Test
    void saveAdvice() throws Exception {
        AdvicePostDto advicePostDto = ModelUtils.getAdvicePostDto();
        String advicePostDtoAsJson = objectMapper.writeValueAsString(advicePostDto);

        mockMvc.perform(post(managementAdvicesLink)
            .content(advicePostDtoAsJson)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(adviceService).save(advicePostDto);
    }

    @Test
    void deleteAdviceById() throws Exception {
        Long adviceId = 1L;
        mockMvc.perform(delete(managementAdvicesLink + "/" + adviceId))
            .andExpect(status().isOk());

        verify(adviceService).delete(1L);
    }

    @Test
    void deleteAllAdvices() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        mockMvc.perform(delete(managementAdvicesLink + "/deleteAll")
            .content("[1,2]")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(adviceService).deleteAllByIds(ids);
    }

    @Test
    void updateAdvice() throws Exception {
        AdvicePostDto advicePostDto = ModelUtils.getAdvicePostDto();
        Long id = 1L;
        String advicePostDtoAsJson = objectMapper.writeValueAsString(advicePostDto);

        mockMvc.perform(put(managementAdvicesLink + "/" + id)
            .content(advicePostDtoAsJson)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(adviceService).update(advicePostDto, id);
    }

    @Test
    void unlinkAdvice() throws Exception {
        Long habitId = 1L;
        Integer[] advicesIndexes = new Integer[2];
        advicesIndexes[0] = 1;
        advicesIndexes[1] = 2;
        mockMvc.perform(delete(managementAdvicesLink + "/" + habitId + "/unlink/advice")
            .content(Arrays.toString(advicesIndexes))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(adviceService).unlinkAdvice("en", habitId, advicesIndexes);
    }
}