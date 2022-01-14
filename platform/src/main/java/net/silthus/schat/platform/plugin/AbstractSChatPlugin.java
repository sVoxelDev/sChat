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

package net.silthus.schat.platform.plugin;

import cloud.commandframework.CommandManager;
import lombok.Getter;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.platform.commands.ChannelCommands;
import net.silthus.schat.platform.commands.Commands;
import net.silthus.schat.policies.Policies;
import net.silthus.schat.policies.PoliciesImpl;
import org.jetbrains.annotations.ApiStatus;

import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;

@Getter
public abstract class AbstractSChatPlugin implements SChatPlugin {

    private Policies policies;
    private ChannelRepository channelRepository;
    private Commands commands;

    public final void enable() {
        setupChatterFactory();

        policies = provideChannelPolicies();
        channelRepository = provideChannelRepository();

        commands = new Commands(provideCommandManager());
        registerNativeCommands();
        registerCustomCommands(commands);
    }

    protected abstract void setupChatterFactory();

    @ApiStatus.OverrideOnly
    protected ChannelRepository provideChannelRepository() {
        return createInMemoryChannelRepository();
    }

    @ApiStatus.OverrideOnly
    protected Policies provideChannelPolicies() {
        return new PoliciesImpl();
    }

    protected abstract CommandManager<Chatter> provideCommandManager();

    private void registerNativeCommands() {
        commands.register(new ChannelCommands(policies, channelRepository));
    }

    @ApiStatus.OverrideOnly
    protected void registerCustomCommands(Commands commands) {
    }
}
