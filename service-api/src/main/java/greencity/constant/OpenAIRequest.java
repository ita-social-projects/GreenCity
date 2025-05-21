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

    public static final String NEWS_WITHOUT_QUERY = """
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
        - "content" provides a balanced and well-structured analysis, covering the key aspects of the topic, its global impact, and potential solutions or innovations.
        - Keep total content length within 2000 characters.
        - Use real-world statistics and references where possible.
        """;

    public static final String RELEVANCE_PROMPT_TEMPLATE = """
    You are a recommendation engine.
    
    Your task is to analyze an eco-news article based on its title and content and determine how relevant it is to a specific user.
    You will be provided with:
    - A news title
    - A news content body
    - A description of the user's reading habits (topics they engage with, frequency, preferences)
    - A list of tags the user is interested in
    
    Using this information:
    1. Evaluate how closely the news article matches the user’s interests, both in content and keywords/tags.
    2. Consider semantic similarity, not just exact word matches.
    3. Consider whether the tone, topic, or style of the article fits the user's habits and interests.
    
    Return a single relevance score between 0 and 1 (rounded to one decimal place), where:
    - 1.0 means "highly relevant"
    - 0.0 means "not relevant at all"
    
    Respond with only the number. Do not add any explanation, text, or symbols.
    
    Title: %s
    Content: %s
    User Habits: %s
    User Tags: %s
    """;
}
