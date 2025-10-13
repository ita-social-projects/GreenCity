package greencity.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionLoggingIdFilterTest {
    @InjectMocks
    private TransactionLoggingIdFilter transactionIdFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    private static final String MDC_TRANSACTION_ID_KEY = "transactionId";

    @BeforeEach
    void setUp() {
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void doFilterInternal_WhenNoTransactionIdHeader_GeneratesNewTransactionId() throws ServletException, IOException {
        when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn(null);

        transactionIdFilter.doFilterInternal(request, response, filterChain);

        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq(TRANSACTION_ID_HEADER), headerValueCaptor.capture());
        String transactionId = headerValueCaptor.getValue();
        assertNotNull(transactionId, "TransactionId should not be null");
        assertFalse(transactionId.isEmpty(), "TransactionId should not be empty");

        verify(filterChain).doFilter(request, response);

        assertNull(MDC.get(MDC_TRANSACTION_ID_KEY), "MDC should be cleared in finally block");
    }

    @Test
    void doFilterInternal_WhenEmptyTransactionIdHeader_GeneratesNewTransactionId()
        throws ServletException, IOException {
        when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn("");

        transactionIdFilter.doFilterInternal(request, response, filterChain);

        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq(TRANSACTION_ID_HEADER), headerValueCaptor.capture());
        String transactionId = headerValueCaptor.getValue();
        assertNotNull(transactionId, "TransactionId should not be null");
        assertFalse(transactionId.isEmpty(), "TransactionId should not be empty");

        verify(filterChain).doFilter(request, response);

        assertNull(MDC.get(MDC_TRANSACTION_ID_KEY), "MDC should be cleared in finally block");
    }

    @Test
    void doFilterInternal_WhenTransactionIdHeaderExists_UsesExistingTransactionId()
        throws ServletException, IOException {
        String existingTransactionId = "existing-transaction-id";
        when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn(existingTransactionId);

        transactionIdFilter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(TRANSACTION_ID_HEADER, existingTransactionId);

        verify(filterChain).doFilter(request, response);

        assertNull(MDC.get(MDC_TRANSACTION_ID_KEY), "MDC should be cleared in finally block");
    }

    @Test
    void doFilterInternal_WhenFilterChainThrowsServletException_ClearsMDC() throws ServletException, IOException {
        when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn(null);
        ServletException servletException = new ServletException("Test exception");
        doThrow(servletException).when(filterChain).doFilter(request, response);

        assertThrows(ServletException.class,
            () -> transactionIdFilter.doFilterInternal(request, response, filterChain));

        assertNull(MDC.get(MDC_TRANSACTION_ID_KEY),
            "MDC should be cleared in finally block even if ServletException occurs");
    }

    @Test
    void doFilterInternal_WhenFilterChainThrowsIOException_ClearsMDC() throws ServletException, IOException {
        when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn(null);
        IOException ioException = new IOException("Test exception");
        doThrow(ioException).when(filterChain).doFilter(request, response);

        assertThrows(IOException.class, () -> transactionIdFilter.doFilterInternal(request, response, filterChain));

        assertNull(MDC.get(MDC_TRANSACTION_ID_KEY),
            "MDC should be cleared in finally block even if IOException occurs");
    }

    @Test
    void doFilterInternal_WhenFilterChainThrowsRuntimeException_ClearsMDC() throws ServletException, IOException {
        when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn(null);
        RuntimeException runtimeException = new RuntimeException("Test exception");
        doThrow(runtimeException).when(filterChain).doFilter(request, response);

        assertThrows(RuntimeException.class,
            () -> transactionIdFilter.doFilterInternal(request, response, filterChain));

        assertNull(MDC.get(MDC_TRANSACTION_ID_KEY),
            "MDC should be cleared in finally block even if RuntimeException occurs");
    }
}