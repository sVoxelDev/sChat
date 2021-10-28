package net.silthus.chat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageTest extends TestBase {

    @Test
    void ofMessageString() {
        Message message = Message.of("Hello");
        assertThat(message)
                .extracting(
                        Message::getSource,
                        Message::getMessage
                ).contains(
                        null,
                        "Hello"
                );
    }

    @Test
    void withFormat_setsMessageFormat() {

        Message message = Message.of(ChatSource.of(server.addPlayer()), "test");
        assertThat(message.getFormat()).isNotNull();

        Format format = Format.builder().prefix("!").build();
        message = message.withFormat(format);

        assertThat(message)
                .extracting(
                        Message::getFormat,
                        Message::getMessage,
                        Message::formattedMessage
                ).contains(
                        format,
                        "test",
                        "!Player0: test"
                );
    }
}