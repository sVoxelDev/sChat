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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.silthus.schat.GsonPluginMessageSerializer;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.platform.config.SChatConfig;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.locale.TranslationManager;
import net.silthus.schat.platform.messaging.GatewayProviderRegistry;
import net.silthus.schat.platform.messaging.MessagingService;
import net.silthus.schat.platform.sender.Sender;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.PluginMessageSerializer.gsonSerializer;
import static net.silthus.schat.platform.config.ConfigKeys.DEBUG;
import static net.silthus.schat.platform.locale.Messages.STARTUP_BANNER;
import static net.silthus.schat.platform.messaging.MessagingService.createMessagingService;

@Getter
@Accessors(fluent = true)
public abstract class AbstractSChatPlugin implements SChatPlugin {

    private TranslationManager translationManager;
    private EventBus eventBus;
    private GatewayProviderRegistry gatewayProviderRegistry;
    private GsonPluginMessageSerializer serializer;

    private SChatConfig config;
    private MessagingService messenger;

    @Override
    public final void load() {
        translationManager = new TranslationManager(bootstrap().configDirectory());
        translationManager.reload();

        serializer = gsonSerializer();
        gatewayProviderRegistry = new GatewayProviderRegistry();

        onLoad();
    }

    protected abstract void onLoad();

    @Override
    public final void enable() {
        setupSenderFactory();

        STARTUP_BANNER.send(getConsole(), bootstrap());

        config = loadConfiguration();

        eventBus = createEventBus();

        registerMessengerGateway(gatewayProviderRegistry());
        messenger = createMessagingService(gatewayProviderRegistry(), serializer(), config());

        onEnable();
    }

    protected abstract void onEnable();

    @Override
    public final void disable() {
        eventBus().close();
        messenger().close();

        onDisable();
    }

    protected abstract void onDisable();

    protected EventBus createEventBus() {
        return EventBus.eventBus(config().get(DEBUG));
    }

    public abstract Sender getConsole();

    protected abstract void setupSenderFactory();

    private @NotNull SChatConfig loadConfiguration() {
        logger().info("Loading configuration...");
        SChatConfig config = new SChatConfig(createConfigurationAdapter(), eventBus());
        config.load();
        return config;
    }

    protected abstract ConfigurationAdapter createConfigurationAdapter();

    protected abstract void registerMessengerGateway(GatewayProviderRegistry registry);

    protected final Path resolveConfig(String fileName) {
        Path configFile = bootstrap().configDirectory().resolve(fileName);

        if (!Files.exists(configFile)) {
            createConfigDirectory(configFile);
            copyDefaultConfig(fileName, configFile);
        }

        return configFile;
    }

    private void copyDefaultConfig(String fileName, Path configFile) {
        try (InputStream is = bootstrap().resourceAsStream(fileName)) {
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
