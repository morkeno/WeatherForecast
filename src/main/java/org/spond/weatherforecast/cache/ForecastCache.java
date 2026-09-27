package org.spond.weatherforecast.cache;

import java.util.Optional;

/**
 * Stores MET forecasts keyed by coordinate.
 *
 */
public interface ForecastCache {

    Optional<CachedForecast> get(String key);

    void put(String key, CachedForecast entry);
}
