package net.silthus.chat;

import co.aikar.commands.PaperCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.silthus.chat.commands.ChannelCommands;
import net.silthus.chat.config.PluginConfig;
import net.silthus.chat.protocollib.ChatPacketListener;
import org.bukkit.Bukkit;
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
    private static boolean testing = false;
    @Getter
    private PaperCommandManager commandManager;
    @Getter
    private ChannelManager channelManager;
    @Getter
    private ProtocolManager protocolManager;
    @Getter
    private BukkitAudiences audiences;

    public SChat() {
        instance = this;
    }

    public SChat(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        instance = this;
        testing = true;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        setupAndLoadChannels();
        audiences = BukkitAudiences.create(this);

        if (!isTesting()) {
            setupProtocolLib();
            setupCommands();
        }
    }

    private void setupAndLoadChannels() {
        channelManager = new ChannelManager(this);
        channelManager.load(new PluginConfig(getConfig()));
        getServer().getPluginManager().registerEvents(channelManager, this);
    }

    private void setupProtocolLib() {
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            protocolManager = ProtocolLibrary.getProtocolManager();
            protocolManager.addPacketListener(new ChatPacketListener(this));
            getLogger().info("Enabled ProtocolLib handler.");
        }
    }

    private void setupCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new ChannelCommands(this));
    }
}
