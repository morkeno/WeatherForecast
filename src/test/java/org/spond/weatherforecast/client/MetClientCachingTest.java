package org.spond.weatherforecast.client;

import org.junit.jupiter.api.Test;
import org.spond.weatherforecast.cache.InMemoryForecastCache;
import org.spond.weatherforecast.client.dto.MetForecastResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class MetClientCachingTest {

    private static final String BODY = """
        {
          "properties": {
            "meta": { "updated_at": "2026-09-27T12:00:00Z" },
            "timeseries": [
              { "time": "2026-09-27T12:00:00Z",
                "data": { "instant": { "details": { "air_temperature": 15.3, "wind_speed": 6.4 } } } }
            ]
          }
        }
        """;

    @Test
    void secondCallWithinExpiryIsServedFromCacheWithoutRequest() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.EXPIRES, rfc1123(Instant.now().plusSeconds(1800)));

        server.expect(requestTo("https://example.test/compact?lat=59.91&lon=10.75"))
                .andRespond(withSuccess(BODY, MediaType.APPLICATION_JSON).headers(headers));

        MetClient client = new MetClient(
                builder, new InMemoryForecastCache(), "https://example.test", "test-ua contact@example.com");

        MetForecastResponse first = client.getCompact(59.91, 10.75);
        MetForecastResponse second = client.getCompact(59.91, 10.75);

        // MockRestServiceServer expects exactly one request; the second call must hit the cache.
        server.verify();
        assertThat(first).isNotNull();
        assertThat(second).isSameAs(first);
    }

    private static String rfc1123(Instant instant) {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneOffset.UTC));
    }
}
