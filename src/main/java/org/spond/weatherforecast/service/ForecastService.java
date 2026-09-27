package org.spond.weatherforecast.service;

import org.spond.weatherforecast.exception.ForecastNotFoundException;
import org.spond.weatherforecast.model.Forecast;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Provides weather forecasts.
 *
 * <p>This is a skeleton implementation that returns stubbed data. Replace the
 * body with a real data source (e.g. an external weather API or a database).
 */
@Service
public class ForecastService {

    private static final List<String> KNOWN_LOCATIONS = List.of("oslo", "bergen", "trondheim");
    private static final String[] SUMMARIES = {"Sunny", "Cloudy", "Rainy", "Snowy"};

    /**
     * Returns a {@code days}-long forecast for the given location.
     *
     * @throws ForecastNotFoundException if the location is unknown
     */
    public List<Forecast> getForecast(String location, int days) {
        if (!KNOWN_LOCATIONS.contains(location.toLowerCase())) {
            throw new ForecastNotFoundException(location);
        }

        return IntStream.range(0, days)
                .mapToObj(offset -> new Forecast(
                        location,
                        LocalDate.now().plusDays(offset),
                        stubTemperature(offset),
                        SUMMARIES[offset % SUMMARIES.length]))
                .toList();
    }

    private double stubTemperature(int offset) {
        return 15.0 + (offset % 5);
    }
}
