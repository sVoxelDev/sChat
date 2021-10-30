package net.silthus.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.chat.Constants.View.CHANNEL_DIVIDER;
import static org.assertj.core.api.Assertions.assertThat;

public class TabbedChatLayoutTests extends TestBase {

    private TabbedChatLayout view;
    private Chatter chatter;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        view = new TabbedChatLayout();
        chatter = Chatter.of(server.addPlayer());


    }

    @Test
    void create() {
        TabbedChatLayout view = new TabbedChatLayout();
        view.render(chatter, Message.of("test"), Message.of("line 2"));
    }

    @Test
    void footer() {
        Component footer = new TabbedChatLayout().footer();
    }

    @Test
    void clearChat_renders100BlankLines() {

        Component component = view.clearChat();
        assertThat(component.children())
                .hasSize(100)
                .allMatch(c -> c.equals(Component.newline()));
    }

    @Test
    void channels_renders_noChannelInfo() {

        assertThat(chatter.getSubscriptions()).isEmpty();

        Component component = view.channelTabs(chatter);
        String text = getStripedText(component);
        assertThat(text).isEqualTo(CHANNEL_DIVIDER + " No Channels selected. Use /ch join <channel> to join a channel.");
    }

    @Test
    void channels_renders_subscribedChannels() {
        addChannels();

        String text = getStripedText(view.channelTabs(chatter));
        assertThat(text)
                .contains(CHANNEL_DIVIDER + " test " + CHANNEL_DIVIDER)
                .contains(CHANNEL_DIVIDER + " foobar " + CHANNEL_DIVIDER);
    }

    @Test
    void channels_renders_activeChannelUnderlined() {
        addChannels();
        chatter.setActiveChannel(new Channel("active"));

        String text = getText(view.channelTabs(chatter));
        assertThat(text)
                .contains(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "active")
                .doesNotContain(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "test")
                .contains(ChatColor.GRAY + "" + "test");
    }

    private String getStripedText(Component component) {
        return ChatColor.stripColor(getText(component));
    }

    private String getText(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    private void addChannels() {
        chatter.subscribe(new Channel("test"));
        chatter.subscribe(new Channel("foobar"));
    }
}
