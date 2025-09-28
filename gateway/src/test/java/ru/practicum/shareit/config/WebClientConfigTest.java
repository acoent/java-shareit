package ru.practicum.shareit.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class WebClientConfigTest {

    @Test
    void restTemplateBean_shouldBeNotNull() {
        WebClientConfig cfg = new WebClientConfig();
        RestTemplate rt = cfg.restTemplate();
        assertThat(rt).isNotNull();
    }
}
