package com.wt.mse.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatFormattingServiceTest {
    private final StatFormattingService formattingService = new StatFormattingService();

    @Test
    void formatsTimeUsingHoursMinutesAndSeconds() {
        assertEquals("  1 hr, 1 min, 1 sec", formattingService.formatTime(20L * 3661L));
    }

    @Test
    void formatsDistanceUsingKilometersMetersAndCentimeters() {
        assertEquals("  1 km, 234 m, 56 cm", formattingService.formatDistance(123456L));
    }

    @Test
    void comparesNumericStringsNumerically() {
        assertEquals(-1, formattingService.numericComparator().compare("2", "10"));
    }
}
