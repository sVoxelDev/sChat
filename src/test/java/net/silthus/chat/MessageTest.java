package net.silthus.chat;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class MessageTest extends TestBase {

    @Test
    void ofMessageString() {
        Message message = Message.message("Hello").build();

        assertThat(message.getSource())
                .isNotNull().isEqualTo(ChatSource.nil());
        assertThat(toText(message)).isEqualTo("Hello");
        assertThat(message.getTargets()).isEmpty();
    }

    @Test
    void target_isEmptyByDefault() {

        Message message = Message.message("test").build();
        assertThat(message.getTargets())
                .isEmpty();
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
    void toChannel_setsChannel_andTarget() {

        Channel channel = Channel.channel("test");
        Message message = Message.message("hi").to(channel).build();

        assertThat(message.getChannel())
                .isNotNull().isEqualTo(channel);
        assertThat(message.getTargets())
                .contains(channel);
    }

    @Test
    void emptySource_usesDirectMessageFormat() {
        Message message = Message.message("test").build();
        assertThat(toText(message)).isEqualTo("test");
    }

    @Test
    void message_withSource_usesDefaultFormat() {

        Message message = Message.message(ChatSource.named("test"), "Hi there!").build();

        assertThat(toText(message)).isEqualTo("test: Hi there!");
    }

    @Test
    void format_overrides_channelFormat() {

        Message message = ChatSource.named("test")
                .message("Hi")
                .to(Channel.channel("channel"))
                .format(Format.noFormat())
                .build();
        String text = toText(message);

        assertThat(text).isEqualTo("Hi");
    }

    @Test
    void format_usesChannelFormat_ifNotSet() {

        Message message = ChatSource.named("test")
                .message("Hi")
                .to(Channel.channel("channel"))
                .build();
        String text = toText(message);

        assertThat(text).isEqualTo("&6[&achannel&6]&etest&7: &aHi");
    }

    @Test
    void format_noFormat_noChannel_usesDefaultFormat() {

        Message message = ChatSource.named("test")
                .message("Hi")
                .build();
        String text = toText(message);

        assertThat(text).isEqualTo("test: Hi");
    }

    @Test
    void timestamp_isSetOnCreate() {
        Message message = Message.message("test").build();
        assertThat(message.getTimestamp())
                .isNotNull()
                .isCloseTo(Instant.now(), within(100, ChronoUnit.MILLIS));
    }

    @Test
    void copy_createsNewTimestamp() throws InterruptedException {
        Message message = Message.message("test").build();
        Thread.sleep(1L);
        Message copy = message.copy().build();

        assertThat(message.getTimestamp()).isNotEqualTo(copy.getTimestamp());
    }

    @Test
    void copy_isNotEqual() {
        Message message = Message.message("test").build();
        Message copy = message.copy().build();

        assertThat(message).isNotEqualTo(copy);
    }
}