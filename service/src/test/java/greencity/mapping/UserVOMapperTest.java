package greencity.mapping;

import greencity.ModelUtils;
import greencity.client.UserRemoteClient;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserVOMapperTest {

    @Mock
    UserRemoteClient userRemoteClient;

    @InjectMocks
    UserVOMapper mapper;

    @Test
    void convert() {
        User userToConvert = User.builder()
            .id(1L)
            .email("email")
            .build();
        UserVO expected = ModelUtils.getUserVOWithData();

        when(userRemoteClient.findByEmail(userToConvert.getEmail()))
            .thenReturn(Optional.of(expected));

        assertEquals(expected, mapper.convert(userToConvert));
    }
}
