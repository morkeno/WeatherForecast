package org.spond.weatherforecast.cache;

import org.spond.weatherforecast.client.dto.MetForecastResponse;

import java.time.Instant;

/**
 * A cached MET forecast together with the freshness metadata from the MET
 * response, used to honor the API's caching terms.
 *
 * @param response     the forecast payload
 * @param expires      when the payload becomes stale (from the {@code Expires} header), or {@code null}
 * @param lastModified the {@code Last-Modified} header value, echoed back as {@code If-Modified-Since}
 */
public record CachedForecast(MetForecastResponse response, Instant expires, String lastModified) {

    /** Whether the cached payload may still be served without revalidating. */
    public boolean isFresh(Instant now) {
        return expires != null && now.isBefore(expires);
    }
}
