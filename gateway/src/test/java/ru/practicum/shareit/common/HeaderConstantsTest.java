package ru.practicum.shareit.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HeaderConstantsTest {

    @Test
    void headerConstant_shouldBeExpectedValue() {
        assertThat(HeaderConstants.X_SHARER_USER_ID).isEqualTo("X-Sharer-User-Id");
    }
}
