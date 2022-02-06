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
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelPrototype;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.features.GlobalChatFeature;
import net.silthus.schat.message.MessagePrototype;
import net.silthus.schat.platform.chatter.AbstractChatterFactory;
import net.silthus.schat.platform.commands.ChannelCommands;
import net.silthus.schat.platform.commands.Commands;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.ui.view.ViewFactory;
import net.silthus.schat.ui.view.ViewProvider;
import net.silthus.schat.ui.views.Views;
import net.silthus.schat.util.gson.GsonProvider;
import net.silthus.schat.util.gson.types.ChannelSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterProvider.createCachingChatterProvider;
import static net.silthus.schat.message.SendMessage.sendMessageUseCase;
import static net.silthus.schat.platform.commands.parser.ChannelArgument.registerChannelArgument;
import static net.silthus.schat.platform.commands.parser.ChatterArgument.registerChatterArgument;
import static net.silthus.schat.platform.config.ConfigKeys.CHANNELS;
import static net.silthus.schat.ui.view.ViewProvider.cachingViewProvider;
import static net.silthus.schat.util.gson.types.ChannelSerializer.CHANNEL_TYPE;

@Getter
@Accessors(fluent = true)
public abstract class AbstractSChatServerPlugin extends AbstractSChatPlugin {

    private ChatterProvider chatterProvider;
    private ChannelRepository channelRepository;

    private Commands commands;

    private ViewFactory viewFactory;
    private ViewProvider viewProvider;

    private final List<Object> features = new ArrayList<>();

    protected void onLoad() {
    }

    @Override
    protected void onEnable() {
        viewFactory = createViewFactory();
        viewProvider = createViewProvider(viewFactory);

        chatterProvider = createCachingChatterProvider(createChatterFactory(viewProvider));
        channelRepository = createChannelRepository();

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

    private void registerSerializers() {
        GsonProvider.registerTypeAdapter(CHANNEL_TYPE, new ChannelSerializer(channelRepository()));
    }

    private void setupPrototypes() {
        MessagePrototype.configure(
            sendMessageUseCase()
                .eventBus(eventBus())
                .channelRepository(channelRepository())
                .create()
        );
        ChannelPrototype.configure(eventBus());
    }

    private void loadFeatures() {
        final GlobalChatFeature feature = new GlobalChatFeature(messenger(), serializer());
        feature.bind(eventBus());
        features.add(feature);
    }

    private void loadChannels() {
        logger().info("Loading channels...");
        for (final Channel channel : this.config().get(CHANNELS)) {
            channelRepository().add(channel);
        }
        logger().info("... loaded " + channelRepository.keys().size() + " channels.");
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
        registerChatterArgument(commandManager, chatterProvider());
        registerChannelArgument(commandManager, channelRepository());
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
