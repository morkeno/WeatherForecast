package org.spond.weatherforecast.client;

import org.spond.weatherforecast.cache.CachedForecast;
import org.spond.weatherforecast.cache.ForecastCache;
import org.spond.weatherforecast.client.dto.MetForecastResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;

/**
 * Client for the MET Norway Locationforecast 2.0 "compact" product.
 *
 * <p>Caches per coordinate and honours the MET caching terms: a fresh cached
 * payload (per the {@code Expires} header) is served without a request, and a
 * stale one is revalidated with {@code If-Modified-Since} (a 304 refreshes the
 * freshness without re-downloading).
 *
 * @see <a href="https://api.met.no/doc/TermsOfService">MET API terms of service</a>
 */
@Component
public class MetClient {

    private final RestClient restClient;
    private final ForecastCache cache;

    public MetClient(
        RestClient.Builder builder,
        ForecastCache cache,
        @Value("${met.base-url}") String baseUrl,
        @Value("${met.user-agent}") String userAgent
    ) {
        this.restClient = builder
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
            .build();
        this.cache = cache;
    }

    /**
     * Fetches the compact forecast for a coordinate.
     */
    public MetForecastResponse getCompact(double lat, double lon) {
        double roundedLat = truncate(lat);
        double roundedLon = truncate(lon);
        String key = roundedLat + "," + roundedLon;

        Optional<CachedForecast> cached = cache.get(key);
        if (cached.isPresent() && cached.get().isFresh(Instant.now())) {
            return cached.get().response();
        }

        return restClient.get()
            .uri(uriBuilder -> uriBuilder.path("/compact")
                .queryParam("lat", roundedLat)
                .queryParam("lon", roundedLon)
                .build())
            .headers(headers -> cached
                .map(CachedForecast::lastModified)
                .ifPresent(lastModified -> headers.set(HttpHeaders.IF_MODIFIED_SINCE, lastModified)))
            .exchange((request, response) -> {
                int status = response.getStatusCode().value();
                if (status == 304 && cached.isPresent()) {
                    CachedForecast refreshed = new CachedForecast(
                        cached.get().response(),
                        expiresFrom(response.getHeaders()),
                        lastModifiedFrom(response.getHeaders(), cached.get().lastModified()));
                    cache.put(key, refreshed);
                    return refreshed.response();
                }
                if (response.getStatusCode().is2xxSuccessful()) {
                    MetForecastResponse body = response.bodyTo(MetForecastResponse.class);
                    cache.put(key, new CachedForecast(
                        body,
                        expiresFrom(response.getHeaders()),
                        response.getHeaders().getFirst(HttpHeaders.LAST_MODIFIED)));
                    return body;
                }
                throw new RestClientException("MET API returned status " + status);
            });
    }

    private static Instant expiresFrom(HttpHeaders headers) {
        long expires = headers.getExpires();
        return expires >= 0 ? Instant.ofEpochMilli(expires) : null;
    }

    private static String lastModifiedFrom(HttpHeaders headers, String fallback) {
        String lastModified = headers.getFirst(HttpHeaders.LAST_MODIFIED);
        return lastModified != null ? lastModified : fallback;
    }

    /**
     * Truncate (not round) to 4 decimals as required by the MET terms of service.
     */
    private static double truncate(double coordinate) {
        return BigDecimal.valueOf(coordinate)
            .setScale(4, RoundingMode.DOWN)
            .doubleValue();
    }
}
