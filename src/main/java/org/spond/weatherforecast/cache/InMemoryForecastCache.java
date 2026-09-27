package org.spond.weatherforecast.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Inmemory implementation of {@link ForecastCache}.
 * Useful for testing.
 */
@Component
@ConditionalOnProperty(name = "forecast.cache.type", havingValue = "in-memory", matchIfMissing = true)
public class InMemoryForecastCache implements ForecastCache {

    private final ConcurrentHashMap<String, CachedForecast> store = new ConcurrentHashMap<>();

    @Override
    public Optional<CachedForecast> get(String key) {
        return Optional.ofNullable(store.get(key));
    }

    @Override
    public void put(String key, CachedForecast entry) {
        store.put(key, entry);
    }
}
