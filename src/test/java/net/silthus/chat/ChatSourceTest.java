package net.silthus.chat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatSourceTest {

    @Test
    void of_identifier() {
        ChatSource source = ChatSource.of("test");
        assertIdAndName(source, "test", "test");
    }

    @Test
    void of_identifier_withDisplayName() {
        ChatSource source = ChatSource.of("test", "Test Source");
        assertIdAndName(source, "test", "Test Source");
    }

    private void assertIdAndName(ChatSource source, String id, String name) {
        assertThat(source)
                .isNotNull()
                .extracting(
                        ChatSource::getIdentifier,
                        ChatSource::getDisplayName
                ).contains(
                        id,
                        name
                );
    }
}