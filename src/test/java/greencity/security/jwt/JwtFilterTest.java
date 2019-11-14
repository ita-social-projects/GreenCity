package greencity.security.jwt;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class JwtFilterTest {


    @InjectMocks
    private JwtFilter filter;

    @Mock
    private JwtTool tool;

    @Test
    public void doFilter() {
    }
}