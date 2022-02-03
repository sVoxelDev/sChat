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

import lombok.Getter;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.messaging.MessengerGatewayProvider;
import net.silthus.schat.platform.config.ConfigKeys;
import net.silthus.schat.platform.config.SChatConfig;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.locale.TranslationManager;
import net.silthus.schat.platform.messaging.GatewayProviderRegistry;
import net.silthus.schat.platform.messaging.MessagingService;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.util.gson.GsonSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.platform.locale.Messages.STARTUP_BANNER;
import static net.silthus.schat.util.gson.GsonProvider.gsonSerializer;

@Getter
public abstract class AbstractSChatPlugin implements SChatPlugin {

    private TranslationManager translationManager;
    private EventBus eventBus;
    private MessengerGatewayProvider.Registry gatewayProviderRegistry;
    private GsonSerializer serializer;

    private SChatConfig config;
    private MessagingService messenger;

    @Override
    public final void load() {
        translationManager = new TranslationManager(getBootstrap().getConfigDirectory());
        translationManager.reload();

        eventBus = createEventBus();

        serializer = gsonSerializer();
        gatewayProviderRegistry = new GatewayProviderRegistry();

        onLoad();
    }

    protected abstract void onLoad();

    @Override
    public final void enable() {
        setupSenderFactory();

        STARTUP_BANNER.send(getConsole(), getBootstrap());

        config = loadConfiguration();

        registerMessengerGateway(getGatewayProviderRegistry());
        messenger = createMessagingService();

        onEnable();
    }

    protected abstract void onEnable();

    @Override
    public final void disable() {
        getEventBus().close();
        getMessenger().close();

        onDisable();
    }

    protected abstract void onDisable();

    protected abstract EventBus createEventBus();

    public abstract Sender getConsole();

    protected abstract void setupSenderFactory();

    private @NotNull SChatConfig loadConfiguration() {
        getLogger().info("Loading configuration...");
        SChatConfig config = new SChatConfig(createConfigurationAdapter(), getEventBus());
        config.load();
        return config;
    }

    protected abstract ConfigurationAdapter createConfigurationAdapter();

    @ApiStatus.OverrideOnly
    protected MessagingService createMessagingService() {
        return new MessagingService(getGatewayProviderRegistry().get(getConfig().get(ConfigKeys.MESSENGER)), getSerializer());
    }

    protected abstract void registerMessengerGateway(MessengerGatewayProvider.Registry registry);
}
