package net.silthus.chat.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import net.silthus.chat.Channel;
import net.silthus.chat.Chatter;
import net.silthus.chat.SChat;
import org.bukkit.entity.Player;

@CommandAlias("schat|channel|ch")
public class ChannelCommands extends BaseCommand {

    private final SChat plugin;

    public ChannelCommands(SChat plugin) {
        this.plugin = plugin;
    }

    @Subcommand("join")
    public void join(Player player, String channel) {
        player.sendMessage("Pseudo joined: " + channel);
    }

    @Subcommand("test")
    public void test(Player player, String message) {

        Chatter chatter = plugin.getChatManager().getChatter(player);
        for (Channel channel : plugin.getChatManager().getChannels()) {
            channel.add(chatter);
            chatter.setActiveChannel(channel);
            chatter.sendMessageTo(channel, message);
        }
//        Channel channel = plugin.getChannelManager().getChannels().get(0);
//        chatter.setActiveChannel(channel);
//        channel.add(chatter);
//        TextComponent component = Component.text()
//                .content(message)
//                .append(Component.storageNBT()
//                        .nbtPath(channel.getAlias())
//                        .storage(Key.key("schat:channel")))
//                .build();
//        chatter.sendMessageTo(channel, message);
//        BukkitAudiences bukkitAudiences = BukkitAudiences.create(plugin);
//        Audience audience = bukkitAudiences.player(player);
//        audience.sendMessage(component);
//        TextComponent.Builder text = Component.text();
//        for (int i = 100; i > 0; i--) {
//            player.sendMessage(i + "");
//            text.append(newline());
//        }
//        text.append(Component.text("test"));
//        audience.sendMessage(text.build());
    }
}
