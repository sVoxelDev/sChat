/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.chat.renderer;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import net.silthus.chat.Chatter;
import net.silthus.chat.Constants;
import net.silthus.chat.Message;
import net.silthus.chat.TestBase;
import net.silthus.chat.config.FooterConfig;
import net.silthus.chat.conversations.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TabbedMessageRendererTests extends TestBase {

    private TabbedMessageRenderer view;
    private PlayerMock player;
    private Chatter chatter;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        view = new TabbedMessageRenderer();
        player = server.addPlayer();
        chatter = Chatter.player(player);
        chatter.setActiveConversation(createChannel("test"));
    }

    @Test
    void footer() {
        Component footer = view.footer(new View(chatter));

        assertThat(toText(footer).stripTrailing())
                .isEqualTo("&8\u250C&m\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500&r");
    }

    @Test
    void footer_isNotRendered_ifDisabled() {
        chatter.setActiveConversation(createChannel(config -> config.footer(FooterConfig.builder().enabled(false).build())));
        final Component footer = view.footer(new View(chatter));

        assertThat(footer).isEqualTo(Component.empty());
    }

    @Test
    void conversationTabs() {
        chatter.setActiveConversation(createChannel("test"));
        Component footer = view.conversationTabs(new View(chatter));

        assertThat(toText(footer).stripTrailing())
                .isEqualTo("""
                        &8\u2502 &7Global&8 \u2502 &4&n\u2718&a&ntest&8 \u2502 &4\u2718&7Trade&8 \u2502""");
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

        Chatter chatter = Chatter.player(new PlayerMock(server, "test"));
        assertThat(chatter.getConversations()).isEmpty();

        Component component = view.conversationTabs(new View(chatter));
        assertComponents(component, text().append(text("\u2502 ", DARK_GRAY))
                .append(text("Use ", GRAY)
                        .append(text("/ch <channel>", AQUA).clickEvent(suggestCommand("/ch ")))
                        .append(text(" to join a channel.", GRAY)))
                .build());
    }

    @Test
    void channels_renders_subscribedChannels() {
        addChannels();

        String text = cleaned(toText(view.conversationTabs(new View(chatter))));
        assertThat(text)
                .contains("test")
                .contains("foobar");
    }

    @Test
    void channels_renders_activeChannelUnderlined() {
        addChannels();
        chatter.setActiveConversation(createChannel("active"));

        String text = toText(view.conversationTabs(new View(chatter)));
        assertThat(text)
                .isEqualTo("&8\u2502 &4&n\u2718&a&nactive&8 \u2502 &4\u2718&7foobar&8 \u2502 &7Global&8 \u2502 &4\u2718&7test&8 \u2502 &4\u2718&7Trade&8 \u2502 ");
    }

    @Test
    void renders_unreadMessageCountNearChannelName() {
        Channel test = createChannel("test");
        Channel foo = createChannel("foo");
        chatter.setActiveConversation(test);
        chatter.subscribe(foo);

        foo.sendMessage("test");

        final String text = toText(view.conversationTabs(new View(chatter)));
        assertThat(text).contains("foo&c\u2081");
    }

    @Test
    void renders_leave_icon_forCanLeaveChannel() {
        final Channel test = createChannel("test");
        chatter.setActiveConversation(test);

        final String text = toCleanText(view.conversationTabs(chatter.getView()));
        assertThat(text).contains("\u2718test");
    }

    @Test
    void render_doesNotRenderLeaveIcon_forForcedChannel() {
        final Channel channel = createChannel(config -> config.canLeave(false));
        chatter.setActiveConversation(channel);

        final String text = toCleanText(view.conversationTabs(chatter.getView()));
        assertThat(text).contains(" " + channel.getName() + " ");
    }

    @Test
    void render_withHighlightMessage_withoutPermission_doesNotMarkMessage() {
        final Message message = Message.message("test").to(chatter).send();
        chatter.getView().selectedMessage(message);

        final String text = toCleanText(view.render(chatter.getView()));
        assertThat(text).contains("test");
    }

    @Test
    void render_withHighlightMessage_doesNotMarkMessage() {
        player.addAttachment(plugin, Constants.PERMISSION_SELECT_MESSAGE, true);
        final Message message = Message.message("test").to(chatter).send();
        chatter.getView().selectedMessage(message);

        final String text = toCleanText(view.render(chatter.getView()));
        assertThat(text).contains("> test");
    }


    private void addChannels() {
        chatter.subscribe(createChannel("test"));
        chatter.subscribe(createChannel("foobar"));
    }
}
