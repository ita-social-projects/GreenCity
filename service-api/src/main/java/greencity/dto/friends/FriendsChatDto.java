package greencity.dto.friends;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FriendsChatDto {
    private Long chatId;
    private Boolean chatExists;
}