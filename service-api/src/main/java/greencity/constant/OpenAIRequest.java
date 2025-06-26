package greencity.constant;

import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OpenAIRequest {
    public static final String FORECAST = """
        Provide a personalized and data-driven forecast of this person's impact on the global environment.
        Analyze their habits over the specified number of days and present insights based on realistic
        environmental data. Use numbers and specific examples to illustrate the impact, such as CO2 reduction,
        water conservation, or waste minimization. The response should be clear, concise, and presented as plain
        text without bullet points, asterisks, numbers, or any additional symbols. Ensure the output contains
        only clean, readable text with appropriate punctuation and no special formatting.
        
        Person's habits: %s;
        """;

    public static final String ADVICE = """
        Provide an engaging and practical eco-friendly tip that can help an individual adopt
        a more sustainable lifestyle. Ensure that the advice is relevant, easy to implement,
        and highlights the environmental and personal benefits.
        Use real-world examples and persuasive language to encourage adoption.
        The response should be clear, concise, and informative.
        
        Person's habit, on which the advice should be based: %s;
        """;

    public static final String NEWS_BY_QUERY = """
        Generate a detailed and structured eco-news article.
    
        Ensure:
        - The response is a properly formatted JSON object.
        - "title" is compelling and engaging (max 100 characters).
        - "content" is fact-based, engaging, and informative.
        - Keep total content length within 2000 characters.
        
        Topic: "%s";
        """;

    public static final String NEWS_WITHOUT_QUERY = """
        Generate a relevant and up-to-date eco-news article on a trending environmental topic.

        Ensure:
        - The response is a properly formatted JSON object.
        - "title" is compelling and engaging (max 100 characters).
        - "content" provides a balanced and well-structured analysis, covering the key aspects of the topic, its global impact, and potential solutions or innovations.
        - Keep total content length within 2000 characters.
        - Use real-world statistics and references where possible.
        """;

    public static final Map<String, Object> JSON_SCHEMA = Map.of(
        "name", "EcoNews",
        "schema", Map.of(
            "type", "object",
            "properties", Map.of(
                "title", Map.of(
                    "type", "string",
                    "maxLength", 100,
                    "description", "Title of the article, max 100 characters"
                ),
                "content", Map.of(
                    "type", "string",
                    "description", "Article body, 5–10 paragraphs, each with 10+ sentences"
                )
            ),
            "required", List.of("title", "content")
        )
    );
}
