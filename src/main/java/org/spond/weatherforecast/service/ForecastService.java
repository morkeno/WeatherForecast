package org.spond.weatherforecast.service;

import org.spond.weatherforecast.client.MetClient;
import org.spond.weatherforecast.client.dto.MetForecastResponse;
import org.spond.weatherforecast.exception.EventNotFoundException;
import org.spond.weatherforecast.exception.ForecastNotFoundException;
import org.spond.weatherforecast.model.Forecast;
import org.spond.weatherforecast.model.SpondEvent;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Provides weather forecasts, backed by the MET Norway API.
 */
@Service
public class ForecastService {

    /** Largest gap between an event and the nearest forecast entry we accept (MET is 6-hourly far out). */
    private static final Duration MAX_MATCH_DISTANCE = Duration.ofHours(6);

    private final SpondEventService spondEventService;
    private final MetClient metClient;

    public ForecastService(SpondEventService spondEventService, MetClient metClient) {
        this.spondEventService = spondEventService;
        this.metClient = metClient;
    }

    /**
     * Returns the forecast for the time and place the given event takes place.
     *
     * @throws EventNotFoundException    if no event exists for the id
     * @throws ForecastNotFoundException if MET returns no forecast for the event's coordinate
     */
    public Forecast getForecast(String eventId) {
        SpondEvent event = spondEventService.getEvent(eventId);
        if (event == null) {
            throw new EventNotFoundException(eventId);
        }

        MetForecastResponse response = metClient.getCompactForecast(event.latitude(), event.longitude());

        OffsetDateTime eventTime = event.start()
            .toOffsetDateTime();

        MetForecastResponse.Timeseries closest = timeseries(response).stream()
            .filter(entry -> entry.time() != null)
            .min(Comparator.comparing(entry -> Duration.between(entry.time(), eventTime)
                .abs()))
            .orElseThrow(() -> new ForecastNotFoundException(event.latitude(), event.longitude()));

        // Guard against events outside MET's forecast range (too far ahead, or in the past).
        if (Duration.between(closest.time(), eventTime).abs().compareTo(MAX_MATCH_DISTANCE) > 0) {
            throw new ForecastNotFoundException(event.latitude(), event.longitude());
        }

        return toForecast(closest);
    }

    private static List<MetForecastResponse.Timeseries> timeseries(MetForecastResponse response) {
        if (response == null || response.properties() == null
            || response.properties().timeseries() == null) {
            return List.of();
        }
        return response.properties().timeseries();
    }

    private static Forecast toForecast(MetForecastResponse.Timeseries entry) {
        MetForecastResponse.Data data = entry.data();

        if (data == null) {
            throw new ForecastNotFoundException(Double.NaN, Double.NaN);
        }

        MetForecastResponse.Details details = entry.data()
            .instant()
            .details();

        if (details == null) {
            throw new ForecastNotFoundException(Double.NaN, Double.NaN);
        }
        double temperature = details.airTemperature();
        double windSpeed = details.windSpeed();

        return new Forecast(temperature, windSpeed);
    }
}
