package net.silthus.chat;

import co.aikar.commands.PaperCommandManager;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.silthus.chat.commands.ChannelCommands;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

@PluginMain
public class SChat extends JavaPlugin {

    @Getter
    @Accessors(fluent = true)
    private static SChat instance;
    @Getter
    private PaperCommandManager commandManager;
    @Getter
    private ChannelManager channelManager;

    public SChat() {
        instance = this;
    }

    public SChat(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        instance = this;
    }

    @Override
    public void onEnable() {

        saveDefaultConfig();

        this.channelManager = new ChannelManager(this);
        getServer().getPluginManager().registerEvents(channelManager, this);

        this.commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new ChannelCommands());
    }
}
