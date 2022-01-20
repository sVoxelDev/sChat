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

package net.silthus.schat.bukkit;

import cloud.commandframework.CommandManager;
import cloud.commandframework.paper.PaperCommandManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.silthus.schat.bukkit.adapter.BukkitSchedulerAdapter;
import net.silthus.schat.bukkit.adapter.BukkitSenderFactory;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapters;
import net.silthus.schat.platform.plugin.AbstractSChatPlugin;
import net.silthus.schat.platform.plugin.adapter.Presenter;
import net.silthus.schat.platform.sender.Sender;
import org.bukkit.Bukkit;

import static cloud.commandframework.execution.CommandExecutionCoordinator.simpleCoordinator;

@Getter
public final class SChatBukkitPlugin extends AbstractSChatPlugin {

    private final SChatBukkitBootstrap bootstrap;
    private BukkitSenderFactory chatterFactory;

    SChatBukkitPlugin(SChatBukkitBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    protected ConfigurationAdapter provideConfigurationAdapter() {
        return ConfigurationAdapters.YAML.create(resolveConfig("config.yml").toFile());
    }

    @Override
    protected void setupChatterFactory() {
        chatterFactory = new BukkitSenderFactory(BukkitAudiences.create(getBootstrap().getLoader()), new BukkitSchedulerAdapter(bootstrap.getLoader()));
    }

    @Override
    protected Presenter providePresenter() {
        return null;
    }

    @Override
    @SneakyThrows
    protected CommandManager<Sender> provideCommandManager() {
        try {
            return new PaperCommandManager<>(
                getBootstrap().getLoader(),
                simpleCoordinator(),
                commandSender -> getChatterFactory().wrap(commandSender),
                sender -> getChatterFactory().unwrap(sender)
            );
        } catch (Exception e) {
            getLogger().severe("Failed to initialize the command manager.");
            Bukkit.getPluginManager().disablePlugin(getBootstrap().getLoader());
            throw new RuntimeException(e);
        }
    }
}
