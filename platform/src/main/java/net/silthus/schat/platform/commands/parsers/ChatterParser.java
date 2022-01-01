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

package net.silthus.schat.platform.commands.parsers;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationAccessor;
import cloud.commandframework.annotations.injection.ParameterInjector;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import io.leangen.geantyref.TypeToken;
import java.util.Queue;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.sender.Sender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ChatterParser implements ArgumentParser<Sender, Chatter>, ParameterInjector<Sender, Chatter> {

    public static void register(CommandManager<Sender> commandManager, ChatterRepository repository) {
        final ChatterParser parser = new ChatterParser(repository);
        commandManager.getParserRegistry().registerParserSupplier(TypeToken.get(Chatter.class), parserParameters -> parser);
        commandManager.parameterInjectorRegistry().registerInjector(Chatter.class, parser);
    }

    private final ChatterRepository repository;

    public ChatterParser(ChatterRepository repository) {
        this.repository = repository;
    }

    @Override
    public @Nullable Chatter create(@NonNull CommandContext<Sender> context, @NonNull AnnotationAccessor annotationAccessor) {
        return repository.get(context.getSender().getUniqueId());
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Chatter> parse(@NonNull CommandContext<@NonNull Sender> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
        return null;
    }
}
