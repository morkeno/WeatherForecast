package org.spond.weatherforecast.client;

import org.spond.weatherforecast.client.dto.MetForecastResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Client for the MET Norway Locationforecast 2.0 "compact" product.
 *
 * <p>First increment: connect and fetch. It sends the identifying {@code User-Agent}
 * required by api.met.no (a missing/banned UA returns 403) and truncates
 * coordinates to 4 decimals (5+ decimals return 403).
 *
 * <p>Caching ({@code Expires} / {@code If-Modified-Since}) will be added in a
 * later increment.
 *
 * @see <a href="https://api.met.no/doc/TermsOfService">MET API terms of service</a>
 */
@Component
public class MetClient {

    private final RestClient restClient;

    public MetClient(
        RestClient.Builder builder,
        @Value("${met.base-url}") String baseUrl,
        @Value("${met.user-agent}") String userAgent
    ) {
        this.restClient = builder
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
            .build();
    }

    /**
     * Fetches the compact forecast for a coordinate.
     *
     * @param altitude ground height in whole meters, or {@code null} to let MET decide
     */
    public MetForecastResponse getCompact(double lat, double lon, Integer altitude) {
        double roundedLat = truncate(lat);
        double roundedLon = truncate(lon);

        return restClient.get()
            .uri(uriBuilder -> {
                uriBuilder.path("/compact")
                    .queryParam("lat", roundedLat)
                    .queryParam("lon", roundedLon);
                if (altitude != null) {
                    uriBuilder.queryParam("altitude", altitude);
                }
                return uriBuilder.build();
            })
            .retrieve()
            .body(MetForecastResponse.class);
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
