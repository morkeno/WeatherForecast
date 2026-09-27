package org.spond.weatherforecast.service;

import org.spond.weatherforecast.model.SpondEvent;
import org.spond.weatherforecast.repository.MockedSpondEventRepository;
import org.springframework.stereotype.Component;

@Component
public class SpondEventService {

    private final MockedSpondEventRepository mockedSpondEventRepository;

    public SpondEventService(MockedSpondEventRepository mockedSpondEventRepository) {
        this.mockedSpondEventRepository = mockedSpondEventRepository;
    }

    public SpondEvent getEvent(String eventId) {
        return mockedSpondEventRepository.getEvent(eventId);
    }
}
