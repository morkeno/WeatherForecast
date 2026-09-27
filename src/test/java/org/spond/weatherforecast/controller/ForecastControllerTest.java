package org.spond.weatherforecast.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spond.weatherforecast.client.MetClient;
import org.spond.weatherforecast.client.dto.MetForecastResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ForecastControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MetClient metClient;

    @BeforeEach
    void setUp() throws Exception {
        MetForecastResponse sample = objectMapper.readValue(
            new ClassPathResource("met-compact-sample.json").getInputStream(),
            MetForecastResponse.class);
        given(metClient.getCompactForecast(anyDouble(), anyDouble())).willReturn(sample);
    }

    @Test
    void returnsForecastForKnownEvent() throws Exception {
        // Event "1" is seeded in MockedSpondEventRepository.
        mockMvc.perform(get("/api/v1/forecast/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.temperatureCelsius").isNumber())
            .andExpect(jsonPath("$.windSpeedMs").isNumber());
    }

    @Test
    void returnsNotFoundForUnknownEvent() throws Exception {
        mockMvc.perform(get("/api/v1/forecast/does-not-exist"))
            .andExpect(status().isNotFound());
    }
}
