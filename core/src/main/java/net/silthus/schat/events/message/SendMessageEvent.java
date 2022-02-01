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

package net.silthus.schat.events.message;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.silthus.schat.event.Cancellable;
import net.silthus.schat.event.SChatEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Targets;

/**
 * The {@code SendMessageEvent} is called before a message is sent to its targets.
 *
 * <p>You can still modify (add/remove) targets from the message when this event is called.</p>
 *
 * <p>Cancelling the event will prevent the message from being sent to its targets.</p>
 *
 * @since next
 */
@Accessors(fluent = true)
public final class SendMessageEvent implements SChatEvent, Cancellable {

    private final Message message;
    private final Targets targets;
    @Getter
    private final AtomicBoolean cancellationState = new AtomicBoolean(false);

    /**
     * Creates a new event.
     *
     * @param message the message of the event
     * @since next
     */
    public SendMessageEvent(final @NonNull Message message) {
        this.message = message;
        this.targets = Targets.copyOf(message.targets());
    }

    /**
     * Gets the message that will be sent after this event is finished.
     *
     * <p>You can modify the {@link Message#targets()} or cancel the sending.</p>
     *
     * @return the message that will be sent
     * @since next
     */
    public Message message() {
        return message;
    }

    public Targets targets() {
        return targets;
    }
}
