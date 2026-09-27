# WeatherForecastApp

A skeleton REST API built with Spring Boot 3 and Java 21.

## Run

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

## Example

```bash
curl "http://localhost:8080/api/v1/forecast/oslo?days=5"
```

Known locations in the stub service: `oslo`, `bergen`, `trondheim`.

## Test

```bash
mvn test
```

## Layout

```
src/main/java/org/spond/weatherforecast
├── WeatherForecastApplication.java   # Spring Boot entry point
├── controller/ForecastController.java # REST endpoints
├── service/ForecastService.java       # Business logic (stubbed)
├── model/Forecast.java                # DTO (record)
└── exception/                         # Domain exception + global handler
```

Replace the stubbed data in `ForecastService` with a real data source
(external weather API, database, etc.).
