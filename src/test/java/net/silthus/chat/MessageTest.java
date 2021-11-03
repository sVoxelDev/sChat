package net.silthus.chat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageTest extends TestBase {

    @Test
    void ofMessageString() {
        Message message = Message.message("Hello").build();
        assertThat(message)
                .extracting(
                        Message::getSource,
                        this::toText,
                        Message::getTargets
                ).contains(
                        ChatSource.nil(),
                        "Hello",
                        ChatTarget.nil()
                );
    }

    @Test
    void target_isEmptyByDefault() {

        Message message = Message.message("test").build();
        assertThat(message.getTargets())
                .isEmpty();
    }

    @Test
    void withNullTarget_doesNotSetTarget() {

        Message message = Message.message("test").to((ChatTarget) null).build();

        assertThat(message.getTargets())
                .isNotNull()
                .doesNotContainNull()
                .first()
                .isInstanceOf(NilChatTarget.class);
        ChatTarget target = ChatTarget.player(server.addPlayer());

        Message.MessageBuilder builder = message.copy().to(target);

        assertThat(builder.to((ChatTarget) null).build().getTargets())
                .containsExactly(target);
    }

    @Test
    void copy_setsParentMessage() {

        Message parent = Message.message("test").build();
        Message message = parent.copy().text("hi").build();

        assertThat(message.getParent())
                .isNotNull()
                .isSameAs(parent);
    }

    @Test
    void send_sendsMessageToTarget() {

        ChatTarget target = ChatTarget.nil();
        Message message = Message.message("hi").to(target).send();

        assertThat(target.getLastReceivedMessage()).isEqualTo(message);
    }

    @Test
    void emptySource_usesDirectMessageFormat() {
        Message message = Message.message("test").build();
        assertThat(toText(message)).isEqualTo("test");
    }
}