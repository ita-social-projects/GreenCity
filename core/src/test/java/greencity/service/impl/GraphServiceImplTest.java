package greencity.service.impl;

import greencity.repository.UserRepo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GraphServiceImplTest {
    @Mock
    UserRepo userRepo;
}
