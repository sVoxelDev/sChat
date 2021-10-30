package net.silthus.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
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
    public String getDisplayName() {
        return getPlayer().getDisplayName();
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
                        appendSourceMetadataToMessage(message),
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

    private TextComponent appendSourceMetadataToMessage(Message message) {
        return Component.text()
                .append(LegacyComponentSerializer.legacySection().deserialize(message.formattedMessage()))
                .append(Component.storageNBT()
                        .nbtPath(message.getTarget().getIdentifier())
                        .storage(Constants.NBT_CHAT_TARGET_KEY))
                .build();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (isNotApplicable(event)) return;

        Message.of(this, event.getMessage())
                .withTarget(getActiveChannel())
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
