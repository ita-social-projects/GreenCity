package greencity.health;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabasePerformanceHealthIndicatorTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private DatabasePerformanceHealthIndicator healthIndicator;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @BeforeEach
    void setUp() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
    }

    @Test
    void testHealthUpWhenDatabaseIsReachable() throws SQLException {
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(anyString())).thenReturn(true);
        when(meterRegistry.gauge(eq("app_database_health"), eq(1))).thenReturn(null);

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("PostgreSQL is reachable", health.getDetails().get("database"));
        assertTrue(health.getDetails().containsKey("latencyMs"));
        assertTrue((Long) health.getDetails().get("latencyMs") >= 0);

        verify(dataSource).getConnection();
        verify(connection).createStatement();
        verify(statement).execute("SELECT 1");
        verify(meterRegistry).gauge("app_database_health", 1);
        verify(connection).close();
        verify(statement).close();
    }

    @Test
    void testHealthDownWhenDatabaseThrowsException() throws SQLException {
        SQLException exception = new SQLException("Connection failed");
        when(dataSource.getConnection()).thenThrow(exception);
        when(meterRegistry.gauge(eq("app_database_health"), eq(0))).thenReturn(null);

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("PostgreSQL is unreachable", health.getDetails().get("database"));
        assertEquals("Connection failed", health.getDetails().get("error"));

        verify(dataSource).getConnection();
        verify(meterRegistry).gauge("app_database_health", 0);
        verify(connection, never()).createStatement();
        verify(statement, never()).execute(anyString());
        verify(connection, never()).close();
        verify(statement, never()).close();
    }

    @Test
    void testHealthDownWhenStatementExecutionFails() throws SQLException {
        when(connection.createStatement()).thenReturn(statement);
        SQLException exception = new SQLException("Query execution failed");
        when(statement.execute("SELECT 1")).thenThrow(exception);
        when(meterRegistry.gauge(eq("app_database_health"), eq(0))).thenReturn(null);

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("PostgreSQL is unreachable", health.getDetails().get("database"));
        assertEquals("Query execution failed", health.getDetails().get("error"));

        verify(dataSource).getConnection();
        verify(connection).createStatement();
        verify(statement).execute("SELECT 1");
        verify(meterRegistry).gauge("app_database_health", 0);
        verify(connection).close();
        verify(statement).close();
    }
}