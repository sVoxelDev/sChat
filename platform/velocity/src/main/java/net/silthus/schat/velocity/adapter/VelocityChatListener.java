package net.silthus.schat.velocity.adapter;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import net.silthus.schat.platform.listener.ChatListener;

import static com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult.denied;
import static net.kyori.adventure.text.Component.text;

public final class VelocityChatListener extends ChatListener {

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        onChat(event.getPlayer().getUniqueId(), text(event.getMessage()));
        event.setResult(denied());
    }
}
