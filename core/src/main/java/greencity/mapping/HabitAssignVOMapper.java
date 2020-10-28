//package greencity.mapping;
//
//import greencity.dto.habit.HabitAssignVO;
//import greencity.dto.habit.HabitVO;
//import greencity.dto.user.UserVO;
//import greencity.entity.HabitAssign;
//import org.modelmapper.AbstractConverter;
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Component;
//
///**
// * Class that used by {@link ModelMapper} to map {@link HabitAssign} into
// * {@link HabitAssignVO}.
// */
//@Component
//public class HabitAssignVOMapper extends AbstractConverter<HabitAssign, HabitAssignVO> {
//    /**
//     * Method convert {@link HabitAssign} to {@link HabitAssignVO}.
//     *
//     * @return {@link HabitAssignVO}
//     */
//    @Override
//    protected HabitAssignVO convert(HabitAssign habitAssign) {
//        return HabitAssignVO.builder()
//            .id(habitAssign.getId())
//            .acquired(habitAssign.getAcquired())
//            .suspended(habitAssign.getSuspended())
//            .createDateTime(habitAssign.getCreateDate())
//            .habitVO(HabitVO.builder().id(habitAssign.getHabit().getId()).build())
//            .userVO(UserVO.builder().id(habitAssign.getUser().getId()).build())
//            .build();
//    }
//}
