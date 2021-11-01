package net.silthus.chat;

import net.silthus.chat.formats.SimpleFormat;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageTest extends TestBase {

    @Test
    void ofMessageString() {
        Message message = Message.of("Hello");
        assertThat(message)
                .extracting(
                        Message::getSource,
                        Message::getMessage,
                        Message::getTarget
                ).contains(
                        null,
                        "Hello",
                        ChatTarget.empty()
                );
    }

    @Test
    void withFormat_setsMessageFormat() {

        Message message = Message.of(ChatSource.of(server.addPlayer()), "test");
        assertThat(message.getFormat()).isNotNull();

        SimpleFormat format = SimpleFormat.builder().prefix("!").build();
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

    @Test
    void target_isEmptyByDefault() {

        Message message = Message.of("test");
        assertThat(message.getTarget())
                .isNotNull()
                .isInstanceOf(EmptyChatTarget.class);
    }

    @Test
    void withTarget_setsMessageTarget() {
        Message message = Message.of("hi");
        ChatTarget target = ChatTarget.of(server.addPlayer());
        Message withTarget = message.withTarget(target);

        assertThat(withTarget.getTarget())
                .isEqualTo(target);
    }

    @Test
    void withNullTarget_doesNotSetTarget() {

        Message message = Message.of("test").withTarget(null);

        assertThat(message.getTarget())
                .isNotNull()
                .isInstanceOf(EmptyChatTarget.class);
        ChatTarget target = ChatTarget.of(server.addPlayer());
        message = message.withTarget(target);

        assertThat(message.withTarget(null).getTarget())
                .isSameAs(target);
    }

    @Test
    void send_sendsMessageToTarget() {

        ChatTarget target = ChatTarget.empty();
        Message message = Message.of("hi").withTarget(target);

        message.send();

        assertThat(target.getLastReceivedMessage()).isEqualTo(message);
    }
}