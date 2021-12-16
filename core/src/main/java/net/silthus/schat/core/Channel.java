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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.silthus.schat.Message;
import net.silthus.schat.Target;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static net.kyori.adventure.text.Component.text;

public class Channel implements net.silthus.schat.Channel {

    @Getter
    private final @NotNull String alias;
    private final @NotNull List<Target> targets = new ArrayList<>();

    @Getter
    @Setter
    private @NonNull Component displayName;

    Channel(String alias) {
        this(alias, text(alias));
    }

    @SuppressWarnings("NullableProblems")
    Channel(String alias, final @NonNull Component displayName) {
        if (isInvalidAlias(alias))
            throw new InvalidAlias();
        this.alias = alias;
        this.displayName = displayName;
    }

    @Override
    public final void sendMessage(final @NonNull Message message) {
        getTargets().forEach(target -> target.sendMessage(message));
    }

    @Override
    @NotNull @Unmodifiable
    public List<Target> getTargets() {
        return Collections.unmodifiableList(targets);
    }

    public void addTarget(final @NonNull Target target) {
        this.targets.add(target);
    }

    public void removeTarget(final @NonNull Target target) {
        this.targets.remove(target);
    }

    private boolean isInvalidAlias(final String alias) {
        return alias == null || alias.isBlank();
    }

}
