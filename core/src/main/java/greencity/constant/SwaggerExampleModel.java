package greencity.constant;

public final class SwaggerExampleModel {
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

    public static final String ADD_TIPS_AND_TRICKS_REQUEST =
        "Add tips & tricks request\n"
            + BEFORE_EXAMPLE
            + "{\n"
            + "  \"image\": \"string\",\n"
            + "  \"source\": \"string\",\n"
            + "  \"tags\": [\n"
            + "    \"string\"\n"
            + "  ],\n"
            + "  \"text\": \"string\",\n"
            + "  \"title\": \"string\"\n"
            + "}"
            + AFTER_EXAMPLE;

    public static final String ADD_ECO_NEWS_REQUEST =
        "Add Eco News Request\n"
            + BEFORE_EXAMPLE
            + "{\n"
            + "  \"image\": \"string\",\n"
            + "  \"source\": \"string\",\n"
            + "  \"tags\": [\n"
            + "    \"string\"\n"
            + "  ],\n"
            + "  \"text\": \"string\",\n"
            + "  \"title\": \"string\"\n"
            + "}\n"
            + AFTER_EXAMPLE;
    public static final String UPDATE_ECO_NEWS =
        "Update Eco News\n"
            + BEFORE_EXAMPLE
            + "{\n"
            + "  \"id\": 0,\n"
            + "  \"image\": \"string\",\n"
            + "  \"source\": \"string\",\n"
            + "  \"tags\": [\n"
            + "    \"string\"\n"
            + "  ],\n"
            + "  \"text\": \"string\",\n"
            + "  \"title\": \"string\"\n"
            + "}\n"
            + AFTER_EXAMPLE;

    private SwaggerExampleModel() {
    }
}
