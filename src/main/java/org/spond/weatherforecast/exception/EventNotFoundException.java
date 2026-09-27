package org.spond.weatherforecast.exception;

/**
 * Thrown when no event exists for the requested id.
 */
public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(String eventId) {
        super("No event found with id: " + eventId);
    }
}
