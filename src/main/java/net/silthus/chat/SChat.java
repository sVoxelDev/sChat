/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.chat;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.base.Strings;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.milkbowl.vault.chat.Chat;
import net.silthus.chat.commands.SChatCommands;
import net.silthus.chat.config.PluginConfig;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.ChannelRegistry;
import net.silthus.chat.conversations.ConversationManager;
import net.silthus.chat.identities.AbstractIdentity;
import net.silthus.chat.identities.Chatter;
import net.silthus.chat.identities.ChatterManager;
import net.silthus.chat.identities.Console;
import net.silthus.chat.integrations.protocollib.ChatPacketQueue;
import net.silthus.chat.integrations.vault.VaultProvider;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@PluginMain
public final class SChat extends JavaPlugin {

    @Getter
    @Accessors(fluent = true)
    private static SChat instance;
    @Getter
    private static boolean testing = false;

    private PluginConfig pluginConfig;
    private Metrics metrics;

    private ChannelRegistry channelRegistry;
    private ChatterManager chatterManager;
    private ConversationManager conversationManager;

    private PaperCommandManager commandManager;
    private ProtocolManager protocolManager;
    @Setter(AccessLevel.PACKAGE)
    private VaultProvider vaultProvider;

    @Setter(AccessLevel.PACKAGE)
    private ChatPacketQueue chatPacketQueue;
    @Setter(AccessLevel.PACKAGE)
    private net.silthus.chat.integrations.bungeecord.BungeeCord bungeecord;

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
        if (!isTesting() && isNotPaperMC()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        setupAndLoadConfigs();

        setupBStats();

        setupAndLoadChannels();
        setupChatterManager();
        setupConversationManager();
        setupConsoleChatter();

        setupProtocolLib();
        setupVaultIntegration();
        setupBungeecordIntegration();
        setupCommands();
    }

    private boolean isNotPaperMC() {
        try {
            Class.forName("io.papermc.paper.event.player.AsyncChatEvent");
            return false;
        } catch (ClassNotFoundException e) {
            getLogger().severe("Server not running PaperMC, but it is required by this plugin. Disabling...");
            return true;
        }
    }

    @Override
    public void onDisable() {

        Console.instance = null;
        if (commandManager != null) commandManager.unregisterCommands();
        if (chatterManager != null) chatterManager.removeAllChatters();
        if (bungeecord != null) tearDownBungeecord();
        if (channelRegistry != null) channelRegistry.clear();

        HandlerList.unregisterAll(this);
    }

    private void setupAndLoadConfigs() {
        saveDefaultConfig();
        saveResource("config.default.yml", true);
        saveResource("lang_en.yaml", false);
        pluginConfig = PluginConfig.fromConfig(getConfig());
    }

    private void setupBStats() {
        if (isTesting()) return;
        metrics = new Metrics(this, Constants.BSTATS_ID);
    }

    private void setupAndLoadChannels() {
        channelRegistry = new ChannelRegistry(this);
        channelRegistry.load(getPluginConfig());
    }

    private void setupChatterManager() {
        chatterManager = new ChatterManager(this);
    }

    private void setupConversationManager() {
        conversationManager = new ConversationManager(this);
    }

    private void setupConsoleChatter() {
        getServer().getPluginManager().registerEvents(Console.init(getPluginConfig().console()), this);
    }

    private void setupProtocolLib() {
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) return;

