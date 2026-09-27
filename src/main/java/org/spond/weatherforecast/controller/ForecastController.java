package org.spond.weatherforecast.controller;

import org.spond.weatherforecast.model.Forecast;
import org.spond.weatherforecast.service.ForecastService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/forecast")
@Validated
public class ForecastController {

    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    /**
     * Returns a multi-day daily forecast for a coordinate.
     *
     * <p>Example: {@code GET /api/v1/forecast?lat=59.91&lon=10.75&days=5}
     *
     * @param eventId  Spond Event ID (NOTE: assuming this contract exists)
     */
    @GetMapping("/{eventId}")
    public Forecast getForecast(
        @PathVariable String eventId
    ) {
        return forecastService.getForecast(eventId);
    }
}
