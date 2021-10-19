package net.silthus.chat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SChatTest extends TestBase {

    @Test
    void instance_isSet() {
        assertThat(SChat.instance()).isNotNull();
    }

    @Test
    void createThrows() {
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(SChat::new);
    }
}