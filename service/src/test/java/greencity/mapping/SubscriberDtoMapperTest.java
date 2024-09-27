package greencity.mapping;

import greencity.TestConst;
import greencity.dto.language.LanguageVO;
import greencity.dto.user.SubscriberDto;
import greencity.dto.user.UserVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SubscriberDtoMapperTest {

    @InjectMocks
    private SubscriberDtoMapper mapper;

    @Test
    void convert() {
        UserVO userVO = UserVO.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .languageVO(LanguageVO.builder()
                .id(1L)
                .code("ua")
                .build())
            .build();

        SubscriberDto expected = SubscriberDto.builder()
            .email(userVO.getEmail())
            .name(userVO.getName())
            .language(userVO.getLanguageVO().getCode())
            .build();

        SubscriberDto actual = mapper.convert(userVO);

        assertEquals(expected, actual);
    }
}