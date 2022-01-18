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
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BukkitTests {
    protected static ServerMock server;
    protected static MockPlugin mockPlugin;
    protected static BukkitAudiences audiences;

    @BeforeAll
    static void beforeAll() {
        server = MockBukkit.mock();
        mockPlugin = MockBukkit.createMockPlugin();
    }

    @AfterAll
    static void afterAll() {
        MockBukkit.unmock();
    }

    @BeforeEach
    void setup() {
        audiences = BukkitAudiences.create(mockPlugin);
    }

    @AfterEach
    void teardown() {
        audiences.close();
    }

    protected void assertLastMessage(ConsoleCommandSenderMock mock, String message) {
        String nextMessage;
        String lastMessage = null;
        while ((nextMessage = mock.nextMessage()) != null) {
            lastMessage = nextMessage;
        }
        assertThat(message).isEqualTo(lastMessage);
    }
}
