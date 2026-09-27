package org.spond.weatherforecast.cache;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryForecastCacheTest {

    private final InMemoryForecastCache cache = new InMemoryForecastCache();

    @Test
    void returnsEmptyForUnknownKey() {
        assertThat(cache.get("missing")).isEmpty();
    }

    @Test
    void storesAndReturnsEntry() {
        CachedForecast entry = new CachedForecast(null, Instant.now().plusSeconds(60), "then");
        cache.put("59.9,10.7", entry);

        assertThat(cache.get("59.9,10.7")).contains(entry);
    }

    @Test
    void freshnessReflectsExpiry() {
        Instant now = Instant.parse("2026-09-27T12:00:00Z");
        assertThat(new CachedForecast(null, now.plusSeconds(1), null).isFresh(now)).isTrue();
        assertThat(new CachedForecast(null, now.minusSeconds(1), null).isFresh(now)).isFalse();
        assertThat(new CachedForecast(null, null, null).isFresh(now)).isFalse();
    }
}
