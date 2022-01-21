package net.silthus.schat.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import lombok.Getter;
import net.silthus.schat.platform.plugin.bootstrap.Bootstrap;
import net.silthus.schat.platform.plugin.logging.PluginLogger;
import net.silthus.schat.velocity.adapter.Slf4jPluginLogger;
import net.silthus.schat.velocity.adapter.VelocitySchedulerAdapter;
import org.slf4j.Logger;

@Plugin(id = "schat",
        name = "sChat",
        version = "1.0.0",
        url = "https://github.com/sVoxelDev/sChat",
        description = "Supercharge your Minecraft Chat Experience!",
        authors = {"Silthus"},
        dependencies = {@Dependency(id = "Protocolize")}
)

@Getter
public final class VelocityBootstrap implements Bootstrap {

    private final PluginLogger pluginLogger;
    private final VelocitySchedulerAdapter scheduler;
    private final VelocityPlugin plugin;

    @Inject
    private ProxyServer proxy;
    @Inject
    private PluginContainer pluginContainer;

    @Inject
    @DataDirectory
    private Path dataDirectory;

    @Inject
    public VelocityBootstrap(Logger logger) {
        this.pluginLogger = new Slf4jPluginLogger(logger);
        this.scheduler = new VelocitySchedulerAdapter(this);

        this.plugin = new VelocityPlugin(this);
    }

    @Override
    public Path getDataDirectory() {
        return this.dataDirectory.toAbsolutePath();
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onEnable(ProxyInitializeEvent e) {
        this.plugin.load();
        this.plugin.enable();
    }

    @Subscribe(order = PostOrder.LAST)
    public void onDisable(ProxyShutdownEvent e) {
        this.plugin.disable();
    }
}
