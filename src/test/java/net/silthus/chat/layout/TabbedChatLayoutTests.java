package net.silthus.chat.layout;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silthus.chat.*;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.kyori.adventure.text.Component.newline;
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
        chatter.setActiveChannel(ChatTarget.channel("test"));
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
                .allMatch(c -> c.equals(newline()));
    }

    @Test
    void channels_renders_noChannelInfo() {

        Chatter chatter = Chatter.of(new PlayerMock(server, "test"));
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
        chatter.setActiveChannel(ChatTarget.channel("active"));

        String text = getText(view.channelTabs(chatter));
        assertThat(text)
                .contains(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "active")
                .doesNotContain(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "test")
                .contains(ChatColor.GRAY + "" + "test");
    }

    @Test
    void supports_channelName_placeholders() {

        Channel channel = createChannel("foo", config -> config.name("<player_name>"));
        chatter.subscribe(channel);
        chatter.setActiveChannel(channel);

        String text = getText(view.channelTabs(chatter));

        assertThat(text).contains(chatter.getName());
    }

    @Test
    void renders_onlyUniqueMessages() {

        Message message = Message.message("test").format(Format.noFormat()).build();

        Component component = view.renderMessages(List.of(message, message));
        assertThat(toText(component)).containsOnlyOnce("test");
    }

    private String getStripedText(Component component) {
        return ChatColor.stripColor(getText(component));
    }

    private String getText(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    private void addChannels() {
        chatter.subscribe(ChatTarget.channel("test"));
        chatter.subscribe(ChatTarget.channel("foobar"));
    }
}
