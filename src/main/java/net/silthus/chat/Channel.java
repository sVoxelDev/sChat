package net.silthus.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.java.Log;
import net.silthus.chat.config.ChannelConfig;
import org.bukkit.entity.Player;

import java.util.*;

@Log
@Data
@ToString(of = {"identifier", "config"})
@EqualsAndHashCode(of = "identifier", callSuper = false)
public class Channel extends AbstractChatTarget implements ChatSource {

    public static Channel channel(String identifier) {
        return new Channel(identifier);
    }

    public static Channel channel(String identifier, ChannelConfig config) {
        return new Channel(identifier, config);
    }

    private final String identifier;
    private final ChannelConfig config;
    private final Set<ChatTarget> targets = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<>()));

    private Channel(String identifier) {
        this(identifier, ChannelConfig.defaults());
    }

    private Channel(String identifier, ChannelConfig config) {
        this.identifier = identifier.toLowerCase();
        this.config = config;
    }

    public String getName() {
        if (getConfig().name() != null)
            return getConfig().name();
        return getIdentifier();
    }

    public String getPermission() {
        return Constants.Permissions.getChannelPermission(this);
    }

    public String getAutoJoinPermission() {
        return Constants.Permissions.getAutoJoinPermission(this);
    }

    public Collection<ChatTarget> getTargets() {
        return List.copyOf(targets);
    }

    public boolean canJoin(Player player) {
        if (getConfig().protect()) {
            return player.hasPermission(getPermission());
        }
        return true;
    }

    public boolean canAutoJoin(Player player) {
        if (!canJoin(player)) return false;
        if (canJoin(player) && getConfig().autoJoin()) return true;
        return player.hasPermission(getAutoJoinPermission());
    }

    public void add(@NonNull ChatTarget target) {
        this.targets.add(target);
    }

    public void remove(@NonNull ChatTarget target) {
        this.targets.remove(target);
    }

    @Override
    public Message sendMessage(String message) {

        return Message.message(message).to(this).send();
    }

    public void sendMessage(Message message) {
        addReceivedMessage(message);

        Message.MessageBuilder channelMessage = message.copy()
                .channel(this)
                .targets(getTargets());

        if (getConfig().sendToConsole())
            channelMessage.to(Console.console());

        channelMessage.send();
    }
}
