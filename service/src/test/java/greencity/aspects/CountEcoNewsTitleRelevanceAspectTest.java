package greencity.aspects;

import greencity.service.EcoNewsRelevanceService;
import greencity.service.EcoNewsService;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econews.UpdateEcoNewsDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountEcoNewsTitleRelevanceAspectTest {

    @Mock
    private EcoNewsService ecoNewsService;

    @Mock
    private EcoNewsRelevanceService ecoNewsRelevanceService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @InjectMocks
    private CountEcoNewsTitleRelevanceAspect aspect;

    private final Long ecoNewsId = 42L;

    private UpdateEcoNewsDto updateDto;
    private EcoNewsVO oldNews;

    @BeforeEach
    void setUp() {
        updateDto = mock(UpdateEcoNewsDto.class);
        oldNews = mock(EcoNewsVO.class);
    }

    @Test
    void aroundUpdate_whenTitleChanged_shouldMarkRelevanceAsOutdated() throws Throwable {
        when(updateDto.getTitle()).thenReturn("New Title");
        when(oldNews.getTitle()).thenReturn("Old Title");
        when(ecoNewsService.findById(ecoNewsId)).thenReturn(oldNews);

        when(joinPoint.getArgs()).thenReturn(new Object[]{updateDto, null, null, ecoNewsId});
        when(joinPoint.proceed()).thenReturn("controllerResult");

        Object result = aspect.aroundUpdate(joinPoint);

        assertEquals("controllerResult", result);
        verify(ecoNewsRelevanceService).markRelevanceAsOutdated(oldNews);
        verify(joinPoint).proceed();
    }

    @Test
    void aroundUpdate_whenTitleNotChanged_shouldNotMarkRelevanceAsOutdated() throws Throwable {
        when(updateDto.getTitle()).thenReturn("Same Title");
        when(oldNews.getTitle()).thenReturn("Same Title");
        when(ecoNewsService.findById(ecoNewsId)).thenReturn(oldNews);

        when(joinPoint.getArgs()).thenReturn(new Object[]{updateDto, null, null, ecoNewsId});
        when(joinPoint.proceed()).thenReturn("controllerResult");

        Object result = aspect.aroundUpdate(joinPoint);

        assertEquals("controllerResult", result);
        verify(ecoNewsRelevanceService, never()).markRelevanceAsOutdated(any());
        verify(joinPoint).proceed();
    }

    @Test
    void aroundUpdate_whenNewTitleIsNullAndOldIsNotNull_shouldMarkRelevanceAsOutdated() throws Throwable {
        when(updateDto.getTitle()).thenReturn(null);
        when(oldNews.getTitle()).thenReturn("Old Title");
        when(ecoNewsService.findById(ecoNewsId)).thenReturn(oldNews);

        when(joinPoint.getArgs()).thenReturn(new Object[]{updateDto, null, null, ecoNewsId});
        when(joinPoint.proceed()).thenReturn("controllerResult");

        Object result = aspect.aroundUpdate(joinPoint);

        assertEquals("controllerResult", result);
        verify(ecoNewsRelevanceService).markRelevanceAsOutdated(oldNews);
        verify(joinPoint).proceed();
    }

    @Test
    void aroundUpdate_whenProceedThrows_shouldPropagateAndNotMarkOutdated() throws Throwable {
        when(updateDto.getTitle()).thenReturn("New Title");
        when(oldNews.getTitle()).thenReturn("Old Title");
        when(ecoNewsService.findById(ecoNewsId)).thenReturn(oldNews);

        when(joinPoint.getArgs()).thenReturn(new Object[]{updateDto, null, null, ecoNewsId});
        RuntimeException boom = new RuntimeException("boom");
        when(joinPoint.proceed()).thenThrow(boom);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> aspect.aroundUpdate(joinPoint));
        assertEquals("boom", thrown.getMessage());
        verify(ecoNewsRelevanceService, never()).markRelevanceAsOutdated(any());
        verify(joinPoint).proceed();
    }
}
