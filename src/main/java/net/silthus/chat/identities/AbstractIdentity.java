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

package net.silthus.chat.identities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kyori.adventure.text.Component;
import net.silthus.chat.Identity;

import java.util.UUID;

@Data
@EqualsAndHashCode(of = {"uniqueId"})
public abstract class AbstractIdentity implements Identity {

    private final UUID uniqueId;
    private final String name;
    private Component displayName;

    protected AbstractIdentity(UUID id, String name) {
        this.uniqueId = id;
        this.name = name;
    }

    protected AbstractIdentity(String name) {
        this(UUID.randomUUID(), name);
    }

    public Component getDisplayName() {
        if (displayName != null)
            return displayName;
        return Component.text(getName());
    }

    public void setDisplayName(Component name) {
        this.displayName = name;
    }
}
