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

package net.silthus.chat.conversations;

import net.silthus.chat.Chatter;
import net.silthus.chat.Conversation;
import net.silthus.chat.TestBase;
import net.silthus.chat.identities.Console;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ConversationManagerTests extends TestBase {

    private ConversationManager manager;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        manager = plugin.getConversationManager();
    }

    @Test
    void isCreatedOnEnable() {
        assertThat(plugin.getConversationManager()).isNotNull();
    }

    @Test
    void isEmpty_afterCreation() {
        assertThat(manager.getConversations()).noneMatch(conversation -> conversation instanceof PrivateConversation);
    }

    @Test
    void getConversation_returnsNull_forUnknown() {
        assertThat(manager.getConversation(UUID.randomUUID())).isNull();
    }

    @Test
    void getConversations_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> manager.getConversations().add(Channel.createChannel("test")));
    }

    @Test
    void getConversation_withChannelId_returnsChannel() {
        Channel channel = createChannel("test");
        assertThat(manager.getConversation(channel.getUniqueId()))
                .isNotNull().isEqualTo(channel);
    }

    @Test
    void getConversation_returnsRegisteredConversation() {
        Conversation conversation = registerConversation();
        assertThat(manager.getConversation(conversation.getUniqueId()))
                .isNotNull().isEqualTo(conversation);
    }

    @Test
    void register_addsConversation() {
        Conversation conversation = registerConversation();
        assertThat(manager.getConversations())
                .contains(conversation);
    }

    @NotNull
    private Conversation registerConversation() {
        Conversation conversation = Conversation.privateConversation(Chatter.player(server.addPlayer()), Console.console());
        manager.registerConversation(conversation);
        return conversation;
    }

    @Test
    void register_ConversationTwice_onlyRegistersOnce() {
        Conversation conversation = registerConversation();
        manager.registerConversation(conversation);

        assertThat(manager.getConversations()).containsOnlyOnce(conversation);
    }

    @Test
    void getConversation_betweenTargets_returnsRegisteredConversation() {
        Chatter target1 = Chatter.player(server.addPlayer());
        Chatter target2 = Chatter.player(server.addPlayer());
        Conversation conversation = Conversation.privateConversation(target1, target2);
        manager.registerConversation(conversation);

        assertThat(manager.getPrivateConversation(target1, target2))
                .isPresent().get()
                .isEqualTo(conversation);
    }

    @Test
    void getConversation_withMoreTargets_onlyReturnsExactMatch() {
        Chatter target1 = Chatter.player(server.addPlayer());
        Chatter target2 = Chatter.player(server.addPlayer());
        Chatter target3 = Chatter.player(server.addPlayer());
        Conversation conversation = Conversation.privateConversation(target1, target2);
        Conversation conversation2 = Conversation.privateConversation(target2, target3);
        manager.registerConversation(conversation);
        manager.registerConversation(conversation2);

        assertThat(manager.getPrivateConversation(target2))
                .isEmpty();
    }

    @Test
    void getConversations_includesChannels() {
        Chatter target1 = Chatter.player(server.addPlayer());
        Chatter target2 = Chatter.player(server.addPlayer());
        Conversation conversation = Conversation.privateConversation(target1, target2);
        final Channel channel = createChannel("test");
        assertThat(manager.getConversations())
                .contains(channel, conversation);
    }

    @Test
    void close_removesChannelFromRegistry() {
        final Channel channel = createChannel("test");
        assertThat(manager.getConversation(channel.getUniqueId())).isNotNull();
        assertThat(plugin.getChannelRegistry().contains(channel)).isTrue();

        channel.close();
        assertThat(manager.getConversation(channel.getUniqueId())).isNull();
        assertThat(plugin.getChannelRegistry().contains(channel)).isFalse();
    }
}
