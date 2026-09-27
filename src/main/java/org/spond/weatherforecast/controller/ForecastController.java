package org.spond.weatherforecast.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.spond.weatherforecast.model.Forecast;
import org.spond.weatherforecast.service.ForecastService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * Returns a multi-day forecast for a location.
     *
     * <p>Example: {@code GET /api/v1/forecast/oslo?days=5}
     */
    @GetMapping("/{location}")
    public List<Forecast> getForecast(
            @PathVariable @NotBlank String location,
            @RequestParam(defaultValue = "3") @Min(1) @Max(14) int days) {
        return forecastService.getForecast(location, days);
    }
}
