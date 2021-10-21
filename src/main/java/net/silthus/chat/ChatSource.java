package net.silthus.chat;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface ChatSource {

    static ChatSource of(Player player) {
        return Chatter.of(player);
    }

    UUID getUniqueId();

    String getDisplayName();

    void sendMessageTo(ChatTarget target, String message);
}
