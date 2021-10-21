package net.silthus.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.silthus.chat.config.ChannelConfig;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log
@Data
@EqualsAndHashCode(of = "alias", callSuper = false)
public class Channel extends AbstractChatTarget {

    private final String alias;
    private final ChannelConfig config;
    private final Set<ChatTarget> targets = new HashSet<>();

    public Channel(String alias) {
        this(alias, new ChannelConfig());
    }

    public Channel(String alias, ChannelConfig config) {
        this.alias = alias;
        this.config = config;
    }

    public String getName() {
        if (getConfig().getName() != null)
            return getConfig().getName();
        return getAlias();
    }

    public String getPermission() {
        return Constants.CHANNEL_PERMISSION + "." + getAlias();
    }

    public Collection<ChatTarget> getTargets() {
        return List.copyOf(targets);
    }

    public void join(@NonNull ChatTarget chatter) {
        this.targets.add(chatter);
    }

    public void leave(@NonNull ChatTarget chatter) {
        this.targets.remove(chatter);
    }

    public void sendMessage(Message message) {
        final Message chatMessage;
        if (message.formatted())
            chatMessage = message;
        else
            chatMessage = message.format(getConfig().getFormat());
        getTargets().forEach(chatter -> chatter.sendMessage(chatMessage));
        setLastMessage(message);
    }

}
