package com.eneik.generated.repository;

import com.eneik.generated.domain.TelemetryEvent;
import com.eneik.generated.dto.MonthlyMetricDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import com.eneik.generated.Application;

@SpringBootTest(classes = Application.class)
public class TelemetryEventRepositoryTest {

    @Autowired
    private TelemetryEventRepository repository;

    @Test
    public void testSaveAndAggregateMonthlyMetrics() {
        // Clear repository
        repository.deleteAll();

        // Fixed deterministic timestamps
        OffsetDateTime t1 = OffsetDateTime.of(2026, 8, 1, 10, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime t2 = OffsetDateTime.of(2026, 8, 15, 12, 30, 0, 0, ZoneOffset.UTC);
        OffsetDateTime t3 = OffsetDateTime.of(2026, 9, 2, 9, 15, 0, 0, ZoneOffset.UTC);

        // Save telemetry events
        TelemetryEvent event1 = new TelemetryEvent("evt-1", "LOGIN", t1, "{\"userId\":\"u1\"}");
        TelemetryEvent event2 = new TelemetryEvent("evt-2", "LOGIN", t2, "{\"userId\":\"u2\"}");
        TelemetryEvent event3 = new TelemetryEvent("evt-3", "LOGOUT", t2, "{\"userId\":\"u1\"}");
        TelemetryEvent event4 = new TelemetryEvent("evt-4", "LOGIN", t3, "{\"userId\":\"u3\"}");

        repository.save(event1);
        repository.save(event2);
        repository.save(event3);
        repository.save(event4);

        // Verify immutable persistence and retrieval
        TelemetryEvent retrieved = repository.findById("evt-1").orElse(null);
        assertNotNull(retrieved);
        assertEquals("evt-1", retrieved.getId());
        assertEquals("LOGIN", retrieved.getEventType());
        assertEquals(t1, retrieved.getTimestamp());
        assertEquals("{\"userId\":\"u1\"}", retrieved.getPayload());

        // Aggregate monthly metrics
        List<MonthlyMetricDto> metrics = repository.aggregateMonthlyMetrics();
        assertNotNull(metrics);
        assertEquals(3, metrics.size());

        // August 2026 - LOGIN (count = 2)
        MonthlyMetricDto m1 = metrics.get(0);
        assertEquals(2026, m1.getYear());
        assertEquals(8, m1.getMonth());
        assertEquals("LOGIN", m1.getEventType());
        assertEquals(2, m1.getCount());

        // August 2026 - LOGOUT (count = 1)
        MonthlyMetricDto m2 = metrics.get(1);
        assertEquals(2026, m2.getYear());
        assertEquals(8, m2.getMonth());
        assertEquals("LOGOUT", m2.getEventType());
        assertEquals(1, m2.getCount());

        // September 2026 - LOGIN (count = 1)
        MonthlyMetricDto m3 = metrics.get(2);
        assertEquals(2026, m3.getYear());
        assertEquals(9, m3.getMonth());
        assertEquals("LOGIN", m3.getEventType());
        assertEquals(1, m3.getCount());
    }
}
