package net.silthus.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class TabbedChatViewTests extends TestBase {

    private TabbedChatView view;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        view = new TabbedChatView();
    }

    @Test
    void create() {
        TabbedChatView view = new TabbedChatView();
        view.render(Chatter.of(server.addPlayer()), Message.of("test"), Message.of("line 2"));
    }

    @Test
    void footer() {
        Component footer = new TabbedChatView().footer();
    }

    @Test
    void clearChat_renders100BlankLines() {

        Component component = view.clearChat();
        assertThat(component.children())
                .hasSize(100)
                .allMatch(c -> c.equals(Component.newline()));
    }

    @Test
    void channels_rendersAllPlayerChannels() {

        Component component = view.channelTabs(Chatter.of(server.addPlayer()));
        String text = ChatColor.stripColor(LegacyComponentSerializer.legacySection().serialize(component));
        assertThat(text)
                .startsWith(Constants.View.CHANNEL_DIVIDER + " ")
                .endsWith(" " + Constants.View.CHANNEL_DIVIDER);
    }
}
