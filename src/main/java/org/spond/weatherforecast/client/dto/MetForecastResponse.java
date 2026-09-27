package org.spond.weatherforecast.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Maps the GeoJSON response of the MET Norway Locationforecast 2.0 "compact" product.
 *
 * @see <a href="https://api.met.no/weatherapi/locationforecast/2.0/documentation">Locationforecast docs</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MetForecastResponse(
    @JsonProperty("geometry") Geometry geometry,
    @JsonProperty("properties") Properties properties) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Geometry(@JsonProperty("coordinates") List<Double> coordinates) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Properties(
        @JsonProperty("meta") Meta meta,
        @JsonProperty("timeseries") List<Timeseries> timeseries) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Meta(
        @JsonProperty("updated_at") OffsetDateTime updatedAt,
        @JsonProperty("units") Map<String, String> units) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Timeseries(
        @JsonProperty("time") OffsetDateTime time,
        @JsonProperty("data") Data data) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(
        @JsonProperty("instant") Instant instant,
        @JsonProperty("next_1_hours") Period next1Hours,
        @JsonProperty("next_6_hours") Period next6Hours,
        @JsonProperty("next_12_hours") Period next12Hours) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Instant(@JsonProperty("details") Details details) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Period(
        @JsonProperty("summary") Summary summary,
        @JsonProperty("details") Details details) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Summary(@JsonProperty("symbol_code") String symbolCode) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Details(
        @JsonProperty("air_temperature") Double airTemperature,
        @JsonProperty("wind_speed") Double windSpeed,
        @JsonProperty("wind_from_direction") Double windFromDirection,
        @JsonProperty("relative_humidity") Double relativeHumidity,
        @JsonProperty("cloud_area_fraction") Double cloudAreaFraction,
        @JsonProperty("air_pressure_at_sea_level") Double airPressureAtSeaLevel,
        @JsonProperty("precipitation_amount") Double precipitationAmount) {
    }
}
