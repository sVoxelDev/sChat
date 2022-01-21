package net.silthus.schat.velocity;

import cloud.commandframework.CommandManager;
import cloud.commandframework.velocity.VelocityCommandManager;
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

import static cloud.commandframework.execution.CommandExecutionCoordinator.simpleCoordinator;

@Getter
public final class VelocityPlugin extends AbstractSChatPlugin {

    private final VelocityBootstrap bootstrap;
    private VelocitySenderFactory senderFactory;

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
}
