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

import lombok.Getter;
import lombok.Setter;
import net.silthus.schat.channel.ChannelPermissionProvider;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.channel.Channels;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.chatter.Chatters;
import net.silthus.schat.handler.types.UserJoinHandler;
import net.silthus.schat.platform.config.SChatConfig;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.user.UserRepository;
import net.silthus.schat.user.Users;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static net.silthus.schat.user.UserRepository.createInMemoryUserRepository;

public abstract class AbstractPlugin implements SChatPlugin {

    @Getter
    private SChatConfig config;
    @Getter
    private Channels channels;
    @Getter
    private Users users;
    @Getter
    private Chatters chatters;
    @Getter
    @Setter
    private ChannelPermissionProvider channelPermissions = ChannelPermissionProvider.DEFAULT;

    @Override
    public final void enable() {
        config = provideConfiguration(provideConfigurationAdapter());
        config.load();

        channels = provideChannelManager(provideChannelRepository());
        channels.load();

        chatters = provideChatterManager(provideChatterRepository());
        users = provideUserManager(provideUserRepository());

        registerListeners();
    }

    @ApiStatus.OverrideOnly
    protected @NotNull SChatConfig provideConfiguration(ConfigurationAdapter adapter) {
        return new SChatConfig(adapter);
    }

    @ApiStatus.OverrideOnly
    protected @NotNull ChannelRepository provideChannelRepository() {
        return createInMemoryChannelRepository();
    }

    @ApiStatus.OverrideOnly
    protected @NotNull ChannelManager provideChannelManager(ChannelRepository repository) {
        return new ChannelManager(this, repository);
    }

    @ApiStatus.OverrideOnly
    protected abstract @NotNull ConfigurationAdapter provideConfigurationAdapter();

    @ApiStatus.OverrideOnly
    protected @NotNull UserRepository provideUserRepository() {
        return createInMemoryUserRepository();
    }

    @ApiStatus.OverrideOnly
    protected @NotNull ChatterRepository provideChatterRepository() {
        return createInMemoryChatterRepository();
    }

    @ApiStatus.OverrideOnly
    protected @NotNull ChatterManager provideChatterManager(ChatterRepository repository) {
        return new ChatterManager(repository);
    }

    @ApiStatus.OverrideOnly
    protected @NotNull UserManager provideUserManager(UserRepository repository) {
        return new UserManager(repository, provideUserJoinHandler());
    }

    @ApiStatus.OverrideOnly
    protected @NotNull UserJoinHandler.Default provideUserJoinHandler() {
        return UserJoinHandler.createUserJoinHandler(getChatters(), channels, getChannelPermissions());
    }

    protected abstract void registerListeners();
}
