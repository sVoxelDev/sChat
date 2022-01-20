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

package net.silthus.schat.platform.commands.parser;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationAccessor;
import cloud.commandframework.annotations.injection.ParameterInjector;
import cloud.commandframework.context.CommandContext;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.platform.sender.Sender;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ChatterArgument implements ParameterInjector<Sender, Chatter> {

    public static void registerChatterArgument(CommandManager<Sender> commandManager, ChatterProvider repository) {
        final ChatterArgument parser = new ChatterArgument(repository);
        commandManager.parameterInjectorRegistry().registerInjector(Chatter.class, parser);
    }

    private final ChatterProvider chatterProvider;

    public ChatterArgument(ChatterProvider chatterProvider) {
        this.chatterProvider = chatterProvider;
    }

    @Override
    public Chatter create(@NonNull CommandContext<Sender> context, @NonNull AnnotationAccessor annotationAccessor) {
        return chatterProvider.get(context.getSender().getUniqueId());
    }
}
