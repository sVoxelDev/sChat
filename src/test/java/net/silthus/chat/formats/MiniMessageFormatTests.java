package net.silthus.chat.formats;

import net.silthus.chat.Format;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MiniMessageFormatTests {

    @Test
    void create() {
        MiniMessageFormat format = Format.fromMiniMessage("<message>");
        assertThat(format).isNotNull();
    }
}
