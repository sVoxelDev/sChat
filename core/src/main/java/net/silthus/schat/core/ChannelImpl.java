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
import net.kyori.adventure.text.Component;
import net.silthus.schat.Channel;
import net.silthus.schat.Message;
import net.silthus.schat.Target;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static net.kyori.adventure.text.Component.text;

public class ChannelImpl implements Channel {

    @Getter
    private final String alias;
    @Getter
    private final Component displayName;
    private final List<Target> targets = new ArrayList<>();

    ChannelImpl(String alias) {
        this(alias, text(alias));
    }

    ChannelImpl(String alias, Component displayName) {
        if (alias == null || alias.isBlank())
            throw new InvalidAlias();
        this.alias = alias;
        this.displayName = displayName;
    }

    @Override
    @NotNull @Unmodifiable
    public List<Target> getTargets() {
        return Collections.unmodifiableList(targets);
    }

    @Override
    public void addTarget(final @NonNull Target target) {
        this.targets.add(target);
    }

    @Override
    public void removeTarget(final @NonNull Target target) {
        this.targets.remove(target);
    }

    @Override
    public final void sendMessage(final @NonNull Message message) {
        getTargets().forEach(target -> target.sendMessage(message));
    }

}
