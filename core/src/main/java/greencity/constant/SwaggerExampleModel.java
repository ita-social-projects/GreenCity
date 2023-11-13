package greencity.constant;

public final class SwaggerExampleModel {
    private static final String IMAGE_DESCRIPTION = "pass image as base64 or upload image\n";

    private static final String BEFORE_EXAMPLE = "<div>\n"
        + "\t<ul class=\"tab\">\n"
        + "\t\t<li class=\"tabitem active\">\n"
        + "\t\t\t<a class=\"tablinks\" data-name=\"example\">Example Value</a>\n"
        + "\t\t</li>\n"
        + "\t\t<li class=\"tabitem\">\n"
        + "\t\t\t<a class=\"tablinks\" data-name=\"model\">Model</a>\n"
        + "\t\t</li>\n"
        + "\t</ul>\n"
        + "\t<pre>\n";

    private static final String EXAMPLE =
        "  \"image\": \"string\",\n"
            + "  \"source\": \"https://example.org/\",\n"
            + "  \"shortInfo\": \"string\",\n"
            + "  \"tags\": [\n"
            + "    \"string\"\n"
            + "  ],\n"
            + "  \"titleTranslation\":\n"
            + "     {\"content\": \"string\",\n"
            + "     \"languageCode\": \"string\"},\n"
            + "  \"textTranslation\":\n"
            + "     {\"content\": \"string\",\n"
            + "     \"languageCode\": \"string\"}\n";

    private static final String AFTER_EXAMPLE = "\t</pre>\n"
        + "</div>";

    public static final String USER_PROFILE_PICTURE_DTO =
        "User Profile Picture\n"
            + BEFORE_EXAMPLE
            + "{\n"
            + "  \"id\": 0,\n"
            + "  \"profilePicturePath\": \"string\"\n"
            + "}\n"
            + AFTER_EXAMPLE;

    public static final String ADD_ECO_NEWS_REQUEST =
        "Add Eco News Request\n"
            + IMAGE_DESCRIPTION
            + BEFORE_EXAMPLE
            + "{\n"
            + EXAMPLE
            + "}\n"
            + AFTER_EXAMPLE;
    public static final String UPDATE_ECO_NEWS =
        "Update Eco News\n"
            + IMAGE_DESCRIPTION
            + BEFORE_EXAMPLE
            + "{\n"
            + "  \"id\": 0,\n"
            + EXAMPLE
            + "}\n"
            + AFTER_EXAMPLE;
    public static final String ADD_EVENT = BEFORE_EXAMPLE
        + "{\n"
        + "\t\"title\":\"string\",\n"
        + "\t\"description\":\"string\",\n"
        + "\t\"open\":true,\n"
        + "\t\"datesLocations\":[\n"
        + "\t\t{\n"
        + "\t\t\t\"startDate\":\"2023-05-27T15:00:00Z\",\n"
        + "\t\t\t\"finishDate\":\"2023-05-27T17:00:00Z\",\n"
        + "\t\t\t\"coordinates\":{\n"
        + "\t\t\t\t\"latitude\":1,\n"
        + "\t\t\t\t\"longitude\":1\n"
        + "\t\t\t},\n"
        + "\t\t\t\"onlineLink\":\"string\"\n"
        + "\t\t}\n"
        + "\t],\n"
        + "\t\"tags\":[\"Social\"]\n"
        + "}" + AFTER_EXAMPLE;

    public static final String UPDATE_EVENT = BEFORE_EXAMPLE
        + "{\n"
        + "\t\"id\":\0,\n"
        + "\t\"title\":\"string\",\n"
        + "\t\"description\":\"string\",\n"
        + "\t\"datesLocations\":[\n"
        + "\t\t{\n"
        + "\t\t\t\"id\":\0,\n"
        + "\t\t\t{\n"
        + "\t\t\t\t\"id\":\0,\n"
        + "\t\t\t\t\"title\":\"string\",\n"
        + "\t\t\t\t{\n"
        + "\t\t\t\t\t\"id\":\0,\n"
        + "\t\t\t\t\t\"title\":\"string\",\n"
        + "\t\t\t\t\t{\n"
        + "\t\t\t\t\t\t\"id\":\0,\n"
        + "\t\t\t\t\t\t\"name\":\"string\",\n"
        + "\t\t\t\t\t\t\"organizerRating\":\0,\n"
        + "\t\t\t\t\t}\n"
        + "\t\t\t\t\t\"creationDate\":\"2023-05-27\",\n"
        + "\t\t\t\t\t\"description\":\"string\",\n"
        + "\t\t\t\t\t\"dates\":[\n"
        + "\t\t\t\t\t\t{\n"
        + "\t\t\t\t\t\t\t\"id\":\0,\n"
        + "\t\t\t\t\t\t\t\"startDate\":\"2023-05-27T15:00:00Z\",\n"
        + "\t\t\t\t\t\t\t\"finishDate\":\"2023-05-27T17:00:00Z\",\n"
        + "\t\t\t\t\t\t\t\"onlineLink\":\"string\",\n"
        + "\t\t\t\t\t\t\t\"coordinates\":{\n"
        + "\t\t\t\t\t\t\t\t\"latitude\":1,\n"
        + "\t\t\t\t\t\t\t\t\"longitude\":1\n"
        + "\t\t\t\t\t\t\t},\n"
        + "\t\t\t\t\t\t}\n"
        + "\t\t\t\t\t\"tags\":[\n"
        + "\t\t\t\t\t\t{\n"
        + "\t\t\t\t\t\t\t\"id\":\0,\n"
        + "\t\t\t\t\t\t\t\"nameUa\":\"string\",\n"
        + "\t\t\t\t\t\t\t\"nameEn\":\"string\",\n"
        + "\t\t\t\t\t\t}\n"
        + "\t\t\t\t\t\"titleImage\":\"string\",\n"
        + "\t\t\t\t\t\"additionalImages\":[\"string\"]\n"
        + "\t\t\t\t\t\"isOpen\":true\n"
        + "\t\t\t\t\t\"isSubscribed\":true\n"
        + "\t\t\t\t\t\"isFavourite\":true\n"
        + "\t\t\t\t}\n"
        + "\t\t\t\"startDate\":\"2023-05-27T15:00:00Z\",\n"
        + "\t\t\t\"finishDate\":\"2023-05-27T17:00:00Z\",\n"
        + "\t\t\t\"onlineLink\":\"string\",\n"
        + "\t\t\t\"coordinates\":{\n"
        + "\t\t\t\t\"latitude\":1,\n"
        + "\t\t\t\t\"longitude\":1\n"
        + "\t\t\t},\n"
        + "\t\t}\n"
        + "\t],\n"
        + "\t\"titleImage\":\"string\"\n"
        + "\t\"additionalImages\":[\"string\"]\n"
        + "\t\"imagesToDelete\":[\"string\"]\n"
        + "\t\"tags\":[\"Social\"]\n"
        + "\t\"isOpen\":true\n"
        + "}";

    private SwaggerExampleModel() {
    }
}
