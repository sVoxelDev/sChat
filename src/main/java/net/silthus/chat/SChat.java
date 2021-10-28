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

@Getter
@PluginMain
public class SChat extends JavaPlugin {

    @Getter
    @Accessors(fluent = true)
    private static SChat instance;
    @Getter
    private static boolean testing = false;

    private ChannelRegistry channelRegistry;
    private ChatManager chatManager;

    private PaperCommandManager commandManager;
    private ProtocolManager protocolManager;
    private BukkitAudiences audiences;

    private ChatPacketListener chatPacketListener;

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
        setupChatManager();

        setupAdventureTextLibrary();
        setupProtocolLib();
        setupCommands();
    }

    @Override
    public void onDisable() {

        commandManager.unregisterCommands();
    }

    private void setupAndLoadChannels() {
        channelRegistry = new ChannelRegistry(this);
        channelRegistry.load(new PluginConfig(getConfig()));
    }

    private void setupChatManager() {
        chatManager = new ChatManager(this);
    }

    private void setupAdventureTextLibrary() {
        audiences = BukkitAudiences.create(this);
    }

    private void setupProtocolLib() {
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            protocolManager = ProtocolLibrary.getProtocolManager();
            chatPacketListener = new ChatPacketListener(this);
            protocolManager.addPacketListener(chatPacketListener);
            getLogger().info("Enabled ProtocolLib handler.");
        }
    }

    private void setupCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new ChannelCommands(this));
    }
}
