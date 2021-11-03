package net.silthus.chat.formats;

import net.silthus.chat.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MiniMessageFormatTests extends TestBase {

    @Test
    void create() {
        assertThat(toText("<message>", Message.message("test")))
                .isEqualTo("test");
    }

    @Test
    void withColor() {
        assertThat(toText("<green><message>", Message.message("test")))
                .isEqualTo("&atest");
    }

    @Test
    void withSource() {
        assertThat(toText("<sender_name>: <message>", Message.message(ChatSource.player(server.addPlayer()), "test")))
                .isEqualTo("Player0: test");
    }

    @Test
    void withNullSource() {
        assertThat(toText("<sender_name>: <message>", Message.message("test")))
                .isEqualTo(": test");
    }

    @Test
    void withChannelName() {
        Message message = Message.message(ChatSource.player(server.addPlayer()), "test")
                .to(ChatTarget.channel("test channel"));

        assertThat(toText("[<channel_name>]<sender_name>: <message>", message))
                .isEqualTo("[test channel]Player0: test");
    }

    @Test
    void withoutMessageTag_appendsMessageTag() {
        MiniMessageFormat format = new MiniMessageFormat("source: ");
        String text = toText(format.applyTo(Message.message("test")));
        assertThat(text).isEqualTo("source: test");
    }

    private String toText(String format, Message message) {
        return toText(Format.miniMessage(format).applyTo(message));
    }
}
