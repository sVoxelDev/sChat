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

package net.silthus.schat.platform.sender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.identity.Identity;

import static net.kyori.adventure.text.JoinConfiguration.noSeparators;

/**
 * Simple implementation of {@link Sender} using a {@link SenderFactory}.
 *
 * @param <T> the command sender type
 */
@EqualsAndHashCode(of = {"identity"})
final class FactorySender<T> implements Sender {

    private final SenderFactory<T> factory;
    @Getter(AccessLevel.PROTECTED)
    private final T handle;
    @Getter
    private final Identity identity;
    @Getter
    private final boolean console;

    FactorySender(SenderFactory<T> factory, T sender) {
        this.factory = factory;
        this.handle = sender;
        this.console = factory.isConsole(sender);
        this.identity = factory.getIdentity(sender);
    }

    @Override
    public void sendMessage(Component message) {
        if (isConsole()) {
            for (Component line : splitNewlines(message)) {
                factory.sendMessage(handle, line);
            }
        } else {
            factory.sendMessage(handle, message);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return isConsole() || factory.hasPermission(handle, permission);
    }

    @Override
    public void performCommand(String commandLine) {
        factory.performCommand(handle, commandLine);
    }

    @Override
    public boolean isValid() {
        return isConsole() || factory.isPlayerOnline(getUniqueId());
    }

    // A small utility method which splits components built using
    // > join(newLine(), components...)
    // back into separate components.
    private static Iterable<Component> splitNewlines(Component message) {
        if (message instanceof TextComponent && message.style().isEmpty() && !message.children().isEmpty() && ((TextComponent) message).content().isEmpty()) {
            LinkedList<List<Component>> split = new LinkedList<>();
            split.add(new ArrayList<>());

            for (Component child : message.children()) {
                if (Component.newline().equals(child)) {
                    split.add(new ArrayList<>());
                } else {
                    Iterator<Component> splitChildren = splitNewlines(child).iterator();
                    if (splitChildren.hasNext()) {
                        split.getLast().add(splitChildren.next());
                    }
                    while (splitChildren.hasNext()) {
                        split.add(new ArrayList<>());
                        split.getLast().add(splitChildren.next());
                    }
                }
            }

            return split.stream().map(input -> switch (input.size()) {
                case 0 -> Component.empty();
                case 1 -> input.get(0);
                default -> Component.join(noSeparators(), input);
            }).collect(Collectors.toList());
        }

        return Collections.singleton(message);
    }
}
