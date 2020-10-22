package greencity.mapping;

import greencity.dto.breaktime.BreakTimeVO;
import greencity.dto.category.CategoryVO;
import greencity.dto.openhours.OpeningHoursVO;
import greencity.dto.photo.PhotoVO;
import greencity.dto.place.PlaceVO;
import greencity.entity.OpeningHours;
import greencity.entity.Photo;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link Photo} into
 * {@link PhotoVO}.
 *
 * @author Vasyl Zhovnir
 */
@Component
public class OpeningHoursVOMapper extends AbstractConverter<OpeningHours, OpeningHoursVO> {
    /**
     * Method convert {@link Photo} to {@link PhotoVO}.
     *
     * @return {@link Photo}
     */
    @Override
    protected OpeningHoursVO convert(OpeningHours source) {
        BreakTimeVO breakTimeVO = BreakTimeVO.builder()
            .id(source.getBreakTime().getId())
            .startTime(source.getBreakTime().getStartTime())
            .endTime(source.getBreakTime().getEndTime())
            .build();

        PlaceVO placeVO = PlaceVO.builder()
            .id(source.getPlace().getId())
            .description(source.getPlace().getDescription())
            .email(source.getPlace().getDescription())
            .modifiedDate(source.getPlace().getModifiedDate())
            .name(source.getPlace().getName())
            .phone(source.getPlace().getPhone())
            .authorId(source.getPlace().getAuthor().getId())
            .category(CategoryVO.builder()
                    .id(source.getPlace().getCategory().getId())
                    .name(source.getPlace().getCategory().getName())
                    .build())
            .locationId(source.getPlace().getLocation().getId())
            .build();

        return OpeningHoursVO.builder()
            .id(source.getId())
            .openTime(source.getOpenTime())
            .closeTime(source.getCloseTime())
            .weekDay(source.getWeekDay())
            .breakTime(breakTimeVO)
            .place(placeVO)
            .build();
    }
}
