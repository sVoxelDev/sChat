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

import co.aikar.commands.*;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.base.Strings;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.chat.Chat;
import net.silthus.chat.commands.NicknameCommands;
import net.silthus.chat.commands.SChatCommands;
import net.silthus.chat.config.Language;
import net.silthus.chat.config.PluginConfig;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.ChannelRegistry;
import net.silthus.chat.conversations.ConversationManager;
import net.silthus.chat.identities.ChatterManager;
import net.silthus.chat.identities.Console;
import net.silthus.chat.integrations.placeholders.BasicPlaceholders;
import net.silthus.chat.integrations.placeholders.PlaceholderAPIWrapper;
import net.silthus.chat.integrations.placeholders.Placeholders;
import net.silthus.chat.integrations.protocollib.ChatPacketQueue;
import net.silthus.chat.integrations.vault.VaultProvider;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.Nullable;

import java.io.File;
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

    private BukkitAudiences audiences;
    @Accessors(fluent = true)
    private Language language;

    private PluginConfig pluginConfig;
    private Metrics metrics;

    @Setter(AccessLevel.PACKAGE)
    private ChannelRegistry channelRegistry;
    private ChatterManager chatterManager;
    private ConversationManager conversationManager;

    private PaperCommandManager commandManager;
    private ProtocolManager protocolManager;
    @Setter(AccessLevel.PACKAGE)
    private VaultProvider vaultProvider;
    private Placeholders placeholders;

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

    public void reload() {
        reloadConfig();

        final PluginConfig oldConfig = getPluginConfig();
        pluginConfig = PluginConfig.config(getConfig());

        if (oldConfig.equals(pluginConfig)) return;

        Console.console().setConfig(getPluginConfig().console());
        getChannelRegistry().load(getPluginConfig());
    }

    @Override
    public void onEnable() {
        setupAndLoadConfigs();
        setupAndLoadLanguage();

        setupBStats();

        setupAdventureAPI();
        setupChannelRegistry();
        setupChatterManager();
        setupConversationManager();
        setupConsoleChatter();

        setupProtocolLib();
        setupVaultIntegration();
        setupBungeecordIntegration();
        setupPlaceholderAPIIntegration();

        loadChannels();

        setupCommands();
    }

    @Override
    public void onDisable() {

        Console.instance = null;
        if (commandManager != null) commandManager.unregisterCommands();
        if (chatterManager != null) chatterManager.removeAllChatters();
        if (channelRegistry != null) channelRegistry.clear();
        if (bungeecord != null) tearDownBungeecord();

        HandlerList.unregisterAll(this);
    }

    private void setupAndLoadConfigs() {
        saveDefaultConfig();
        saveResource("config.default.yml", true);
        pluginConfig = PluginConfig.config(getConfig());
    }

    private void setupAndLoadLanguage() {
        final String languageConfig = getPluginConfig().languageConfig();
        saveResource(languageConfig, true);
        this.language = Language.language(YamlConfiguration.loadConfiguration(new File(getDataFolder(), languageConfig)),
                Locale.forLanguageTag(languageConfig.replace("languages/", "").replace(".yaml", "")));
    }

    private void setupBStats() {
        if (isTesting()) return;
        metrics = new Metrics(this, Constants.BSTATS_ID);
    }

    private void setupAdventureAPI() {
        this.audiences = BukkitAudiences.create(this);
    }

    private void setupChannelRegistry() {
        channelRegistry = new ChannelRegistry(this);
    }

    private void loadChannels() {
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

    private void setupPlaceholderAPIIntegration() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholders = new PlaceholderAPIWrapper();
        } else {
            placeholders = new BasicPlaceholders();
        }
    }

    private void setupCommands() {
        commandManager = new PaperCommandManager(this);

        registerChatterContext(commandManager);
        registerChannelContext(commandManager);
        registerConversationContext(commandManager);
        registerMessageContext(commandManager);

        registerChannelCompletion(commandManager);
        registerChatterCompletion(commandManager);

        loadCommandLocales(commandManager);

        commandManager.registerCommand(new SChatCommands(this));
        commandManager.registerCommand(new NicknameCommands(this));
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
            if (selectSelf(context))
                return Chatter.commandSender(context.getSender());

            String arg = context.popFirstArg();
            Player player = getPlayer(context, arg);
            if (player == null && context.hasFlag("defaultself"))
                return Chatter.commandSender(context.getSender());
            if (player == null) return null;

            validatePermissionForOthers(context, player);

            return Chatter.player(player);
        });
    }

    private boolean selectSelf(BukkitCommandExecutionContext context) {
        return context.hasFlag("self") || defaultsToSelf(context);
    }

    private boolean defaultsToSelf(BukkitCommandExecutionContext context) {
        return Strings.isNullOrEmpty(context.getFirstArg()) && context.hasFlag("defaultself");
    }

    @Nullable
    private Player getPlayer(BukkitCommandExecutionContext context, String arg) {
        if (isEntitySelector(arg))
            return selectPlayer(context.getSender(), arg);
        return findPlayer(context, arg);
    }

    @Nullable
    private Player findPlayer(BukkitCommandExecutionContext context, String arg) {
        try {
            return Bukkit.getPlayer(UUID.fromString(arg));
        } catch (Exception e) {
            return ACFBukkitUtil.findPlayerSmart(context.getIssuer(), arg);
        }
    }

    private void validatePermissionForOthers(BukkitCommandExecutionContext context, Player player) {
        if (player == null) return;
        boolean hasOtherFlag = context.hasFlag("other");
        boolean isOther = hasOtherFlag && !Objects.equals(player, context.getPlayer());
        String otherPermission = context.getFlagValue("other", Constants.PERMISSION_ADMIN_OTHERS);
        if (isOther && !context.getIssuer().hasPermission(otherPermission))
            throw new ConditionFailedException(MessageKeys.PERMISSION_DENIED);
    }

    private void registerConversationContext(PaperCommandManager commandManager) {
        commandManager.getCommandContexts().registerContext(Conversation.class, context -> getConversationManager().getConversation(UUID.fromString(context.popFirstArg())));
    }

    private void registerMessageContext(PaperCommandManager commandManager) {
        commandManager.getCommandContexts().registerIssuerAwareContext(Message.class, context -> {
            try {
                final Chatter chatter = Chatter.player(context.getPlayer());
                final Optional<Message> optionalMessage = chatter.getMessage(UUID.fromString(context.popFirstArg()));
                if (optionalMessage.isEmpty())
                    throw new InvalidCommandArgument(MessageKeys.UNKNOWN_COMMAND);
                return optionalMessage.get();
            } catch (IllegalArgumentException e) {
                throw new InvalidCommandArgument(MessageKeys.INVALID_SYNTAX);
            }
        });
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
                        .map(Chatter::getName)
                        .filter(name -> !context.getSender().getName().equalsIgnoreCase(name))
                        .collect(Collectors.toSet()));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadCommandLocales(PaperCommandManager commandManager) {
        commandManager.getLocales().setDefaultLocale(Locale.ENGLISH);
        final File languages = new File(getDataFolder(), "languages");
        languages.mkdirs();
        for (File file : Objects.requireNonNull(languages.listFiles())) {
            saveResource("languages/" + file.getName(), true);
            loadCommandLanguage(file);
        }
    }

    private void loadCommandLanguage(File file) {
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        commandManager.getLocales().loadLanguage(config, Locale.forLanguageTag(file.getName().replace(".yaml", "")));
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
