package greencity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEmailAndIdDto {

        private Long id;
        @NotBlank
private String email;
       public UserEmailAndIdDto(String email)
       {
               this.email=email;
       }


}
