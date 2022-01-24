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

package net.silthus.schat.ui.model;

import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.pointer.Configured;
import net.silthus.schat.pointer.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static net.silthus.schat.pointer.Settings.createSettings;

@Getter
public class ChatterViewModel implements Configured.Modifiable<ChatterViewModel> {

    public static ChatterViewModel of(@NonNull Chatter chatter) {
        return new ChatterViewModel(chatter);
    }

    private final Chatter chatter;
    private final Settings settings = createSettings();

    ChatterViewModel(@NonNull Chatter chatter) {
        this.chatter = chatter;
    }

    public @NotNull @Unmodifiable List<Message> getMessages() {
        return chatter.getMessages().stream()
            .filter(this::isMessageDisplayed)
            .sorted()
            .toList();
    }

    private boolean isMessageDisplayed(Message message) {
        return notSentToChannel(message) || hasActiveChannel() && sentToActiveChannel(message);
    }

    private boolean notSentToChannel(Message message) {
        return message.channels().isEmpty();
    }

    private boolean hasActiveChannel() {
        return chatter.getActiveChannel().isPresent();
    }

    private boolean sentToActiveChannel(Message message) {
        return chatter.getActiveChannel().map(channel -> message.channels().contains(channel)).orElse(false);
    }

    public @NotNull @Unmodifiable List<Channel> getChannels() {
        return chatter.getChannels().stream().sorted().toList();
    }

    public boolean isActiveChannel(Channel channel) {
        return chatter.isActiveChannel(channel);
    }
}
