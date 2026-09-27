# WeatherForecastApp

A REST API built with Spring Boot 3 and Java 21 that returns the weather
forecast for the time and place a Spond event takes place, backed by the
[MET Norway Locationforecast API](https://api.met.no/).

## Run

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

Set a real contact in `met.user-agent` (`application.properties`) — api.met.no
returns 403 for a missing or generic User-Agent.

## Example

Look up the forecast for an event by its id:

```bash
curl "http://localhost:8080/api/v1/forecast/1"
```

```json
{
  "temperatureCelsius": 13.5,
  "windSpeedMs": 2.5
}
```

The forecast is taken from the MET entry closest to the event's start time.
Events `1`–`8` are seeded in `MockedSpondEventRepository` (in place of Spond's
real event store).

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
├── controller/ForecastController.java  # GET /api/v1/forecast/{eventId}
├── service/ForecastService.java        # event -> nearest MET forecast entry
├── service/SpondEventService.java      # event lookup
├── repository/MockedSpondEventRepository.java  # stubbed event store
├── client/MetClient.java               # MET Norway client (with caching)
├── client/dto/MetForecastResponse.java # MET response mapping
├── cache/                              # ForecastCache + in-memory implementation
├── model/Forecast.java, SpondEvent.java
└── exception/                          # domain exceptions + global handler
```

## Next steps
- Add a proper cache
- Add authentication
- Connect to a real event store
- Logging 
  * Request/Responses
  * Rate limiting
- Explore MET API further
- Improve the customer experience by using other data from the API
