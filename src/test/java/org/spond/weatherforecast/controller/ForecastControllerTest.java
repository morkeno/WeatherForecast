package org.spond.weatherforecast.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spond.weatherforecast.client.MetClient;
import org.spond.weatherforecast.client.dto.MetForecastResponse;
import org.spond.weatherforecast.model.SpondEvent;
import org.spond.weatherforecast.service.SpondEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ForecastControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpondEventService spondEventService;

    @MockBean
    private MetClient metClient;

    @BeforeEach
    void setUp() {
        // Return a forecast entry at event "1"'s time so the match is within tolerance regardless of the clock.
        SpondEvent event = spondEventService.getEvent("1");
        MetForecastResponse response = responseAt(event.start().toOffsetDateTime(), 15.3, 6.4);
        given(metClient.getCompactForecast(anyDouble(), anyDouble())).willReturn(response);
    }

    @Test
    void returnsForecastForKnownEvent() throws Exception {
        // Event "1" is seeded in MockedSpondEventRepository.
        mockMvc.perform(get("/api/v1/forecast/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.temperatureCelsius").isNumber())
            .andExpect(jsonPath("$.windSpeedMs").isNumber());
    }

    @Test
    void returnsNotFoundForUnknownEvent() throws Exception {
        mockMvc.perform(get("/api/v1/forecast/does-not-exist"))
            .andExpect(status().isNotFound());
    }

    private static MetForecastResponse responseAt(OffsetDateTime time, double temp, double wind) {
        MetForecastResponse.Details details =
            new MetForecastResponse.Details(temp, wind, null, null, null, null, null);
        MetForecastResponse.Timeseries entry = new MetForecastResponse.Timeseries(time,
            new MetForecastResponse.Data(new MetForecastResponse.Instant(details), null, null, null));
        return new MetForecastResponse(null,
            new MetForecastResponse.Properties(null, List.of(entry)));
    }
}
