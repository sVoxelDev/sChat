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

package net.silthus.schat.platform.messenger;

import java.util.Collection;
import net.silthus.schat.platform.plugin.TestPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class CrossServerMessengerMock implements Messenger, IncomingMessageConsumer {
    private final TestPlugin plugin;
    private final Collection<TestPlugin> servers;

    public CrossServerMessengerMock(TestPlugin plugin, Collection<TestPlugin> servers) {
        this.plugin = plugin;
        this.servers = servers;
    }

    @Override
    public void sendOutgoingMessage(@NotNull OutgoingMessage outgoingMessage) {
        servers.stream()
            .filter(p -> !p.equals(plugin))
            .forEach(p -> p.getMessenger().consumeIncomingMessageAsString(outgoingMessage.asEncodedString()));
    }

    @Override
    public boolean consumeIncomingMessage(@NonNull PluginMessage message) {
        return false;
    }

    @Override
    public boolean consumeIncomingMessageAsString(@NonNull String encodedString) {
        return false;
    }
}
