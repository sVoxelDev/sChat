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

package net.silthus.schat.core;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class IdentifiedChatterStub implements Chatter {

    public static Chatter identifiedChatter(final Identity identity) {
        return new IdentifiedChatterStub(identity);
    }

    public static Chatter identifiedChatter() {
        return new IdentifiedChatterStub(Identity.identity(UUID.randomUUID(), "Stub"));
    }

    private final Identity identity;

    private IdentifiedChatterStub(final Identity identity) {
        this.identity = identity;
    }

    @Override
    public @NotNull @Unmodifiable List<Message> getMessages() {
        return null;
    }

    @Override
    public @NotNull Optional<Channel> getActiveChannel() {
        return Optional.empty();
    }

    @Override
    public @NotNull @Unmodifiable List<Channel> getChannels() {
        return null;
    }

    @Override
    public @NotNull Identity getIdentity() {
        return identity;
    }

    @Override
    public void sendMessage(@NonNull final Message message) {

    }
}
