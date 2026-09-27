package org.spond.weatherforecast.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spond.weatherforecast.client.MetClient;
import org.spond.weatherforecast.client.dto.MetForecastResponse;
import org.spond.weatherforecast.exception.EventNotFoundException;
import org.spond.weatherforecast.exception.ForecastNotFoundException;
import org.spond.weatherforecast.model.Forecast;
import org.spond.weatherforecast.model.SpondEvent;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ForecastServiceTest {

    @Mock
    private SpondEventService spondEventService;

    @Mock
    private MetClient metClient;

    @InjectMocks
    private ForecastService forecastService;

    @Test
    void returnsForecastForEntryClosestToEventStart() {
        // 20:00 Europe/Oslo (CEST, UTC+2) == 18:00Z, matching the second entry exactly.
        SpondEvent event = new SpondEvent(59.91, 10.75,
                LocalDateTime.parse("2026-09-27T20:00:00"),
                LocalDateTime.parse("2026-09-27T22:00:00"));
        given(spondEventService.getEvent("1")).willReturn(event);
        given(metClient.getCompact(anyDouble(), anyDouble())).willReturn(response(
                entry("2026-09-27T12:00:00Z", 10.0, 3.0),
                entry("2026-09-27T18:00:00Z", 20.0, 8.0),
                entry("2026-09-28T00:00:00Z", 5.0, 1.0)));

        Forecast forecast = forecastService.getForecast("1");

        assertThat(forecast.temperatureCelsius()).isEqualTo(20.0);
        assertThat(forecast.windSpeedMs()).isEqualTo(8.0);
    }

    @Test
    void throwsWhenEventMissing() {
        given(spondEventService.getEvent("nope")).willReturn(null);

        assertThatThrownBy(() -> forecastService.getForecast("nope"))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void throwsWhenNoTimeseries() {
        given(spondEventService.getEvent("1")).willReturn(new SpondEvent(59.91, 10.75,
                LocalDateTime.parse("2026-09-27T17:00:00"),
                LocalDateTime.parse("2026-09-27T19:00:00")));
        given(metClient.getCompact(anyDouble(), anyDouble())).willReturn(response());

        assertThatThrownBy(() -> forecastService.getForecast("1"))
                .isInstanceOf(ForecastNotFoundException.class);
    }

    private static MetForecastResponse response(MetForecastResponse.Timeseries... entries) {
        return new MetForecastResponse(null,
                new MetForecastResponse.Properties(null, List.of(entries)));
    }

    private static MetForecastResponse.Timeseries entry(String time, double temp, double wind) {
        MetForecastResponse.Details details =
                new MetForecastResponse.Details(temp, wind, null, null, null, null, null);
        MetForecastResponse.Data data =
                new MetForecastResponse.Data(new MetForecastResponse.Instant(details), null, null, null);
        return new MetForecastResponse.Timeseries(OffsetDateTime.parse(time), data);
    }
}
