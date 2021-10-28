package net.silthus.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
        return SChat.instance().getChatManager().registerChatter(player);
    }

    static Chatter create(Player player) {
        return new Chatter(player);
    }

    private final Player player;
    private Channel activeChannel;

    Chatter(Player player) {
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

    public void setActiveChannel(Channel activeChannel) {
        this.activeChannel = activeChannel;
        if (activeChannel != null)
            activeChannel.add(this);
    }

    @Override
    public void sendMessage(Message message) {
        SChat.instance().getAudiences()
                .player(getPlayer())
                .sendMessage(
                        getIdentity(message),
                        appendSourceMetadataToMessage(message),
                        MessageType.CHAT
                );
        addReceivedMessage(message);
    }

    @Override
    public void sendMessageTo(ChatTarget target, String message) {
        target.sendMessage(Message.of(this, message));
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

    private Identity getIdentity(Message message) {
        return message.getSource() != null ? Identity.identity(message.getSource().getUniqueId()) : Identity.nil();
    }

    private TextComponent appendSourceMetadataToMessage(Message message) {
        return Component.text()
                .append(LegacyComponentSerializer.legacySection().deserialize(message.formattedMessage()))
                .append(Component.storageNBT()
                        .nbtPath(message.getSource() != null ? "global" : "system")
                        .storage(Key.key("schat:channel")))
                .build();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (isNotApplicable(event)) return;

        sendMessageTo(getActiveChannel(), event.getMessage());
        event.setCancelled(true);
    }
}
