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

package net.silthus.schat.ui;

import java.time.Instant;
import java.util.Comparator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(of = {"message"})
final class MessageViewModel implements Comparable<MessageViewModel> {

    @Getter
    private final Message message;

    MessageViewModel(Message message) {
        this.message = message;
    }

    public Component getText() {
        return message.getText();
    }

    public boolean hasSource() {
        return message.getSource() != Identity.nil();
    }

    public Component getSource() {
        return message.getSource().getDisplayName();
    }

    private Instant getTimestamp() {
        return message.getTimestamp();
    }

    @Override
    public int compareTo(@NotNull MessageViewModel o) {
        return Comparator
            .comparing(MessageViewModel::getTimestamp)
            .compare(this, o);
    }

    public boolean isSystemMessage() {
        return message.getType() == Message.Type.SYSTEM;
    }

    public boolean isExcluded() {
        return message.isDeleted();
    }
}
