package org.spond.weatherforecast.exception;

/**
 * Thrown when MET returns no forecast data for the requested coordinate.
 */
public class ForecastNotFoundException extends RuntimeException {

    public ForecastNotFoundException(double latitude, double longitude) {
        super("No forecast available for coordinate: " + latitude + ", " + longitude);
    }
}
