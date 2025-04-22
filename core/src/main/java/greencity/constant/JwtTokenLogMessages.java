package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JwtTokenLogMessages {
    public static final String METHOD_CALLED = "📥 Method '{}' called with arguments: {}";
    public static final String METHOD_SUCCESS = "✅ Method '{}' completed successfully. Result: {}";
    public static final String METHOD_EXCEPTION = "❌ Exception in method '{}': {}";

    public static final String NO_BEARER_TOKEN = "🔐 No Bearer token found in Authorization header.";
    public static final String TOKEN_EXPIRING = "⏳ Token for '{}' is close to expiration (in {} seconds)";
    public static final String TOKEN_ROLES = "👤 Roles for '{}': {}";
    public static final String ADMIN_ACCESS = "👑 Admin access detected for '{}'";
    public static final String TOKEN_PARSE_FAILED = "⚠️ Failed to parse JWT token: {}";

    public static final String TOKEN_INSPECTION_SUMMARY = """
        📦 Token Inspection Summary:
        - Email: {}
        - Issued At: {}
        - Expires At: {}
        - Time Left: {} sec
        """;

    public static final String JSON_CONVERSION_FAILED = "Unable to convert to JSON";

    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String CLAIM_ROLES = "roles";
    public static final String ADMIN_ROLE = "ADMIN";
}
