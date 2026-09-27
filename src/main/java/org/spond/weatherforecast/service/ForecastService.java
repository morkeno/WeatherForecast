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
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

/**
 * Provides weather forecasts, backed by the MET Norway API.
 */
@Service
public class ForecastService {

    private static final ZoneId EVENT_ZONE = ZoneId.of("Europe/Oslo");

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

        MetForecastResponse response = metClient.getCompact(event.latitude(), event.longitude());

        OffsetDateTime eventTime = event.start()
            .atZone(EVENT_ZONE)
            .toOffsetDateTime();

        return timeseries(response).stream()
            .filter(entry -> entry.time() != null)
            .min(Comparator.comparing(entry -> Duration.between(entry.time(), eventTime)
                .abs()))
            .map(ForecastService::toForecast)
            .orElseThrow(() -> new ForecastNotFoundException(event.latitude(), event.longitude()));
    }

    private static List<MetForecastResponse.Timeseries> timeseries(MetForecastResponse response) {
        if (response == null || response.properties() == null
            || response.properties()
            .timeseries() == null) {
            return List.of();
        }
        return response.properties()
            .timeseries();
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
