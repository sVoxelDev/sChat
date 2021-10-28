package net.silthus.chat.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import net.silthus.chat.SChat;

@CommandAlias("schat|channel|ch")
public class ChannelCommands extends BaseCommand {

    private final SChat plugin;

    public ChannelCommands(SChat plugin) {
        this.plugin = plugin;
    }
}
