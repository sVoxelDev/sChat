package net.silthus.chat;

import net.silthus.chat.chatter.PlayerChatter;
import org.bukkit.entity.Player;

public interface Chatter extends ChatSource, ChatTarget {

    static Chatter of(Player player) {
        return new PlayerChatter(player);
    }

    Channel getFocusedChannel();

    void setFocusedChannel(Channel channel);
}
