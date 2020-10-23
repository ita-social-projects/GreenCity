
package greencity.service;

import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceTranslationVO;
import greencity.dto.advice.AdviceVO;
import greencity.service.AdviceServiceImpl;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import greencity.entity.localization.AdviceTranslation;
import greencity.repository.AdviceTranslationRepo;
import java.util.Collections;
import java.util.List;

import greencity.service.AdviceTranslationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;


@ExtendWith(MockitoExtension.class)
class AdviceTranslationServiceImplTest {
    @Mock
    private AdviceTranslationRepo adviceTranslationRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private  AdviceServiceImpl adviceService;

    @InjectMocks
    private AdviceTranslationServiceImpl adviceTranslationService;

    @Spy
    @InjectMocks
    private AdviceTranslationServiceImpl adviceTranslationServiceSpy;

    private List<AdviceTranslationVO> adviceTranslationsVO =
        Collections.singletonList(AdviceTranslationVO.builder().build());

    @Test
    void saveAdviceTranslationTest() {
        List<AdviceTranslation> adviceTranslations =
            Collections.singletonList(AdviceTranslation.builder().build());
        when(modelMapper.map(adviceTranslationsVO,new TypeToken<List<AdviceTranslation>>() {
        }.getType())).thenReturn(adviceTranslations);
        when(adviceTranslationRepo.saveAll(adviceTranslations)).thenReturn(adviceTranslations);
        when(modelMapper.map(adviceTranslations, new TypeToken<List<AdviceTranslationVO>>() {
        }.getType())).thenReturn(adviceTranslationsVO);
        assertEquals(adviceTranslationsVO,adviceTranslationService.saveAdviceTranslation(adviceTranslationsVO));
    }

    @Test
    void saveAdviceAndAdviceTranslationTest() {
        AdviceVO adviceVO = AdviceVO.builder().build();
        AdvicePostDto advicePostDto = new AdvicePostDto();
        when(adviceService.save(advicePostDto)).thenReturn(adviceVO);
        when(modelMapper.map(advicePostDto.getTranslations(), new TypeToken<List<AdviceTranslationVO>>() {
            }.getType())).thenReturn(adviceTranslationsVO);
        doReturn(adviceTranslationsVO).when(adviceTranslationServiceSpy).saveAdviceTranslation(adviceTranslationsVO);
        assertEquals(adviceTranslationsVO, adviceTranslationServiceSpy.saveAdviceAndAdviceTranslation(advicePostDto));
    }
}
