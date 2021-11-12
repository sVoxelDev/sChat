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

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Message;
import net.silthus.chat.TestBase;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.identities.Chatter;
import net.silthus.chat.identities.Console;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalScopeTests extends TestBase {

    private LocalScope scope;
    private Channel channel;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        scope = new LocalScope();
        scope.range = 10;
        channel = createChannel("test", config -> config.scope(scope));
        channel.addTarget(Console.console());
    }

    @Test
    void filters_playersNotInRange() {
        Chatter chatter1 = chatterAt(0, 0);
        Chatter chatter2 = chatterAt(5, 5);
        Chatter notInRange = chatterAt(11, 0);

        Collection<ChatTarget> targets = scope.apply(channel, chatter1.message("hi").to(channel).build());

        assertThat(targets).contains(chatter1, chatter2, Console.console())
                .doesNotContain(notInRange);
    }

    @Test
    void doesNotFilter_ifSourceIsNotAPlayer() {
        Chatter chatter1 = chatterAt(10, 20);
        Chatter chatter2 = chatterAt(100, 200);

        Collection<ChatTarget> targets = scope.apply(channel, Message.message("hi").build());
        assertThat(targets).contains(chatter1, chatter2, Console.console());
    }

    private Chatter chatterAt(double x, double y) {
        PlayerMock player = server.addPlayer();
        player.setLocation(new Location(player.getWorld(), x, 128, y));
        Chatter chatter = Chatter.of(player);
        channel.addTarget(chatter);
        return chatter;
    }
}
