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
import net.silthus.chat.listeners.ChatPacketListener;
import net.silthus.chat.listeners.PlayerListener;
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

    private PaperCommandManager commandManager;
    private ChatManager chatManager;
    private ProtocolManager protocolManager;
    private BukkitAudiences audiences;

    private ChatPacketListener chatPacketListener;
    private PlayerListener playerListener;

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

        setupListeners();
    }

    private void setupAndLoadChannels() {
        chatManager = new ChatManager(this);
        chatManager.load(new PluginConfig(getConfig()));
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

    private void setupListeners() {
        playerListener = new PlayerListener(this);
        getServer().getPluginManager().registerEvents(playerListener, this);
    }
}
