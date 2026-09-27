package org.spond.weatherforecast.model;

import java.time.LocalDateTime;

public record SpondEvent(double latitude, double longitude, LocalDateTime start, LocalDateTime end) {
}
