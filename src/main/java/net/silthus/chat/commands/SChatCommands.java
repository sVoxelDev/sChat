package net.silthus.chat.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.*;
import co.aikar.locales.MessageKey;
import net.silthus.chat.*;

import static net.silthus.chat.Constants.Language.*;
import static net.silthus.chat.Constants.*;

@CommandAlias("schat")
@CommandPermission(PERMISSION_PLAYER_COMMANDS)
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
    @CommandPermission(PERMISSION_PLAYER_CHANNEL_COMMANDS)
    public class ChannelCommands extends BaseCommand {

        @Subcommand("join")
        @CommandAlias("join|ch")
        @CommandCompletion("@channels")
        @CommandPermission(PERMISSION_PLAYER_CHANNEL_JOIN)
        public void join(@Flags("self") Chatter chatter, Channel channel) {
            try {
                chatter.join(channel);
                success(JOINED_CHANNEL, "{channel}", channel.getName());
            } catch (AccessDeniedException e) {
                error(ACCESS_TO_CHANNEL_DENIED, "{channel}", channel.getName());
                throw new ConditionFailedException(key(ACCESS_TO_CHANNEL_DENIED), "{channel}", channel.getName());
            }
        }

        @Subcommand("message|msg")
        @CommandAlias("ch")
        @CommandCompletion("@channels *")
        @CommandPermission(PERMISSION_PLAYER_CHANNEL_QUICKMESSAGE)
        public void quickMessage(@Flags("self") Chatter chatter, Channel channel, String message) {
            if (channel.canSendMessage(chatter)) {
                Message.message(chatter, message).to(channel).send();
            } else {
                error(SEND_TO_CHANNEL_DENIED, "{channel}", channel.getName());
                throw new ConditionFailedException(key(SEND_TO_CHANNEL_DENIED), "{channel}", channel.getName());
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
