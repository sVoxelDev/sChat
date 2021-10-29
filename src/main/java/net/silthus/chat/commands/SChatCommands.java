package net.silthus.chat.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.locales.MessageKey;
import net.silthus.chat.AccessDeniedException;
import net.silthus.chat.Channel;
import net.silthus.chat.Chatter;
import net.silthus.chat.SChat;

import static net.silthus.chat.Constants.Language.*;

@CommandAlias("schat")
public class SChatCommands extends BaseCommand {

    static MessageKey key(String key) {
        return MessageKey.of(ACF_BASE_KEY + "." + key);
    }

    private final SChat plugin;

    public SChatCommands(SChat plugin) {
        this.plugin = plugin;
    }

    @Subcommand("channel|ch")
    @CommandAlias("channel")
    public class ChannelCommands extends BaseCommand {

        @Subcommand("join")
        @CommandAlias("join|ch")
        @CommandCompletion("@channels")
        public void join(Channel channel, @Flags("self") Chatter chatter) {
            try {
                chatter.join(channel);
                success(JOINED_CHANNEL, "{channel}", channel.getIdentifier());
            } catch (AccessDeniedException e) {
                error(ACCESS_TO_CHANNEL_DENIED, "{channel}", channel.getIdentifier());
                throw new ConditionFailedException(key(ACCESS_TO_CHANNEL_DENIED), "{channel}", channel.getIdentifier());
            }
        }
    }

    private void success(String key, String... replacements) {
        getCurrentCommandIssuer().sendMessage(MessageType.INFO, key(key), replacements);
    }

    private void error(String key, String... replacements) {
        getCurrentCommandIssuer().sendMessage(MessageType.ERROR, key(key), replacements);
    }
}
