package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OpenAIRequest {
    public static final String FORECAST =
        "respond in a personalized manner, concisely, and accurately. Use numbers to present approximate data. "
            + "Provide a forecast of this person's impact on the global environment, considering their habits "
            + "over the specified number of days:";
    public static final String ADVICE = "answer by starting with the words \"since you don't have active habits\" "
        + "in the specified language. and then advertise the benefits of this habit:";
    public static final String NEWS_BY_QUERY = "generate real and relevant eco news "
        + "(maximum length 1350 characters) on the topic:";
    public static final String NEWS_WITHOUT_QUERY = "generate a real and relevant eco-news story on any topic according"
        + " to the latest trends in the world (maximum length 1350 characters)";
    public static final String ECO_FACT =
        "generate a concise, scientifically accurate eco fact about a general environmental topic "
            + "(e.g., climate change, biodiversity, sustainability). in the specified language. "
            + "Ensure the fact is engaging, educational, and no longer than 300 characters. "
            + "Avoid speculation; cite well-established environmental knowledge.";
    public static final String ECO_FACT_BY_QUERY =
        "generate a concise, fact-based eco insight (max 300 characters) specifically about: {query} "
            + "in the specified language. Focus on verified environmental science, avoiding opinions.";
    public static final String ECO_FACT_BY_HABITS =
        "generate a personalized eco fact (max 300 characters) based on these user habits: {habits} "
            + "in the specified language. Tailor the fact to improve sustainability "
            + "(e.g., 'Using cold water saves X energy vs. hot.'). "
            + "If habits are irrelevant/unclear, provide a general eco fact instead.";
}
