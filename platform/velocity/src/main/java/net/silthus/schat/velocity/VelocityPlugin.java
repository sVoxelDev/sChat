package net.silthus.schat.velocity;

import cloud.commandframework.CommandManager;
import cloud.commandframework.velocity.VelocityCommandManager;
import dev.simplix.protocolize.api.Protocolize;
import java.io.File;
import lombok.Getter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapters;
import net.silthus.schat.platform.listener.ChatListener;
import net.silthus.schat.platform.plugin.AbstractSChatPlugin;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.velocity.adapter.VelocityChatListener;
import net.silthus.schat.velocity.adapter.VelocityChatterFactory;
import net.silthus.schat.velocity.adapter.VelocitySenderFactory;
import net.silthus.schat.velocity.protocolize.ChatPacketListener;

import static cloud.commandframework.execution.CommandExecutionCoordinator.simpleCoordinator;

@Getter
public final class VelocityPlugin extends AbstractSChatPlugin {

    private final VelocityBootstrap bootstrap;
    private VelocitySenderFactory senderFactory;
    private ChatPacketListener chatPacketListener;

    public VelocityPlugin(VelocityBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    protected ConfigurationAdapter provideConfigurationAdapter() {
        return ConfigurationAdapters.YAML.create(new File(bootstrap.getConfigDirectory().toFile(), "config.yml"));
    }

    @Override
    protected void setupSenderFactory() {
        senderFactory = new VelocitySenderFactory(bootstrap.getProxy());
    }

    @Override
    protected ChatterFactory provideChatterFactory() {
        return new VelocityChatterFactory(getBootstrap().getProxy(), getViewProvider());
    }

    @Override
    protected ChatListener provideChatListener() {
        final VelocityChatListener listener = new VelocityChatListener();
        bootstrap.getProxy().getEventManager().register(bootstrap, listener);
        return listener;
    }

    @Override
    protected CommandManager<Sender> provideCommandManager() {
        return new VelocityCommandManager<>(
            bootstrap.getPluginContainer(),
            bootstrap.getProxy(),
            simpleCoordinator(),
            commandSource -> getSenderFactory().wrap(commandSource),
            sender -> getSenderFactory().unwrap(sender)
        );
    }

    @Override
    protected void registerListeners() {
        chatPacketListener = new ChatPacketListener(getChatterProvider(), getViewProvider(), getMessenger());
        Protocolize.listenerProvider().registerListener(chatPacketListener);
    }
}
