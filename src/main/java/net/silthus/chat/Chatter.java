package net.silthus.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
        return SChat.instance().getChatterManager().registerChatter(player);
    }

    static Chatter create(Player player) {
        return new Chatter(player);
    }

    private final Player player;
    private Channel activeChannel;

    Chatter(Player player) {
        this.player = player;
    }

    public UUID getUniqueId() {
        return getPlayer().getUniqueId();
    }

    @Override
    public String getIdentifier() {
        return getUniqueId().toString();
    }

    @Override
    public String getName() {
        return getPlayer().getDisplayName();
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    public void setActiveChannel(Channel activeChannel) {
        this.activeChannel = activeChannel;
        if (activeChannel != null)
            subscribe(activeChannel);
    }

    public boolean canJoin(Channel channel) {
        return channel.canJoin(getPlayer());
    }

    public void join(Channel channel) throws AccessDeniedException {
        if (!canJoin(channel))
            throw new AccessDeniedException("You don't have permission to join the channel: " + channel.getIdentifier());
        setActiveChannel(channel);
    }

    @Override
    public void sendMessage(Message message) {
        SChat.instance().getAudiences()
                .player(getPlayer())
                .sendMessage(
                        getIdentity(message),
                        appendMessageId(message),
                        MessageType.CHAT
                );
        addReceivedMessage(message);
    }

    private Identity getIdentity(Message message) {
        try {
            return message.getSource() != null ? Identity.identity(UUID.fromString(message.getSource().getIdentifier())) : Identity.nil();
        } catch (IllegalArgumentException e) {
            return Identity.nil();
        }
    }

    private TextComponent appendMessageId(Message message) {
        return Component.text()
                .append(message.formatted())
                .append(Component.storageNBT()
                        .nbtPath(message.getId().toString())
                        .storage(Constants.NBT_MESSAGE_ID))
                .build();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (isNotApplicable(event)) return;

        Message.message(this, event.getMessage())
                .to(getActiveChannel())
                .send();
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
