package org.spond.weatherforecast.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spond.weatherforecast.cache.CachedForecast;
import org.spond.weatherforecast.cache.ForecastCache;
import org.spond.weatherforecast.client.dto.MetForecastResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
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

    private static final Logger log = LoggerFactory.getLogger(MetClient.class);
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
    public MetForecastResponse getCompactForecast(double lat, double lon) {
        double roundedLat = truncate(lat);
        double roundedLon = truncate(lon);
        String key = roundedLat + "," + roundedLon;

        Optional<CachedForecast> cached = cache.get(key);
        if (cached.isPresent() && cached.get().isFresh(Instant.now())) {
            return cached.get().response();
        }

        return requestCompact(key, roundedLat, roundedLon, cached);
    }

    private MetForecastResponse requestCompact(
        String key, double roundedLat, double roundedLon, Optional<CachedForecast> cached) {

        return restClient.get()
            .uri(uriBuilder -> uriBuilder.path("/compact")
                .queryParam("lat", roundedLat)
                .queryParam("lon", roundedLon)
                .build())
            .headers(headers -> cached
                .map(CachedForecast::lastModified)
                .ifPresent(lastModified -> headers.set(HttpHeaders.IF_MODIFIED_SINCE, lastModified)))
            .exchange((request, response) -> handleResponse(key, cached, response));
    }

    private MetForecastResponse handleResponse(
        String key, Optional<CachedForecast> cached, ConvertibleClientHttpResponse response) throws IOException {

        HttpStatusCode statusCode = response.getStatusCode();
        int status = statusCode.value();

        // Documentated by MET
        if (status == 203) {
            log.warn("MET API returned 203, contract might have changed.");
        }

        if (status == 304 && cached.isPresent()) {
            return serveRevalidated(key, cached.get(), response.getHeaders());
        }
        if (statusCode.is2xxSuccessful()) {
            return storeFresh(key, response);
        }
        throw new RestClientException("MET API returned status " + status);
    }

    /** 304 Not Modified: keep the cached body, refresh only its freshness metadata. */
    private MetForecastResponse serveRevalidated(String key, CachedForecast cached, HttpHeaders headers) {
        CachedForecast refreshed = new CachedForecast(
            cached.response(),
            expiresFrom(headers),
            lastModifiedFrom(headers, cached.lastModified()));
        cache.put(key, refreshed);
        return refreshed.response();
    }

    /** Store the freshly downloaded body (read-through) and return it. */
    private MetForecastResponse storeFresh(String key, ConvertibleClientHttpResponse response) {
        MetForecastResponse body = response.bodyTo(MetForecastResponse.class);
        HttpHeaders headers = response.getHeaders();
        cache.put(key, new CachedForecast(
            body,
            expiresFrom(headers),
            headers.getFirst(HttpHeaders.LAST_MODIFIED)));
        return body;
    }

    /** MET's {@code Expires}, or {@code null} when absent — an entry with no expiry is treated as stale. */
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
