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
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Format;
import net.silthus.chat.Message;
import net.silthus.chat.TestBase;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.identities.Chatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.newline;
import static org.assertj.core.api.Assertions.assertThat;

public class TabbedMessageRendererTests extends TestBase {

    private TabbedMessageRenderer view;
    private Chatter chatter;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        view = new TabbedMessageRenderer();
        chatter = Chatter.of(server.addPlayer());
        chatter.setActiveConversation(ChatTarget.channel("test"));
    }

    @Test
    void footer() {
        Component footer = view.footer(chatter);

        assertThat(toText(footer).stripTrailing())
                .isEqualTo("""
                        ----------------------------------------------------
                        &8| &7Global&8 | &a&ntest&8 |""");
    }

    @Test
    void conversationTabs() {
        chatter.setActiveConversation(Channel.channel("test"));
        Component footer = view.conversationTabs(chatter);

        assertThat(toText(footer).stripTrailing())
                .isEqualTo("""
                        &8| &7Global&8 | &a&ntest&8 |""");
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
        assertThat(chatter.getConversations()).isEmpty();

        Component component = view.conversationTabs(chatter);
        String text = cleaned(toText(component));
        assertThat(text).isEqualTo("&8| &7No Channels selected. Use &b/ch join <channel> &7to join a channel.");
    }

    @Test
    void channels_renders_subscribedChannels() {
        addChannels();

        String text = cleaned(toText(view.conversationTabs(chatter)));
        assertThat(text)
                .contains("test")
                .contains("foobar");
    }

    @Test
    void channels_renders_activeChannelUnderlined() {
        addChannels();
        chatter.setActiveConversation(ChatTarget.channel("active"));

        String text = toText(view.conversationTabs(chatter));
        assertThat(text)
                .isEqualTo("&8| &a&nactive&8 | &7foobar&8 | &7Global&8 | &7test&8 | ");
    }

    @Test
    void supports_channelName_placeholders() {

        Channel channel = createChannel("foo", config -> config.name("<player_name>"));
        chatter.subscribe(channel);
        chatter.setActiveConversation(channel);

        String text = toText(view.conversationTabs(chatter));

        assertThat(text).contains(toText(chatter.getDisplayName()));
    }

    @Test
    void renders_onlyUniqueMessages() {

        Message message = Message.message("test").format(Format.noFormat()).build();

        Component component = view.renderMessages(List.of(message, message));
        assertThat(toText(component)).containsOnlyOnce("test");
    }

    @Test
    void ordersMessages_byTimestamp() {
        Collection<Message> messages = randomMessages(10);
        String sortedMessages = messages.stream().sorted().map(this::toText).collect(Collectors.joining("\n"));
        Component component = view.renderMessages(messages);
        assertThat(toText(component)).isEqualTo(sortedMessages);
    }

    private void addChannels() {
        chatter.subscribe(ChatTarget.channel("test"));
        chatter.subscribe(ChatTarget.channel("foobar"));
    }
}
