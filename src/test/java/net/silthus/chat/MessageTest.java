package net.silthus.chat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageTest extends TestBase {

    @Test
    void ofMessageString() {
        Message message = Message.of("Hello");
        assertThat(message)
                .extracting(
                        Message::source,
                        Message::message
                ).contains(
                        null,
                        "Hello"
                );
    }

    @Test
    void format_updatesMessage() {

        Message message = Message.of(ChatSource.of(server.addPlayer()), "test")
                .format(Format.builder().build());

        assertThat(message)
                .extracting(
                        Message::formatted,
                        Message::message
                ).contains(
                        true,
                        "Player0: test"
                );
    }
}