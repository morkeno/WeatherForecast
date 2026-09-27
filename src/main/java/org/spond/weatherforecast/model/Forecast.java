package org.spond.weatherforecast.model;

/**
 * The weather forecast for an event
 */
public record Forecast(
        double temperatureCelsius,
        double windSpeedMs
) {
}
