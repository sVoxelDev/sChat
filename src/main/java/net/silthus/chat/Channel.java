package net.silthus.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.java.Log;
import net.silthus.chat.config.ChannelConfig;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

@Log
@Data
@ToString(of = {"identifier", "config"})
@EqualsAndHashCode(of = "identifier", callSuper = false)
public class Channel extends AbstractChatTarget {

    private final String identifier;
    private final ChannelConfig config;
    private final Set<ChatTarget> targets = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<>()));

    public Channel(String identifier) {
        this(identifier, new ChannelConfig());
    }

    public Channel(String identifier, ChannelConfig config) {
        this.identifier = identifier;
        this.config = config;
    }

    public Channel(String identifier, ConfigurationSection config) {
        this(identifier, ChannelConfig.of(config));
    }


    public String getName() {
        if (getConfig().getName() != null)
            return getConfig().getName();
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

    public void add(@NonNull ChatTarget chatter) {
        this.targets.add(chatter);
    }

    public void remove(@NonNull ChatTarget chatter) {
        this.targets.remove(chatter);
    }

    public void sendMessage(Message message) {
        final Message chatMessage = message.withFormat(getConfig().getFormat());
        getTargets().forEach(chatter -> chatter.sendMessage(chatMessage));
        addReceivedMessage(message);
    }

}
