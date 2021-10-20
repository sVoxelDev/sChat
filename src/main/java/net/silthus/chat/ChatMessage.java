package net.silthus.chat;

import org.bukkit.entity.Player;

public record ChatMessage(Player player, String message) {

}
