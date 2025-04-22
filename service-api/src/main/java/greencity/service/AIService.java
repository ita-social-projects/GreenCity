package greencity.service;

import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.EcoNewsGenericDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface for interacting with an AI-based forecasting service.
 */
public interface AIService {
    /**
     * Retrieves a forecast for a user based on their ID and preferred language.
     *
     * @param userId   The ID of the user for whom the forecast is being requested.
     * @param language The preferred language for the forecast response.
     * @return The forecast as a string in the specified language.
     */
    String getForecast(Long userId, String language);

    /**
     * Gets advice for a user based on their ID and the selected language.
     *
     * @param userId   The ID of the user for whom the advice is being requested.
     * @param language The preferred language for the forecast response.
     * @return The advice as a string in the specified language.
     */
    String getAdvice(Long userId, String language);

    /**
     * Generates news content based on the specified language and topic.
     *
     * @param language The preferred language for the news content.
     * @param query    The topic for the news. If the query is empty, generates news
     *                 on a random eco-friendly topic.
     * @return The generated news as a string in the specified language.
     */
    String getNews(String language, String query);
    
    /**
     * Generates eco-news based on the user's habits.
     *
     * @param language The preferred language for the eco-news response.
     *
     * @return The generated eco-news as a string in the specified language.
     */
    String generateEcoNewsBasedOnHabits(String language);

    /**
     Retrieves relevant eco-news for a user based on filters.

     @param userId User ID.
     @param language Preferred language.
     @param tags Tags to filter by.
     @param title Title to filter by.
     @param authorId Author ID to filter by.
     @param favorite Filter by user's favorites.
     @return List of relevant eco-news.
     **/
    List<EcoNewsDto> getRelevantEcoNewsForUser(Long userId, String language, List<String> tags, String title, Long authorId, boolean favorite);

    /**
     * Retrieves a paginated list of eco-news for a user based on filters.
     *
     * @param userId   User ID.
     * @param language Preferred language.
     * @param pageable Pagination information.
     * @param tags     Tags to filter by.
     * @param title    Title to filter by.
     * @param authorId Author ID to filter by.
     * @param favorite Filter by user's favorites.
     * @return Paginated list of eco-news.
     **/
    Page<EcoNewsGenericDto> getCombinedEcoNewsForUser(Long userId, String language, Pageable pageable, List<String> tags, String title, Long authorId, boolean favorite);

}