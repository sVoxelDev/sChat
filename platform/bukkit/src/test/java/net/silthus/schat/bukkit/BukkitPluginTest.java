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

package net.silthus.schat.bukkit;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import java.util.function.Function;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.settings.Setting;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BukkitPluginTest {

    private ServerMock server;
    private SChatBukkitPlugin plugin;
    private SChatBukkitBootstrap bootstrap;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        bootstrap = MockBukkit.load(BukkitLoader.class).getBootstrap();
        plugin = bootstrap().getPlugin();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    protected ServerMock server() {
        return server;
    }

    protected SChatBukkitPlugin plugin() {
        return plugin;
    }

    protected SChatBukkitBootstrap bootstrap() {
        return bootstrap;
    }

    protected <V> Channel channelWith(Setting<V> setting, V value) {
        return createChannelWith(builder -> builder.setting(setting, value));
    }

    protected Channel createChannelWith(Function<Channel.Builder, Channel.Builder> config) {
        final Channel channel = config.apply(Channel.channel(RandomStringUtils.random(10, "abcdefghijklmnopqrstuvwxyz0123456789"))).create();
        plugin().getChannels().add(channel);
        return channel;
    }

    protected Chatter chatter(PlayerMock player) {
        return plugin().getChatters().get(player.getUniqueId());
    }
}
