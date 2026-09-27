package org.spond.weatherforecast.repository;

import org.spond.weatherforecast.model.SpondEvent;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.HashMap;

/**
 * Mocked repository of Spond's internal data
 * <p>
 * NOTE: Choosing to fetch directly from the database without optimization to keep it simple.
 *
 */
@Component
public class MockedSpondEventRepository {

    private static final HashMap<String, SpondEvent> mockedRepository = new HashMap<>();

    public MockedSpondEventRepository() {
        generateMockEvents();
    }

    /**
     * Simple test data generator. Would not be part of any final solution
     */
    private static void generateMockEvents() {
        mockedRepository.put("1", new SpondEvent(59.91, 10.75, ZonedDateTime.now(), ZonedDateTime.now()
            .plusHours(2)));
        mockedRepository.put("2", new SpondEvent(59.91, 10.75, ZonedDateTime.now()
            .plusDays(1), ZonedDateTime.now()
            .plusDays(1)
            .plusMinutes(90)));
        mockedRepository.put("3", new SpondEvent(59.91, 10.75, ZonedDateTime.now()
            .plusDays(2), ZonedDateTime.now()
            .plusDays(2)
            .plusHours(2)));
        mockedRepository.put("4", new SpondEvent(59.91, 10.75, ZonedDateTime.now()
            .plusDays(3), ZonedDateTime.now()
            .plusDays(3)
            .plusDays(4)));

        mockedRepository.put("5", new SpondEvent(60.3913, 5.3221, ZonedDateTime.now(),
            ZonedDateTime.now()
                .plusHours(2)));
        mockedRepository.put("6", new SpondEvent(60.3913, 5.3221, ZonedDateTime.now()
            .plusDays(3), ZonedDateTime.now()
            .plusDays(3)
            .plusHours(2)));
        mockedRepository.put("7", new SpondEvent(60.3913, 5.3221, ZonedDateTime.now()
            .plusDays(5), ZonedDateTime.now()
            .plusDays(5)
            .plusHours(2)));
        mockedRepository.put("8", new SpondEvent(60.3913, 5.3221, ZonedDateTime.now()
            .plusDays(7), ZonedDateTime.now()
            .plusDays(7)
            .plusHours(1)));
    }

    public SpondEvent getEvent(String id) {
        return mockedRepository.get(id);
    }

}
