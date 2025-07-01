package greencity.service;

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
     * Generates an eco-fact personalized for the user based on their ID and
     * language.
     *
     * @param userId   The ID of the user for whom the eco-fact is being generated.
     * @param language The preferred language for the eco-fact.
     * @return The personalized eco-fact as a string in the specified language.
     */
    String getEcoFact(Long userId, String language);

    /**
     * Generates a general eco-fact based on the specified language and query.
     *
     * @param language The preferred language for the eco-fact.
     * @param query    The eco-related topic or keyword. If empty, a random eco-fact
     *                 may be returned.
     * @return The eco-fact as a string in the specified language.
     */
    String getEcoFact(String language, String query);
}