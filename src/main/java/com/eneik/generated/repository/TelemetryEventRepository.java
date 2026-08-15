package com.eneik.generated.repository;

import com.eneik.generated.domain.TelemetryEvent;
import com.eneik.generated.dto.MonthlyMetricDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelemetryEventRepository extends JpaRepository<TelemetryEvent, String> {

    @Query("SELECT new com.eneik.generated.dto.MonthlyMetricDto(" +
           "YEAR(e.timestamp), MONTH(e.timestamp), e.eventType, COUNT(e)) " +
           "FROM TelemetryEvent e " +
           "GROUP BY YEAR(e.timestamp), MONTH(e.timestamp), e.eventType " +
           "ORDER BY YEAR(e.timestamp), MONTH(e.timestamp), e.eventType")
    List<MonthlyMetricDto> aggregateMonthlyMetrics();
}
