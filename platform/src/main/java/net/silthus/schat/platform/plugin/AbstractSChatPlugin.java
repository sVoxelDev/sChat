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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Getter;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelInteractorImpl;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.message.Messenger;
import net.silthus.schat.platform.commands.ChannelCommands;
import net.silthus.schat.platform.commands.Commands;
import net.silthus.schat.platform.config.SChatConfig;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.listener.ChatListener;
import net.silthus.schat.platform.locale.Messages;
import net.silthus.schat.platform.locale.Presenter;
import net.silthus.schat.platform.locale.TranslationManager;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.policies.Policies;
import net.silthus.schat.policies.PoliciesImpl;
import net.silthus.schat.usecases.OnChat;
import net.silthus.schat.view.ViewFactory;
import net.silthus.schat.view.ViewProvider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterProvider.createChatterProvider;
import static net.silthus.schat.platform.commands.parser.ChannelArgument.registerChannelArgument;
import static net.silthus.schat.platform.commands.parser.ChatterArgument.registerChatterArgument;
import static net.silthus.schat.platform.config.ConfigKeys.CHANNELS;
import static net.silthus.schat.platform.locale.Presenter.defaultPresenter;
import static net.silthus.schat.ui.model.ChatterViewModel.of;
import static net.silthus.schat.ui.view.Views.tabbedChannels;
import static net.silthus.schat.view.ViewProvider.simpleViewProvider;

@Getter
public abstract class AbstractSChatPlugin implements SChatPlugin {

    private TranslationManager translationManager;

    private SChatConfig config;
    private Messenger messenger;
    private Presenter presenter;
    private Policies policies;
    private ChatterProvider chatterProvider;
    private ChannelRepository channelRepository;
    private ChannelInteractorImpl channelInteractor;
    private OnChat chatListener;
    private Commands commands;

    private ViewFactory viewFactory;
    private ViewProvider viewProvider;

    @Override
    public final void load() {
        translationManager = new TranslationManager(getBootstrap().getConfigDirectory());
        translationManager.reload();
    }

    @Override
    public final void enable() {
        setupSenderFactory();

        Messages.STARTUP_BANNER.send(getConsole(), getBootstrap());

        getLogger().info("Loading configuration...");
        config = new SChatConfig(provideConfigurationAdapter());
        config.load();

        messenger = provideMessenger();
        presenter = providePresenter();

        viewFactory = provideViewFactory();
        viewProvider = provideViewProvider(getViewFactory());

        policies = provideChannelPolicies();
        chatterProvider = createChatterProvider(provideChatterFactory(viewProvider));
        channelRepository = provideChannelRepository();

        channelInteractor = new ChannelInteractorImpl().channelRepository(channelRepository).chatterProvider(chatterProvider).canJoinChannel(policies);

        chatListener = provideChatListener().chatterProvider(getChatterProvider()).messenger(getMessenger());

        getLogger().info("Loading channels...");
        for (final Channel channel : getConfig().get(CHANNELS)) {
            getChannelRepository().add(channel);
        }
        getLogger().info("... loaded " + channelRepository.keys().size() + " channels.");

        commands = createCommands();

        registerListeners();
    }

    @Override
    public final void disable() {

    }

    public abstract Sender getConsole();

    protected abstract ConfigurationAdapter provideConfigurationAdapter();

    protected abstract void setupSenderFactory();

    @ApiStatus.OverrideOnly
    protected Messenger provideMessenger() {
        return Messenger.defaultMessenger();
    }

    @ApiStatus.OverrideOnly
    protected Presenter providePresenter() {
        return defaultPresenter();
    }

    @ApiStatus.OverrideOnly
    protected ViewFactory provideViewFactory() {
        return chatter -> tabbedChannels(of(chatter));
    }

    @ApiStatus.OverrideOnly
    protected ViewProvider provideViewProvider(ViewFactory viewFactory) {
        return simpleViewProvider(viewFactory);
    }

    protected abstract ChatterFactory provideChatterFactory(final ViewProvider viewProvider);

    @ApiStatus.OverrideOnly
    protected ChannelRepository provideChannelRepository() {
        return createInMemoryChannelRepository();
    }

    @ApiStatus.OverrideOnly
    protected Policies provideChannelPolicies() {
        return new PoliciesImpl();
    }

    protected abstract ChatListener provideChatListener();

    @NotNull
    private Commands createCommands() {
        final CommandManager<Sender> commandManager = provideCommandManager();
        final Commands commands = new Commands(commandManager);

        registerCommandArguments(commandManager);
        registerCommands(commands);

        return commands;
    }

    private void registerCommandArguments(CommandManager<Sender> commandManager) {
        registerChatterArgument(commandManager, getChatterProvider());
        registerChannelArgument(commandManager, getChannelRepository(), getPolicies());
    }

    protected abstract CommandManager<Sender> provideCommandManager();

    private void registerCommands(Commands commands) {
        registerNativeCommands(commands);
        registerCustomCommands(commands);
    }

    private void registerNativeCommands(Commands commands) {
        commands.register(new ChannelCommands(channelInteractor));
    }

    @ApiStatus.OverrideOnly
    protected void registerCustomCommands(Commands commands) {
    }

    @ApiStatus.OverrideOnly
    protected void registerListeners() {
    }

    protected final Path resolveConfig(String fileName) {
        Path configFile = getBootstrap().getConfigDirectory().resolve(fileName);

        if (!Files.exists(configFile)) {
            createConfigDirectory(configFile);
            copyDefaultConfig(fileName, configFile);
        }

        return configFile;
    }

    private void copyDefaultConfig(String fileName, Path configFile) {
        try (InputStream is = getBootstrap().getResourceStream(fileName)) {
            Files.copy(is, configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createConfigDirectory(Path configFile) {
        try {
            Files.createDirectories(configFile.getParent());
        } catch (IOException ignored) {
        }
    }
}
