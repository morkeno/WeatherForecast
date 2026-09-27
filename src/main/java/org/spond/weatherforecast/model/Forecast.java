package org.spond.weatherforecast.model;

import java.time.LocalDate;

/**
 * A single day's weather forecast for a location.
 */
public record Forecast(
        String location,
        LocalDate date,
        double temperatureCelsius,
        String summary
) {
}
