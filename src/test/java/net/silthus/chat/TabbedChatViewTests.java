package net.silthus.chat;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

public class TabbedChatViewTests extends TestBase {

    @Test
    void create() {
        TabbedChatView view = new TabbedChatView();
        view.render(ChatTarget.of(server.addPlayer()), Message.of("test"), Message.of("line 2"));
    }

    @Test
    void footer() {
        Component footer = new TabbedChatView().footer();
    }
}
