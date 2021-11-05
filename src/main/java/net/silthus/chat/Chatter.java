package net.silthus.chat;

import net.silthus.chat.targets.PlayerChatter;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Chatter extends ChatTarget, ChatSource {

    static PlayerChatter of(Player player) {
        return SChat.instance().getChatterManager().registerChatter(player);
    }

    UUID getUniqueId();
}
