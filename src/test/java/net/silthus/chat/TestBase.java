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

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.chat.Chat;
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.integrations.bungeecord.BungeecordIntegration;
import net.silthus.chat.integrations.protocollib.ChatPacketQueue;
import net.silthus.chat.integrations.vault.VaultProvider;
import org.apache.commons.lang.RandomStringUtils;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("UnstableApiUsage")
public abstract class TestBase {

    protected ServerMock server;
    protected SChat plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(SChat.class);
        plugin.setChatPacketQueue(new ChatPacketQueue(plugin));
        Chat chat = mock(Chat.class);
        when(chat.getPlayerPrefix(any())).thenReturn("&7[ADMIN]&a");
        when(chat.getPlayerSuffix(any())).thenReturn("[!]&a");
        plugin.setVaultProvider(new VaultProvider(chat));
        PlayerMock messageChannelSender = spy(new PlayerMock(server, "PluginMessageChannelSender"));
        doAnswer(invocation -> {
            byte[] message = invocation.getArgument(2);
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String messageType = in.readUTF();
            if (messageType.equals("Forward")) {
                in.readUTF();
                String channel = in.readUTF();
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                if (channel.equals(Constants.SCHAT_MESSAGES_CHANNEL)) {
                    out.writeUTF(channel);
                    short len = in.readShort();
                    byte[] bytes = new byte[len];
                    in.readFully(bytes);
                    out.writeShort(len);
                    out.write(bytes);
                }
                plugin.getBungeecord().onPluginMessageReceived(invocation.getArgument(1), messageChannelSender, out.toByteArray());
            } else if (messageType.equals("PlayerList")) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("ALL");
                out.writeUTF("Player1,Player2,Player3");
                plugin.getBungeecord().onPluginMessageReceived(invocation.getArgument(1), messageChannelSender, out.toByteArray());
            }
            return invocation;
        }).when(messageChannelSender).sendPluginMessage(eq(plugin), anyString(), any());
        plugin.setBungeecord(spy(new BungeecordIntegration(plugin, () -> messageChannelSender)));

    }

    @AfterEach
    @SneakyThrows
    public void tearDown() {
        Bukkit.getScheduler().cancelTasks(plugin);
        MockBukkit.unmock();
    }

    protected Stream<Listener> getRegisteredListeners() {
        return HandlerList.getRegisteredListeners(plugin).stream()
                .map(RegisteredListener::getListener);
    }

    protected void assertReceivedNoMessage(PlayerMock player) {
        assertThat(player.nextMessage()).isNull();
    }

    protected void assertReceivedMessage(PlayerMock player, String message) {
        assertThat(player.nextMessage()).isEqualTo(message);
    }

    protected Channel createChannel(Function<ChannelConfig, ChannelConfig> cfg) {
        return createChannel(RandomStringUtils.randomAlphabetic(10), cfg);
    }

    protected Channel createChannel(String identifier) {
        return createChannel(identifier, config -> config);
    }

    protected Channel createChannel(String identifier, Function<ChannelConfig, ChannelConfig> cfg) {
        return cfg.apply(ChannelConfig.defaults()).toChannel(identifier);
    }

    protected String toText(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    protected String toText(Message message) {
        return toText(message.formatted());
    }

    protected Message randomMessage() {
        return Message.message(RandomString.make() + "-" + Instant.now()).build();
    }

    @SneakyThrows
    protected Collection<Message> randomMessages(int count) {
        ArrayList<Message> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(randomMessage());
            Thread.sleep(10L);
        }
        Collections.shuffle(list);
        return list;
    }

}
