package greencity.facade;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.econews.EcoNewsGenericDto;
import greencity.security.utils.TokenUtilService;
import greencity.service.AIService;
import greencity.utils.PageDtoUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Facade class for eco news-related operations. Handles user authentication,
 * filters application, and formatting of the paged response.
 */
@RequiredArgsConstructor
@Component
public class EcoNewsFacade {
    private final TokenUtilService tokenUtilService;
    private final AIService aiService;
    private final PageDtoUtil pageDtoUtil;

    /**
     * Retrieves filtered eco news for the authenticated user.
     *
     * @param request  HTTP request containing the authorization token.
     * @param page     pagination information.
     * @param tags     list of tags to filter by (optional).
     * @param title    title to filter by (optional).
     * @param authorId ID of the news author to filter by (optional).
     * @param favorite flag indicating whether to return only favorite news.
     * @param language user-selected language for content.
     * @return paged list of {@link EcoNewsGenericDto}, wrapped in
     *         {@link ResponseEntity}.
     */
    public ResponseEntity<PageableAdvancedDto<EcoNewsGenericDto>> getFilteredEcoNews(
        HttpServletRequest request, Pageable page,
        List<String> tags, String title,
        Long authorId, boolean favorite,
        String language) {
        Optional<Long> userOpt = tokenUtilService.getUserIdFromRequest(request);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = userOpt.get();
        Page<EcoNewsGenericDto> result = aiService.getCombinedEcoNewsForUser(
            userId, language, page, tags, title, authorId, favorite);

        return ResponseEntity.ok(pageDtoUtil.toPageableDto(result));
    }
}
