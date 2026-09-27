package org.spond.weatherforecast.repository;

import org.spond.weatherforecast.model.SpondEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Mocked repository of Spond's internal data
 * <p>
 * NOTE: Choosing to "fetch" directly from the database to keep it simple, assumes the DB is available for all
 * internal services (which it probably isn't)
 *
 */
@Component
public class MockedSpondEventRepository {

    private static final HashMap<String, SpondEvent> mockedRepository = new HashMap<>();

    public MockedSpondEventRepository() {
        // Generate mocked events on creation
        mockedRepository.put("1", new SpondEvent(59.91, 10.75, LocalDateTime.now(), LocalDateTime.now()
            .plusDays(1)));
        mockedRepository.put("2", new SpondEvent(59.91, 10.75, LocalDateTime.now()
            .plusDays(1), LocalDateTime.now()
            .plusDays(2)));
        mockedRepository.put("3", new SpondEvent(59.91, 10.75, LocalDateTime.now()
            .plusDays(2), LocalDateTime.now()
            .plusDays(3)));
        mockedRepository.put("4", new SpondEvent(59.91, 10.75, LocalDateTime.now()
            .plusDays(3), LocalDateTime.now()
            .plusDays(4)));
    }

    public SpondEvent getEvent(String id) {
        return mockedRepository.get(id);
    }

}
