package org.spond.weatherforecast.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.spond.weatherforecast.client.dto.MetForecastResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Hits the real api.met.no. Disabled by default so the normal build stays
 * offline and deterministic; run it with {@code MET_LIVE_TEST=true mvn test}.
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "MET_LIVE_TEST", matches = "true")
class MetClientLiveTest {

    @Autowired
    private MetClient metClient;

    @Test
    void fetchesForecastForOslo() {
        MetForecastResponse response = metClient.getCompact(59.91, 10.75);

        assertThat(response).isNotNull();
        assertThat(response.properties()).isNotNull();
        assertThat(response.properties().timeseries()).isNotEmpty();
        assertThat(response.properties().timeseries().getFirst().data().instant().details().airTemperature())
                .isNotNull();
    }
}
