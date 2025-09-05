package greencity.aspects;

import greencity.dto.econews.EcoNewsVO;
import greencity.service.EcoNewsRelevanceService;
import greencity.service.EcoNewsService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import greencity.dto.econews.UpdateEcoNewsDto;
import org.aspectj.lang.ProceedingJoinPoint;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "greencity.relevance", name = "enabled", havingValue = "true")
public class CountEcoNewsTitleRelevanceAspect {
    private final EcoNewsService ecoNewsService;
    private final EcoNewsRelevanceService ecoNewsRelevanceService;

    /**
     * Around advice that wraps the execution of the {@code update} method in
     * {@code EcoNewsController}. If the eco news title has changed as a result of
     * the update, it sets the relevance of the old title as outdated for future
     * scheduled recalculation.
     *
     * @param joinPoint the join point representing the method call
     * @return the original return value of the update method
     * @throws Throwable if the underlying method throws any exceptions
     */
    @Around("execution(* greencity.controller.EcoNewsController.update(..))")
    public Object aroundUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        UpdateEcoNewsDto updateDto = (UpdateEcoNewsDto) args[0];
        Long ecoNewsId = (Long) args[3];
        String newTitle = updateDto.getTitle();

        EcoNewsVO oldNews = ecoNewsService.findById(ecoNewsId);
        String oldTitle = oldNews.getTitle();

        Object result = joinPoint.proceed();

        if (!Objects.equals(oldTitle, newTitle)) {
            ecoNewsRelevanceService.markRelevanceAsOutdated(oldNews);
        }

        return result;
    }
}
