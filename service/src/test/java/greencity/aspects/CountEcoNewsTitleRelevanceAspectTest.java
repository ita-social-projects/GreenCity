package greencity.aspects;

import greencity.dto.econews.EcoNewsGenericDto;
import greencity.dto.econews.UpdateEcoNewsDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.service.EcoNewsService;
import greencity.service.AIServiceImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CountEcoNewsTitleRelevanceAspectTest {
    private AIServiceImpl aiService;
    private EcoNewsService ecoNewsService;
    private CountEcoNewsTitleRelevanceAspect aspect;

    @BeforeEach
    void setUp() {
        aiService = mock(AIServiceImpl.class);
        ecoNewsService = mock(EcoNewsService.class);
        aspect = new CountEcoNewsTitleRelevanceAspect(aiService, ecoNewsService);
    }


    @Test
    void testAfterSavingEcoNews_WithEcoNewsGenericDto() {
        EcoNewsGenericDto dto = EcoNewsGenericDto.builder()
                .id(42L)
                .build();

        ResponseEntity<EcoNewsGenericDto> response = ResponseEntity.ok(dto);

        aspect.afterSavingEcoNews(response);

        verify(aiService, times(1)).getRelevanceForEcoNews(42L);
    }

    @Test
    void testAfterSavingEcoNews_WithNonDtoBody() {
        ResponseEntity<String> response = ResponseEntity.ok("Not a DTO");

        aspect.afterSavingEcoNews(response);

        verify(aiService, never()).getRelevanceForEcoNews(anyLong());
    }

    @Test
    void testAfterSavingEcoNews_NotResponseEntity() {
        aspect.afterSavingEcoNews("Some Object");

        verify(aiService, never()).getRelevanceForEcoNews(anyLong());
    }

    @Test
    void testAroundUpdate_TitleChanged_ShouldInvokeAIService() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);

        UpdateEcoNewsDto updateDto = new UpdateEcoNewsDto();
        updateDto.setTitle("New Title");

        Long ecoNewsId = 1L;
        EcoNewsVO oldNews = new EcoNewsVO();
        oldNews.setTitle("Old Title");

        Object[] args = new Object[]{updateDto, null, null, ecoNewsId};
        when(joinPoint.getArgs()).thenReturn(args);
        when(ecoNewsService.findById(ecoNewsId)).thenReturn(oldNews);
        when(joinPoint.proceed()).thenReturn("result");

        Object result = aspect.aroundUpdate(joinPoint);

        verify(aiService, times(1)).getRelevanceForEcoNews(ecoNewsId);
        assertEquals("result", result);
    }

    @Test
    void testAroundUpdate_TitleNotChanged_ShouldNotInvokeAIService() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);

        UpdateEcoNewsDto updateDto = new UpdateEcoNewsDto();
        updateDto.setTitle("Same Title");

        Long ecoNewsId = 2L;
        EcoNewsVO oldNews = new EcoNewsVO();
        oldNews.setTitle("Same Title");

        Object[] args = new Object[]{updateDto, null, null, ecoNewsId};
        when(joinPoint.getArgs()).thenReturn(args);
        when(ecoNewsService.findById(ecoNewsId)).thenReturn(oldNews);
        when(joinPoint.proceed()).thenReturn("result");

        Object result = aspect.aroundUpdate(joinPoint);

        verify(aiService, never()).getRelevanceForEcoNews(anyLong());
        assertEquals("result", result);
    }
}

