package com.eneik.generated.dto;

public class MonthlyMetricDto {
    private final int year;
    private final int month;
    private final String eventType;
    private final long count;

    public MonthlyMetricDto(int year, int month, String eventType, long count) {
        this.year = year;
        this.month = month;
        this.eventType = eventType;
        this.count = count;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public String getEventType() {
        return eventType;
    }

    public long getCount() {
        return count;
    }
}
