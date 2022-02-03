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
import lombok.Getter;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelPrototype;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.features.GlobalChatFeature;
import net.silthus.schat.message.MessagePrototype;
import net.silthus.schat.platform.chatter.AbstractChatterFactory;
import net.silthus.schat.platform.commands.ChannelCommands;
import net.silthus.schat.platform.commands.Commands;
import net.silthus.schat.platform.listener.ChatListener;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.ui.view.ViewFactory;
import net.silthus.schat.ui.view.ViewProvider;
import net.silthus.schat.ui.views.Views;
import net.silthus.schat.usecases.OnChat;
import net.silthus.schat.util.gson.GsonProvider;
import net.silthus.schat.util.gson.types.ChannelSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterProvider.createCachingChatterProvider;
import static net.silthus.schat.platform.commands.parser.ChannelArgument.registerChannelArgument;
import static net.silthus.schat.platform.commands.parser.ChatterArgument.registerChatterArgument;
import static net.silthus.schat.platform.config.ConfigKeys.CHANNELS;
import static net.silthus.schat.ui.view.ViewProvider.cachingViewProvider;
import static net.silthus.schat.util.gson.types.ChannelSerializer.CHANNEL_TYPE;

@Getter
public abstract class AbstractSChatServerPlugin extends AbstractSChatPlugin {

    private ChatterProvider chatterProvider;
    private ChannelRepository channelRepository;

    private OnChat chatListener;
    private Commands commands;

    private ViewFactory viewFactory;
    private ViewProvider viewProvider;

    protected void onLoad() {
    }

    @Override
    protected void onEnable() {
        viewFactory = createViewFactory();
        viewProvider = createViewProvider(viewFactory);

        chatterProvider = createCachingChatterProvider(createChatterFactory(viewProvider));
        channelRepository = createChannelRepository();

        chatListener = createChatListener(chatterProvider);

        registerSerializers();
        setupPrototypes();
        loadFeatures();

        loadChannels();

        commands = createCommands();

        registerListeners();
    }

    @Override
    protected void onDisable() {
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

}
