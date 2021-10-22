package net.silthus.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

@Data
@EqualsAndHashCode(of = "player", callSuper = false)
public class Chatter extends AbstractChatTarget implements Listener, ChatSource, ChatTarget {

    public static Chatter of(Player player) {
        return new Chatter(player);
    }

    private final Player player;
    private Channel activeChannel;

    public Chatter(Player player) {
        this.player = player;
    }

    @Override
    public UUID getUniqueId() {
        return getPlayer().getUniqueId();
    }

    @Override
    public String getDisplayName() {
        return getPlayer().getDisplayName();
    }

    @Override
    public void sendMessage(Message message) {
        player.sendMessage(message.formattedMessage());
        setLastMessage(message);
    }

    @Override
    public void sendMessageTo(ChatTarget target, String message) {
        target.sendMessage(Message.of(this, message));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (isNotApplicable(event)) return;

        sendMessageTo(getActiveChannel(), event.getMessage());
        event.setCancelled(true);
    }

    private boolean isNotApplicable(AsyncPlayerChatEvent event) {
        return isNotSamePlayer(event) || noActiveChannel(event);
    }

    private boolean isNotSamePlayer(AsyncPlayerChatEvent event) {
        return !event.getPlayer().equals(getPlayer());
    }

    private boolean noActiveChannel(AsyncPlayerChatEvent event) {
        if (getActiveChannel() != null) return false;
        event.getPlayer().sendMessage(Constants.Errors.NO_ACTIVE_CHANNEL);
        event.setCancelled(true);
        return true;
    }
}
