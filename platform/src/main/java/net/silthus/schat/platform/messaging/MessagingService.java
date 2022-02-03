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

package net.silthus.schat.platform.messaging;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.schat.messaging.IncomingMessageConsumer;
import net.silthus.schat.messaging.Messenger;
import net.silthus.schat.messaging.PluginMessage;
import net.silthus.schat.messaging.PluginMessageSerializer;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.messaging.PluginMessage.of;

@Getter
public abstract class MessagingService implements Messenger, IncomingMessageConsumer {

    private final SchedulerAdapter scheduler;
    private final PluginMessageSerializer serializer;
    private final Set<UUID> processedMessages = new HashSet<>();

    protected MessagingService(SchedulerAdapter scheduler, PluginMessageSerializer serializer) {
        this.scheduler = scheduler;
        this.serializer = serializer;
    }

    @Override
    public final void sendPluginMessage(@NotNull PluginMessage message) {
        getScheduler().executeAsync(() -> sendOutgoingMessage(serializer.encode(of(message))));
    }

    protected abstract void sendOutgoingMessage(String data);

    @Override
    public boolean consumeIncomingMessage(@NonNull PluginMessage.Type message) {
        return processedMessages.add(message.id());
    }

    protected final boolean consumeIncomingMessageAsString(@NonNull String encodedString) {
        return consumeIncomingMessage(serializer.decode(encodedString));
    }
}
