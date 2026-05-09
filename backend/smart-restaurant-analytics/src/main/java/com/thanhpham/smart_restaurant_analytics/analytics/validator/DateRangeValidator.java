package com.thanhpham.smart_restaurant_analytics.analytics.validator;

import com.thanhpham.smart_restaurant_analytics.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DateRangeValidator {

    private static final int MAX_DAILY_RANGE_DAYS = 366;
    // private static final int MAX_MONTHLY_RANGE_YEARS = 5;

    /**
     * Validate a date range for daily queries.
     * Returns [startDateTime, endDateTime] inclusive.
     */
    public LocalDateTime[] validateDailyRange(LocalDate startDate, LocalDate endDate) {
        validateNotNull(startDate, "startDate");
        validateNotNull(endDate, "endDate");

        if (endDate.isBefore(startDate)) {
            throw new BusinessRuleException("endDate must not be before startDate");
        }

        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (days > MAX_DAILY_RANGE_DAYS) {
            throw new BusinessRuleException(
                    "Date range cannot exceed " + MAX_DAILY_RANGE_DAYS + " days for daily queries");
        }

        // Half-open [start, endExclusive): repository queries should use `< :end`.
        return new LocalDateTime[] {
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        };
    }

    /**
     * Validate a year for monthly queries.
     */
    public void validateYear(int year) {
        int currentYear = LocalDate.now().getYear();
        if (year < 2020 || year > currentYear + 1) {
            throw new BusinessRuleException(
                    "Year must be between 2020 and " + (currentYear + 1));
        }
    }

    public int clampLimit(Integer limit, int defaultVal, int max) {
        if (limit == null)
            return defaultVal;
        if (limit < 1)
            return defaultVal;
        return Math.min(limit, max);
    }

    private void validateNotNull(Object value, String field) {
        if (value == null) {
            throw new BusinessRuleException(field + " is required");
        }
    }
}