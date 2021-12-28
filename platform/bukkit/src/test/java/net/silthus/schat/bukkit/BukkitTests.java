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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BukkitTests {

    private ServerMock server;
    private SChatBukkitBootstrap bootstrap;
    private SChatBukkitPlugin plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        bootstrap = MockBukkit.load(SChatBukkitBootstrap.class);
        plugin = bootstrap.getPlugin();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void onEnable_loadsChannels_fromConfig() {
        assertThat(plugin.getChannels().contains("global")).isTrue();
    }

    @Test
    void onJoin_createsUser() {
        final PlayerMock player = server.addPlayer();
        assertThat(plugin.getUsers().get(player.getUniqueId())).isNotNull();
    }
}
