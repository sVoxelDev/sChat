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
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelPrototype;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.features.GlobalChatFeature;
import net.silthus.schat.message.MessagePrototype;
import net.silthus.schat.messaging.MessengerGatewayProvider;
import net.silthus.schat.platform.chatter.AbstractChatterFactory;
import net.silthus.schat.platform.commands.ChannelCommands;
import net.silthus.schat.platform.commands.Commands;
import net.silthus.schat.platform.config.ConfigKeys;
import net.silthus.schat.platform.config.SChatConfig;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.listener.ChatListener;
import net.silthus.schat.platform.locale.TranslationManager;
import net.silthus.schat.platform.messaging.GatewayProviderRegistry;
import net.silthus.schat.platform.messaging.MessagingService;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.ui.view.ViewFactory;
import net.silthus.schat.ui.view.ViewProvider;
import net.silthus.schat.ui.views.Views;
import net.silthus.schat.usecases.OnChat;
import net.silthus.schat.util.gson.GsonProvider;
import net.silthus.schat.util.gson.GsonSerializer;
import net.silthus.schat.util.gson.types.ChannelSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterProvider.createCachingChatterProvider;
import static net.silthus.schat.platform.commands.parser.ChannelArgument.registerChannelArgument;
import static net.silthus.schat.platform.commands.parser.ChatterArgument.registerChatterArgument;
import static net.silthus.schat.platform.config.ConfigKeys.CHANNELS;
import static net.silthus.schat.platform.locale.Messages.STARTUP_BANNER;
import static net.silthus.schat.ui.view.ViewProvider.cachingViewProvider;
import static net.silthus.schat.util.gson.GsonProvider.gsonSerializer;
import static net.silthus.schat.util.gson.types.ChannelSerializer.CHANNEL_TYPE;

@Getter
public abstract class AbstractSChatPlugin implements SChatPlugin {

    private TranslationManager translationManager;
    private EventBus eventBus;
    private MessengerGatewayProvider.Registry gatewayProviderRegistry;
    private MessagingService messenger;

    private SChatConfig config;
    private GsonSerializer serializer;

    private ChatterProvider chatterProvider;
    private ChannelRepository channelRepository;

    private OnChat chatListener;
    private Commands commands;

    private ViewFactory viewFactory;
    private ViewProvider viewProvider;

    @Override
    public final void load() {
        translationManager = new TranslationManager(getBootstrap().getConfigDirectory());
        translationManager.reload();

        eventBus = createEventBus();

        serializer = gsonSerializer();
        gatewayProviderRegistry = new GatewayProviderRegistry();

        onLoad();
    }

    @ApiStatus.OverrideOnly
    protected void onLoad() {
    }

    @Override
    public final void enable() {
        setupSenderFactory();

        STARTUP_BANNER.send(getConsole(), getBootstrap());

        config = loadConfiguration();

        viewFactory = createViewFactory();
        viewProvider = createViewProvider(viewFactory);

        chatterProvider = createCachingChatterProvider(createChatterFactory(viewProvider));
        channelRepository = createChannelRepository();

        messenger = createMessagingService();
        chatListener = createChatListener(chatterProvider);

        registerSerializers();
        setupPrototypes();
        loadFeatures();

        loadChannels();

        commands = createCommands();

        registerListeners();

        onEnable();
    }

    @Override
    public final void disable() {
        eventBus.close();
        messenger.close();

        onDisable();
    }

    @ApiStatus.OverrideOnly
    protected void onDisable() {
    }

    private @NotNull SChatConfig loadConfiguration() {
        getLogger().info("Loading configuration...");
        SChatConfig config = new SChatConfig(createConfigurationAdapter(), getEventBus());
        config.load();
        return config;
    }

    public abstract Sender getConsole();

    protected abstract ConfigurationAdapter createConfigurationAdapter();

    protected abstract EventBus createEventBus();

    protected abstract void setupSenderFactory();

    @ApiStatus.OverrideOnly
    protected MessagingService createMessagingService() {
        return new MessagingService(getGatewayProviderRegistry().get(config.get(ConfigKeys.MESSENGER)), getSerializer());
    }

    @ApiStatus.OverrideOnly
    protected ViewFactory createViewFactory() {
        return Views::tabbedChannels;
    }

    @ApiStatus.OverrideOnly
    protected ViewProvider createViewProvider(ViewFactory viewFactory) {
        return cachingViewProvider(viewFactory);
    }

    protected abstract AbstractChatterFactory createChatterFactory(final ViewProvider viewProvider);

    @ApiStatus.OverrideOnly
    protected ChannelRepository createChannelRepository() {
        return createInMemoryChannelRepository();
    }

    protected abstract ChatListener createChatListener(ChatterProvider provider);

    private void registerSerializers() {
        GsonProvider.registerTypeAdapter(CHANNEL_TYPE, new ChannelSerializer(getChannelRepository()));
    }

    private void loadFeatures() {
        new GlobalChatFeature(getMessenger(), getSerializer()).bind(getEventBus());
    }

    private void setupPrototypes() {
        MessagePrototype.configure(getEventBus());
        ChannelPrototype.configure(getEventBus());
    }

    private void loadChannels() {
        getLogger().info("Loading channels...");
        for (final Channel channel : getConfig().get(CHANNELS)) {
            getChannelRepository().add(channel);
        }
        getLogger().info("... loaded " + channelRepository.keys().size() + " channels.");
    }

    @ApiStatus.OverrideOnly
    protected void onEnable() {
    }

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
        registerChannelArgument(commandManager, getChannelRepository());
    }

    protected abstract CommandManager<Sender> provideCommandManager();

    private void registerCommands(Commands commands) {
        registerNativeCommands(commands);
        registerCustomCommands(commands);
    }

    private void registerNativeCommands(Commands commands) {
        commands.register(new ChannelCommands());
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
