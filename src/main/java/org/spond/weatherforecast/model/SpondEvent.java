package org.spond.weatherforecast.model;

import java.time.ZonedDateTime;

public record SpondEvent(double latitude, double longitude, ZonedDateTime start, ZonedDateTime end) {
}
