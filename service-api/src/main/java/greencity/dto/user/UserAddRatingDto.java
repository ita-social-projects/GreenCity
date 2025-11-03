<<<<<<<< HEAD:service-api/src/main/java/greencity/dto/search/SearchPlacesDto.java
package greencity.dto.search;
========
package greencity.dto.user;
>>>>>>>> dev:service-api/src/main/java/greencity/dto/user/UserAddRatingDto.java

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

<<<<<<<< HEAD:service-api/src/main/java/greencity/dto/search/SearchPlacesDto.java
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchPlacesDto {
    private Long id;
    private String name;
    private String category;
========
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserAddRatingDto {
    private Long id;

    private Double rating;
>>>>>>>> dev:service-api/src/main/java/greencity/dto/user/UserAddRatingDto.java
}
