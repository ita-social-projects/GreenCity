package greencity.security;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;

public class JwtFilterTest {


    @InjectMocks
    private JwtFilter filter;

    @Mock
    private JwtTokenTool tool;

    @Test
    public void doFilter() {
    }
}