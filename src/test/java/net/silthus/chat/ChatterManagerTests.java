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

package net.silthus.chat;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.identities.Chatter;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ChatterManagerTests extends TestBase {

    private ChatterManager manager;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        manager = new ChatterManager(plugin);
    }

    @Test
    void onEnable_createsChatManager() {

        assertThat(plugin.getChatterManager())
                .isNotNull();
    }

    @Test
    void getChatters_isEmpty_afterInit() {
        assertThat(manager.getChatters()).isEmpty();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getChatters_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> manager.getChatters().add(Chatter.of(server.addPlayer())));
    }

    @Test
    void getChatter_createsChatterOnTheFly() {
        PlayerMock player = new PlayerMock(server, "test");
        Chatter chatter = manager.getOrCreateChatter(player);
        assertChatterIsPlayer(player, chatter);
        assertThat(getRegisteredListeners()).contains(chatter);
    }

    @Test
    void getChatter_doesNotCreateDuplicateChatter() {
        PlayerMock player = addChatter();
        assertThat(manager.getChatters()).hasSize(1);

        Chatter chatter = manager.getOrCreateChatter(player);

        assertThat(manager.getChatters()).hasSize(1);
        assertChatterIsPlayer(player, chatter);
    }

    private void assertChatterIsPlayer(PlayerMock player, Chatter chatter) {
        assertThat(chatter)
                .isNotNull()
                .extracting(
                        Identity::getUniqueId,
                        Identity::getName
                ).contains(
                        player.getUniqueId(),
                        player.getName()
                );
    }

    @NotNull
    private PlayerMock addChatter() {
        PlayerMock player = server.addPlayer();
        manager.registerChatter(player);
        return player;
    }

    @Test
    void getChatter_byId() {
        PlayerMock player = addChatter();

        assertChatterIsPlayer(player, manager.getChatter(player.getUniqueId()));
    }

    @Test
    void getChatter_byName() {
        PlayerMock player = addChatter();

        assertThat(manager.getChatter(player.getName()))
                .isPresent()
                .get().extracting(
                        Identity::getUniqueId,
                        Identity::getName
                ).contains(
                        player.getUniqueId(),
                        player.getName()
                );
    }

    @Test
    void registerChatter_addsPlayerToChatterCache() {

        Chatter chatter = registerChatter();

        assertThat(manager.getChatters())
                .hasSize(1)
                .containsOnly(chatter);
    }

    @Test
    void registerChatter_registersChatListeners() {

        Chatter chatter = manager.registerChatter(new PlayerMock(server, "test"));

        assertThat(getRegisteredListeners()).contains(chatter);
    }

    @Test
    void unregisterChatter_removesChatterFromCache() {

        Chatter chatter = registerChatter();

        manager.unregisterChatter(chatter.getPlayer());
        assertThat(manager.getChatters()).isEmpty();
    }

    @Test
    void unregisterChatter_removesChatterFromListeners() {

        Chatter chatter = registerChatter();
        assertThat(getRegisteredListeners()).contains(chatter);

        manager.unregisterChatter(chatter.getPlayer());
        assertThat(getRegisteredListeners()).doesNotContain(chatter);
    }

    @Test
    void unregisterChatter_unsubscribesChannels() {

        Chatter chatter = registerChatter();
        Channel channel = ChatTarget.channel("test");
        chatter.subscribe(channel);

        manager.unregisterChatter(chatter.getPlayer());
        assertThat(channel.getTargets()).isEmpty();
        assertThat(chatter.getConversations()).isEmpty();
    }

    private Chatter registerChatter() {
        PlayerMock player = new PlayerMock(server, "test");
        return manager.registerChatter(player);
    }

    @Nested
    class Listeners {

        private ChatterManager.PlayerListener listener;

        @BeforeEach
        void setUp() {
            listener = manager.playerListener;
        }

        @Test
        void registerListener() {

            assertThat(listener).isNotNull();
            assertThat(getRegisteredListeners())
                    .contains(listener);
        }

        @Test
        void playerIsAddedToChatters_onJoin() {

            Chatter chatter = Chatter.of(server.addPlayer());

            assertThat(manager.getChatters())
                    .hasSize(1)
                    .containsExactly(chatter);
            assertThat(getRegisteredListeners())
                    .contains(chatter);
        }

        @Test
        void onJoin_player_autoSubscribes_toConfiguredChannels() {

            Channel channel = ChatTarget.channel("test");
            plugin.getChannelRegistry().add(channel);
            PlayerMock player = new PlayerMock(server, "test");
            player.addAttachment(plugin, Constants.Permissions.getAutoJoinPermission(channel), true);
            assertThat(channel.getTargets()).isEmpty();

            server.addPlayer(player);
            assertThat(channel.getTargets()).contains(Chatter.of(player));
        }

        @Test
        void onJoin_doesNotSubscribe_toChannelsWithoutPermission() {

            Channel channel = createChannel(config -> config.protect(true));
            plugin.getChannelRegistry().add(channel);

            PlayerMock player = server.addPlayer();
            Chatter chatter = manager.getOrCreateChatter(player);
            assertThat(chatter.getConversations())
                    .doesNotContain(channel);
        }

        @Test
        void onJoin_doesNotJoinProtectedChannels_withoutPermission() {

            Channel channel = createChannel(config -> config.protect(true));
            plugin.getChannelRegistry().add(channel);

            PlayerMock player = new PlayerMock(server, "test");
            player.addAttachment(plugin, Constants.Permissions.getAutoJoinPermission(channel), true);
            assertThat(channel.getTargets()).isEmpty();

            server.addPlayer(player);
            assertThat(channel.getTargets()).doesNotContain(Chatter.of(player));
        }

        @Test
        void onJoin_channelWithAutoJoin_joinsAllRegardlessOfPermission() {

            Channel channel = createChannel(config -> config.autoJoin(true));
            plugin.getChannelRegistry().add(channel);

            Chatter chatter = Chatter.of(server.addPlayer());
            assertThat(channel.getTargets()).contains(chatter);
        }

        @Test
        void onQuit_removesPlayerFromChatters() {

            PlayerMock player = server.addPlayer();
            assertThat(manager.getChatters()).hasSize(1);

            quit(player);
            assertThat(manager.getChatters()).isEmpty();
            assertThat(getRegisteredListeners())
                    .doesNotContain(Chatter.of(player));
        }

        @Test
        void onQuit_noRegisteredPlayer_doesNothing() {

            PlayerMock player = new PlayerMock(server, "test");

            quit(player);
            assertThat(manager.getChatters()).isEmpty();
            assertThat(getRegisteredListeners())
                    .doesNotContain(Chatter.of(player));
        }

        private void quit(PlayerMock player) {
            listener.onQuit(new PlayerQuitEvent(player, Component.empty(), null));
        }
    }
}
