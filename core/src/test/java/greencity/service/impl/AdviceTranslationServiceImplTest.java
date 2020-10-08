/*
package greencity.service.impl;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import greencity.dto.advice.AdvicePostDTO;
import greencity.entity.Advice;
import greencity.entity.localization.AdviceTranslation;
import greencity.repository.AdviceTranslationRepo;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
class AdviceTranslationServiceImplTest {

    @Mock
    private AdviceTranslationRepo adviceTranslationRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AdviceServiceImpl adviceService;

    @InjectMocks
    private AdviceTranslationServiceImpl adviceTranslationService;

    @Test
    void saveAdviceTranslation() {
        List<AdviceTranslation> adviceTranslations = Collections.singletonList(getAdviceTranslation());
        when(adviceTranslationRepo.saveAll(adviceTranslations)).thenReturn(adviceTranslations);
        adviceTranslationService.saveAdviceTranslation(adviceTranslations);
        verify(adviceTranslationRepo, times(1)).saveAll(adviceTranslations);
    }

    @Test
    void saveAdviceAndAdviceTranslation() {
        Advice advice = getAdvice();
        AdvicePostDTO advicePostDTO = getAdvicePostDTO();
        List<AdviceTranslation> adviceTranslations = Collections.singletonList(getAdviceTranslation());
        when(adviceService.save(advicePostDTO)).thenReturn(advice);
        when(modelMapper.map(advicePostDTO.getTranslations(), new TypeToken<List<AdviceTranslation>>() {
            }.getType())).thenReturn(adviceTranslations);
        when(adviceTranslationRepo.saveAll(adviceTranslations)).thenReturn(adviceTranslations);

        adviceTranslationService.saveAdviceAndAdviceTranslation(advicePostDTO);
        assertEquals(adviceTranslations.get(0).getAdvice(), advice);
        verify(adviceTranslationRepo, times(1)).saveAll(adviceTranslations);
    }
}*/
