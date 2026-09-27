# WeatherForecastApp

A REST API built with Spring Boot 3 and Java 21, backed by the
[MET Norway Locationforecast API](https://api.met.no/).

## Run

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

Set a real contact in `met.user-agent` (`application.properties`) — api.met.no
returns 403 for a missing or generic User-Agent.

## Example

```bash
curl "http://localhost:8080/api/v1/forecast?lat=59.91&lon=10.75&days=5"
```

Parameters: `lat`, `lon` (required), `altitude` (optional, meters), `days` (1–14, default 3).

Weather data by MET Norway, licensed under CC BY 4.0.

## Test

```bash
mvn test
```

The MET client is mocked in unit tests. A live test against api.met.no is gated:

```bash
MET_LIVE_TEST=true mvn test -Dtest=MetClientLiveTest
```

## Layout

```
src/main/java/org/spond/weatherforecast
├── WeatherForecastApplication.java     # Spring Boot entry point
├── controller/ForecastController.java  # REST endpoint
├── service/ForecastService.java        # Maps MET data to the response model
├── client/MetClient.java               # MET Norway API client
├── client/dto/MetForecastResponse.java # MET response mapping
├── model/Forecast.java                 # Response record
└── exception/                          # Domain exception + global handler
```
