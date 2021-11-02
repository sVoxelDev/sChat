package net.silthus.chat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageTest extends TestBase {

    @Test
    void ofMessageString() {
        Message message = Message.message("Hello");
        assertThat(message)
                .extracting(
                        Message::getSource,
                        Message::getMessage,
                        Message::getTarget
                ).contains(
                        ChatSource.nil(),
                        "Hello",
                        ChatTarget.nil()
                );
    }

    @Test
    void withFormat_setsMessageFormat() {

        Message message = Message.message(ChatSource.player(server.addPlayer()), "test");
        assertThat(message.getFormat()).isNotNull();

        Format format = Format.miniMessage("!<sender_name>: <message>");
        message = message.withFormat(format);

        assertThat(message)
                .extracting(
                        Message::getFormat,
                        Message::getMessage,
                        msg -> toText(msg.formattedMessage())
                ).contains(
                        format,
                        "test",
                        "!Player0: test"
                );
    }

    @Test
    void target_isEmptyByDefault() {

        Message message = Message.message("test");
        assertThat(message.getTarget())
                .isNotNull()
                .isInstanceOf(NilChatTarget.class);
    }

    @Test
    void withTarget_setsMessageTarget() {
        Message message = Message.message("hi");
        ChatTarget target = ChatTarget.player(server.addPlayer());
        Message withTarget = message.to(target);

        assertThat(withTarget.getTarget())
                .isEqualTo(target);
    }

    @Test
    void withNullTarget_doesNotSetTarget() {

        Message message = Message.message("test").to(null);

        assertThat(message.getTarget())
                .isNotNull()
                .isInstanceOf(NilChatTarget.class);
        ChatTarget target = ChatTarget.player(server.addPlayer());
        message = message.to(target);

        assertThat(message.to(null).getTarget())
                .isSameAs(target);
    }

    @Test
    void send_sendsMessageToTarget() {

        ChatTarget target = ChatTarget.nil();
        Message message = Message.message("hi").to(target);

        message.send();

        assertThat(target.getLastReceivedMessage()).isEqualTo(message);
    }

    @Test
    void emptySource_usesDirectMessageFormat() {
        Message message = Message.message("test");
        assertThat(toText(message)).isEqualTo("test");
    }
}