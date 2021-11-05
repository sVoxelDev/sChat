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

package net.silthus.chat.targets;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Conversation;
import net.silthus.chat.Format;

import java.util.*;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class AbstractConversation extends AbstractChatTarget implements Conversation {

    private final Set<ChatTarget> targets = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<>()));
    private Format format = Format.defaultFormat();

    protected AbstractConversation(String identifier) {
        super(identifier);
    }

    @Override
    public Collection<ChatTarget> getTargets() {
        return List.copyOf(targets);
    }

    @Override
    public void addTarget(@NonNull ChatTarget target) {
        this.targets.add(target);
    }

    @Override
    public void removeTarget(@NonNull ChatTarget target) {
        this.targets.remove(target);
    }

}
