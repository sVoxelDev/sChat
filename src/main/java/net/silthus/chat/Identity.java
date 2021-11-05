package net.silthus.chat;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface Identity {

    String getIdentifier();

    Component getName();

    default boolean isPlayer() {
        return false;
    }

    default Player getPlayer() {
        return null;
    }
}
