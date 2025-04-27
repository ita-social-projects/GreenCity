package greencity.constant;

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
        """;

    public static final String ADVICE = """
        Provide an engaging and practical eco-friendly tip that can help an individual adopt
        a more sustainable lifestyle. Ensure that the advice is relevant, easy to implement,
        and highlights the environmental and personal benefits.
        Use real-world examples and persuasive language to encourage adoption.
        The response should be clear, concise, and informative.
        """;
    public static final String NEWS_BY_QUERY = """
        Generate a detailed and structured eco-news article.
        The response **must** be in **valid JSON format**.

        JSON Structure:
        {
            "title": "string (max 100 characters)",
            "content": "string (5-10 paragraphs, each 3+ sentences)"
        }

        Ensure:
        - The response is a properly formatted JSON object.
        - "title" is compelling and engaging (max 100 characters).
        - "content" is fact-based, engaging, and informative.
        - Keep total content length within 2000 characters.

        Topic:
        """;
    public static final String NEWS_WITHOUT_QUERY =
        """
             Generate a relevant and up-to-date eco-news article on a trending environmental topic.
             The response **must** be in **valid JSON format**.

             JSON Structure:
             {
                 "title": "string (max 100 characters)",
                 "content": "string (5-10 paragraphs, each 10+ sentences)"
             }

             Ensure:
             - The response is a properly formatted JSON object.
             - "title" is compelling and engaging (max 100 characters).
             - "content" provides a balanced and well-structured analysis, \s
             covering the key aspects of the topic, its global impact, and potential solutions or innovations.
             - Keep total content length within 2000 characters.
             - Use real-world statistics and references where possible.
            \s""";
    public static final String OPENAI_SIMILARITY_PROMPT = """
        I want you to act as an expert in environmental news and sustainable development.
        Your task is to analyze the semantic meaning and context of two provided topics
        and assess how similar they are on a scale from 0 to 1.

        A score closer to 0 means the topics are highly relevant and closely related.
        A score between 0.0 and 0.6 indicates similarity, while a score above 0.6 indicates irrelevance.

        Provide only a numerical score without any additional text.

        Topic 1: %s
        Topic 2: %s
        """;
}
