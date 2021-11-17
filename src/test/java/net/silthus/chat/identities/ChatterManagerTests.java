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

package net.silthus.chat.identities;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.*;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.persistence.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class ChatterManagerTests extends TestBase {

    private ChatterManager manager;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        manager = plugin.getChatterManager();
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
                .isThrownBy(() -> manager.getChatters().add(Chatter.player(server.addPlayer())));
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
    void removeChatter_removesChatterFromCache() {

        Chatter chatter = registerChatter();

        manager.removeChatter(chatter);
        assertThat(manager.getChatters()).isEmpty();
    }

    @Test
    void unregisterChatter_removesChatterFromListeners() {

        Chatter chatter = registerChatter();
        assertThat(getRegisteredListeners()).contains(chatter);

        manager.removeChatter(chatter);
        assertThat(getRegisteredListeners()).doesNotContain(chatter);
    }

    @Test
    void unregisterChatter_unsubscribesChannels() {

        Chatter chatter = registerChatter();
        Channel channel = ChatTarget.channel("test");
        chatter.subscribe(channel);

        manager.removeChatter(chatter);
        assertThat(channel.getTargets()).doesNotContain(chatter);
        assertThat(chatter.getConversations()).isEmpty();
    }

    @Test
    void unregisterAllChatters_unsubscribesAll() {
        server.setPlayers(3);
        assertThat(manager.getChatters()).hasSize(3);

        manager.removeAllChatters();
        assertThat(manager.getChatters()).isEmpty();
    }

    @Test
    void register_loadsPlayerData() {
        final PlayerChatter chatter = spy(new PlayerChatter(new PlayerMock(server, "test")));
        manager.registerChatter(chatter);

        verify(chatter).load();
    }

    @Test
    void remove_callsSave() {
        final PlayerChatter chatter = spy(new PlayerChatter(new PlayerMock(server, "test")));
        manager.registerChatter(chatter);
        manager.removeChatter(chatter);

        verify(chatter).save();
    }

    @Test
    void commandSender_withNameOnly_mapsId() {
        final CommandSender sender = mock(CommandSender.class);
        when(sender.getName()).thenReturn("Bob");
        final Chatter chatter = manager.getOrCreateChatter(sender);
        final Message message = chatter.sendMessage("hi");
        final Chatter chatter2 = manager.getOrCreateChatter(sender);

        assertThat(chatter).isSameAs(chatter2);
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
            Chatter chatter = Chatter.player(server.addPlayer());

            assertThat(manager.getChatters())
                    .hasSize(1)
                    .containsExactly(chatter);
            assertThat(getRegisteredListeners())
                    .contains(chatter);
        }

        @Test
        void onJoin_loadsPlayerData() {
            final PlayerMock player = new PlayerMock(server, "test");
            final Channel channel = createChannel("test");
            final Chatter chatter = mock(PlayerChatter.class);
            when(chatter.getActiveConversation()).thenReturn(channel);
            player.getPersistentDataContainer().set(Constants.Persistence.PLAYER_DATA, PlayerData.type(), new PlayerData(chatter));

            server.addPlayer(player);
            final Chatter joinedChatter = Chatter.player(player);
            assertThat(joinedChatter.getActiveConversation()).isEqualTo(channel);
        }

        @Test
        void onJoin_player_autoSubscribes_toConfiguredChannels() {
            Channel channel = createChannel(config -> config.sendToConsole(false));
            plugin.getChannelRegistry().add(channel);
            PlayerMock player = new PlayerMock(server, "test");
            player.addAttachment(plugin, Constants.Permissions.getAutoJoinPermission(channel), true);
            assertThat(channel.getTargets()).isEmpty();

            server.addPlayer(player);
            Chatter chatter = Chatter.player(player);
            assertThat(channel.getTargets()).contains(chatter);
            assertThat(chatter.getConversations()).contains(channel);
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
            Channel channel = createChannel(config -> config.protect(true).sendToConsole(false));
            plugin.getChannelRegistry().add(channel);

            PlayerMock player = new PlayerMock(server, "test");
            player.addAttachment(plugin, Constants.Permissions.getAutoJoinPermission(channel), true);
            assertThat(channel.getTargets()).isEmpty();

            server.addPlayer(player);
            assertThat(channel.getTargets()).doesNotContain(Chatter.player(player));
        }

        @Test
        void onJoin_channelWithAutoJoin_joinsAllRegardlessOfPermission() {
            Channel channel = createChannel(config -> config.autoJoin(true));
            plugin.getChannelRegistry().add(channel);

            Chatter chatter = Chatter.player(server.addPlayer());
            assertThat(channel.getTargets()).contains(chatter);
        }

        @Test
        void onJoin_synchronizesChatter_withOtherServers() {
            Chatter chatter = Chatter.player(server.addPlayer());
            verify(plugin.getBungeecord()).sendChatter(chatter);
        }

        @Test
        void onQuit_callsSaveOnChatter() {
            final PlayerMock player = new PlayerMock(server, "Test");
            final PlayerChatter chatter = spy(new PlayerChatter(player));
            manager.registerChatter(chatter);
            server.addPlayer(player);

            listener.onQuit(new PlayerQuitEvent(player, ""));
            verify(chatter).save();
        }
    }
}
