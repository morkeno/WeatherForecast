package org.spond.weatherforecast.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ForecastControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsForecastForKnownLocation() throws Exception {
        mockMvc.perform(get("/api/v1/forecast/oslo").param("days", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].location").value("oslo"));
    }

    @Test
    void returnsNotFoundForUnknownLocation() throws Exception {
        mockMvc.perform(get("/api/v1/forecast/atlantis"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsOutOfRangeDays() throws Exception {
        mockMvc.perform(get("/api/v1/forecast/oslo").param("days", "99"))
                .andExpect(status().isBadRequest());
    }
}
