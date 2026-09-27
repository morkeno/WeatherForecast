package org.spond.weatherforecast.exception;

/**
 * Thrown when no forecast is available for the requested location.
 */
public class ForecastNotFoundException extends RuntimeException {

    public ForecastNotFoundException(String location) {
        super("No forecast available for location: " + location);
    }
}
