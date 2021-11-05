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
import net.silthus.chat.commands.SChatCommands;
import net.silthus.chat.config.PluginConfig;
import net.silthus.chat.protocollib.ChatPacketQueue;
import net.silthus.chat.targets.Channel;
import net.silthus.chat.targets.Console;
import net.silthus.chat.targets.PlayerChatter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
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

    private ChannelRegistry channelRegistry;
    private ChatterManager chatterManager;

    private PaperCommandManager commandManager;
    private ProtocolManager protocolManager;

    @Setter(AccessLevel.PACKAGE)
    private ChatPacketQueue chatPacketQueue;

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
        setupAndLoadConfigs();

        setupAndLoadChannels();
        setupChatterManager();
        setupConsoleChatter();

        setupProtocolLib();
        setupCommands();
//
//        boolean isPapermc = false;
//        try {
//            Class.forName("com.destroystokyo.paper.VersionHistoryManager$VersionData");
//            isPapermc = true;
//        } catch (ClassNotFoundException e) {
//            Bukkit.getLogger().info("Not paper");
//        }
//
//        if (isPapermc) {
//            Bukkit.getLogger().info("Got paper");
//        }
    }

    @Override
    public void onDisable() {

        Console.instance = null;
        commandManager.unregisterCommands();
    }

    private void setupAndLoadConfigs() {
        saveDefaultConfig();
        saveResource("config.defaults.yml", true);
        saveResource("lang_en.yaml", false);
        pluginConfig = PluginConfig.fromConfig(getConfig());
    }

    private void setupAndLoadChannels() {
        channelRegistry = new ChannelRegistry(this);
        channelRegistry.load(getPluginConfig());
    }

    private void setupChatterManager() {
        chatterManager = new ChatterManager(this);
    }

    private void setupConsoleChatter() {
        getServer().getPluginManager().registerEvents(Console.init(getPluginConfig().console()), this);
    }

    private void setupProtocolLib() {
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            protocolManager = ProtocolLibrary.getProtocolManager();
            chatPacketQueue = new ChatPacketQueue(this);
            protocolManager.addPacketListener(chatPacketQueue);
            getLogger().info("Enabled ProtocolLib handler.");
        }
    }

    private void setupCommands() {
        commandManager = new PaperCommandManager(this);

        registerChatterContext(commandManager);
        registerChannelContext(commandManager);

        registerChannelCompletion(commandManager);

        loadCommandLocales(commandManager);

        commandManager.registerCommand(new SChatCommands(this));
    }

    private void registerChannelContext(PaperCommandManager commandManager) {
        commandManager.getCommandContexts().registerContext(Channel.class, context -> {
            String channelIdentifier = context.popFirstArg();
            return getChannelRegistry().get(channelIdentifier)
                    .orElseThrow(() -> new InvalidCommandArgument("The channel '" + channelIdentifier + "' does not exist."));
        });
    }

    private void registerChatterContext(PaperCommandManager commandManager) {
        commandManager.getCommandContexts().registerIssuerAwareContext(PlayerChatter.class, context -> {
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
                    Optional<PlayerChatter> chatter = getChatterManager().getChatter(arg);
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

    private void registerChannelCompletion(PaperCommandManager commandManager) {
        commandManager.getCommandCompletions().registerAsyncCompletion("channels", context ->
                getChannelRegistry().getChannels().stream()
                        .map(Channel::getIdentifier)
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
