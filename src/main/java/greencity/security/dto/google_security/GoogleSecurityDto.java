package greencity.security.dto.google_security;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class GoogleSecurityDto {

    @NotBlank private String authToken;
    @NotBlank private String email;
    @NotBlank private String firstName;
    @NotBlank private String id;
    @NotBlank private String idToken;
    @NotBlank private String lastName;
    @NotBlank private String name;
    @NotBlank private String photoUrl;
    @NotBlank private String provider;
}
