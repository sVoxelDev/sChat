/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.platform.plugin;

import cloud.commandframework.CommandManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Getter;
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
import net.silthus.schat.platform.locale.Presenter;
import net.silthus.schat.platform.locale.TranslationManager;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.policies.Policies;
import net.silthus.schat.policies.PoliciesImpl;
import net.silthus.schat.ui.ViewProvider;
import net.silthus.schat.usecases.OnChat;
import org.jetbrains.annotations.ApiStatus;

import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterProvider.createChatterProvider;
import static net.silthus.schat.platform.locale.Presenter.defaultPresenter;
import static net.silthus.schat.ui.ViewProvider.simpleViewProvider;

@Getter
public abstract class AbstractSChatPlugin implements SChatPlugin {

    private TranslationManager translationManager;

    private SChatConfig config;
    private Messenger messenger;
    private Presenter presenter;
    private ViewProvider viewProvider;
    private Policies policies;
    private ChatterProvider chatterProvider;
    private ChannelRepository channelRepository;
    private ChannelInteractorImpl channelInteractor;
    private OnChat chatListener;
    private Commands commands;

    @Override
    public final void load() {
        translationManager = new TranslationManager(getBootstrap().getConfigDirectory());
        translationManager.reload();
    }

    @Override
    public final void enable() {
        setupSenderFactory();

        config = new SChatConfig(provideConfigurationAdapter());
        config.load();

        messenger = provideMessenger();
        presenter = providePresenter();
        viewProvider = provideViewProvider();

        policies = provideChannelPolicies();
        chatterProvider = createChatterProvider(provideChatterFactory());
        channelRepository = provideChannelRepository();

        channelInteractor = new ChannelInteractorImpl()
            .channelRepository(channelRepository)
            .chatterProvider(chatterProvider)
            .canJoinChannel(policies);

        chatListener = provideChatListener()
            .chatterProvider(getChatterProvider())
            .messenger(getMessenger());

        commands = new Commands(provideCommandManager(), new Commands.Context(chatterProvider, channelRepository));
        registerCommands();

        registerListeners();
    }

    @Override
    public final void disable() {

    }

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
    protected ViewProvider provideViewProvider() {
        return simpleViewProvider();
    }

    protected abstract ChatterFactory provideChatterFactory();

    @ApiStatus.OverrideOnly
    protected ChannelRepository provideChannelRepository() {
        return createInMemoryChannelRepository();
    }

    @ApiStatus.OverrideOnly
    protected Policies provideChannelPolicies() {
        return new PoliciesImpl();
    }

    protected abstract ChatListener provideChatListener();

    protected abstract CommandManager<Sender> provideCommandManager();

    private void registerCommands() {
        registerNativeCommands();
        registerCustomCommands(commands);
    }

    private void registerNativeCommands() {
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
