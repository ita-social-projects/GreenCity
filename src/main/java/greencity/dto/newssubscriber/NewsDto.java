package greencity.dto.newssubscriber;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsDto {
    public Long id;
    public String title;
    public String text;
    public ZonedDateTime creationDate;
}
