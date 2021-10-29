package net.silthus.chat.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.*;
import co.aikar.locales.MessageKey;
import net.silthus.chat.*;
import org.bukkit.ChatColor;

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

    @CommandAlias("test")
    @Subcommand("test")
    public class TestCommands extends BaseCommand {

        @Subcommand("center")
        @CommandCompletion("* -")
        public void center(String text, @Default(" ") String spacer) {
            getCurrentCommandIssuer().sendMessage(ChatUtil.centerText(text, spacer));
        }

        @Subcommand("wrap")
        @CommandCompletion("*")
        public void wrapText(String text) {
            getCurrentCommandIssuer().sendMessage(ChatUtil.wrapText(text, "|-", "-", "-|"));
        }

        @Subcommand("space")
        @CommandCompletion("*")
        public void spaceText(String text) {
            getCurrentCommandIssuer().sendMessage(ChatUtil.spaceAndCenterText("|-", "|", "-|", " ", "Hi", ChatColor.GREEN + "there" + ChatColor.RESET, ChatColor.BOLD + "Player!" + ChatColor.RESET));
        }

        @Subcommand("frame")
        public void frame() {
            getCurrentCommandIssuer().sendMessage(ChatUtil.wrapText(FontInfo.LONG_MINUS.toString(), FontInfo.LEFT_LINE_CORNER.toString(), FontInfo.LONG_MINUS.toString(), FontInfo.RIGHT_LINE_CORNER.toString()));
            getCurrentCommandIssuer().sendMessage(ChatUtil.wrapText("Global | Local | Trade", FontInfo.LONG_LINE.toString(), " ", FontInfo.LONG_LINE.toString()));
        }
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
