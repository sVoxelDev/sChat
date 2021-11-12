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

package net.silthus.chat.scopes;

import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Scopes;
import net.silthus.chat.TestBase;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.identities.Chatter;
import net.silthus.configmapper.ConfigurationException;
import org.bukkit.Location;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static net.silthus.chat.Constants.Scopes.WORLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class WorldScopeTests extends TestBase {

    private Channel channel;
    private WorldMock someWorld;
    private WorldMock otherWorld;
    private WorldScope scope;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        channel = createChannel("test");

        someWorld = server.addSimpleWorld("some-world");
        otherWorld = server.addSimpleWorld("some-other");

        MemoryConfiguration config = new MemoryConfiguration();
        config.set("worlds", List.of("world", "some-world"));

        scope = (WorldScope) Scopes.scope(WORLD, config);
    }

    @Test
    void worlds_isSet() {
        assertThat(scope.worlds)
                .isNotEmpty()
                .contains("world", "some-world");
    }

    @Test
    void newScope_withoutWorldsConfig_throws() {
        assertThatExceptionOfType(ConfigurationException.class)
                .isThrownBy(() -> Scopes.scope(WORLD));
    }

    @Test
    void filtersPlayersByWorld() {
        Chatter chatter1 = chatterInWorld(someWorld);
        Chatter chatter2 = chatterInWorld(someWorld);
        Chatter chatter3 = chatterInWorld(otherWorld);

        Collection<ChatTarget> targets = scope.apply(channel);
        assertThat(targets)
                .doesNotContain(chatter3)
                .contains(chatter1, chatter2);
    }

    private Chatter chatterInWorld(WorldMock world) {
        PlayerMock player = server.addPlayer();
        player.teleport(new Location(world, 1, 2, 3));
        Chatter chatter = Chatter.of(player);
        channel.addTarget(chatter);
        return chatter;
    }
}
