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
    void withFormat_setsMessageFormat() {

        Message message = Message.of(ChatSource.of(server.addPlayer()), "test");
        assertThat(message.format()).isNotNull();

        Format format = Format.builder().prefix("!").build();
        message = message.withFormat(format);

        assertThat(message)
                .extracting(
                        Message::format,
                        Message::message,
                        Message::formattedMessage
                ).contains(
                        format,
                        "test",
                        "!Player0: test"
                );
    }
}