        protocolManager = ProtocolLibrary.getProtocolManager();
        chatPacketQueue = new ChatPacketQueue(this);
        protocolManager.addPacketListener(chatPacketQueue);
        getLogger().info("Enabled ProtocolLib handler.");
    }

    private void setupVaultIntegration() {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            vaultProvider = new VaultProvider(Objects.requireNonNull(getServer().getServicesManager().getRegistration(Chat.class)).getProvider());
        } else {
            vaultProvider = new VaultProvider();
        }
    }

    private void setupBungeecordIntegration() {
        this.bungeecord = new net.silthus.chat.integrations.bungeecord.BungeeCord(this);
        if (!isTesting()) {
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, Constants.BungeeCord.BUNGEECORD_CHANNEL);
            this.getServer().getMessenger().registerIncomingPluginChannel(this, Constants.BungeeCord.BUNGEECORD_CHANNEL, bungeecord);
        }
    }

    private void tearDownBungeecord() {
        if (!isTesting()) {
            this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, Constants.BungeeCord.BUNGEECORD_CHANNEL);
            this.getServer().getMessenger().unregisterIncomingPluginChannel(this, Constants.BungeeCord.BUNGEECORD_CHANNEL, bungeecord);
        }
        this.bungeecord = null;
    }

    private void setupCommands() {
        commandManager = new PaperCommandManager(this);

        registerChatterContext(commandManager);
        registerChannelContext(commandManager);
        registerConversationContext(commandManager);

        registerChannelCompletion(commandManager);
        registerChatterCompletion(commandManager);

        loadCommandLocales(commandManager);

        commandManager.registerCommand(new SChatCommands(this));
    }

    private void registerChannelContext(PaperCommandManager commandManager) {
        commandManager.getCommandContexts().registerContext(Channel.class, context -> {
            String channelIdentifier = context.popFirstArg();
            return getChannelRegistry().find(channelIdentifier)
                    .orElseThrow(() -> new InvalidCommandArgument("The channel '" + channelIdentifier + "' does not exist."));
        });
    }

    private void registerChatterContext(PaperCommandManager commandManager) {
        commandManager.getCommandContexts().registerIssuerAwareContext(Chatter.class, context -> {
            if (context.hasFlag("self")) {
                return Chatter.of(context.getPlayer());
            }

            String arg = context.popFirstArg();
            Player player;
            if (isEntitySelector(arg)) {
                player = selectPlayer(context.getSender(), arg);
            } else {
                if (Strings.isNullOrEmpty(arg)) {
                    return Chatter.of(context.getPlayer());
                }
                try {
                    return getChatterManager().getChatter(UUID.fromString(arg));
                } catch (Exception e) {
                    Optional<Chatter> chatter = getChatterManager().getChatter(arg);
                    if (chatter.isPresent()) return chatter.get();
                    player = Bukkit.getPlayerExact(arg);
                }
            }

            if (player == null) {
                throw new InvalidCommandArgument("The player '" + arg + "' was not found.");
            }

            return Chatter.of(player);
        });
    }

    private void registerConversationContext(PaperCommandManager commandManager) {
        commandManager.getCommandContexts().registerContext(Conversation.class, context -> getConversationManager().getConversation(UUID.fromString(context.popFirstArg())));
    }

    private void registerChannelCompletion(PaperCommandManager commandManager) {
        commandManager.getCommandCompletions().registerAsyncCompletion("channels", context ->
                getChannelRegistry().getChannels().stream()
                        .map(Channel::getName)
                        .collect(Collectors.toSet()));
    }

    private void registerChatterCompletion(PaperCommandManager commandManager) {
        commandManager.getCommandCompletions().registerAsyncCompletion("chatters", context ->
                getChatterManager().getChatters().stream()
                        .map(AbstractIdentity::getName)
                        .collect(Collectors.toSet()));
    }

    private void loadCommandLocales(PaperCommandManager commandManager) {
        try {
            commandManager.getLocales().setDefaultLocale(Locale.ENGLISH);
            commandManager.getLocales().loadYamlLanguageFile("lang_en.yaml", Locale.ENGLISH);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe("Failed to load language config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isEntitySelector(String arg) {
        return !Strings.isNullOrEmpty(arg) && arg.startsWith("@");
    }

    private Player selectPlayer(CommandSender sender, String playerIdentifier) {

        List<Player> matchedPlayers;
        try {
            matchedPlayers = getServer().selectEntities(sender, playerIdentifier).parallelStream()
                    .unordered()
                    .filter(e -> e instanceof Player)
                    .map(e -> ((Player) e))
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new InvalidCommandArgument(String.format("Error parsing selector '%s' for %s! See console for more details",
                    playerIdentifier, sender.getName()));
        }
        if (matchedPlayers.isEmpty()) {
            throw new InvalidCommandArgument(String.format("No player found with selector '%s' for %s",
                    playerIdentifier, sender.getName()));
        }
        if (matchedPlayers.size() > 1) {
            throw new InvalidCommandArgument(String.format("Error parsing selector '%s' for %s. ambiguous result (more than one player matched) - %s",
                    playerIdentifier, sender.getName(), matchedPlayers));
        }

        return matchedPlayers.get(0);
    }
}
