package net.silthus.schat.core.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.Channel;
import net.silthus.schat.Message;
import net.silthus.schat.core.chatter.Chatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class ApiChatter implements net.silthus.schat.Chatter {

    private final Chatter handle;

    public ApiChatter(Chatter handle) {
        this.handle = handle;
    }

    @Override
    public UUID getId() {
        return handle.getId();
    }

    @Override
    public String getName() {
        return handle.getName();
    }

    @Override
    public Component getDisplayName() {
        return handle.getDisplayName();
    }

    @Override
    public @NotNull @Unmodifiable List<Message> getMessages() {
        return handle.getMessages();
    }

    @Override
    public @NotNull Optional<Channel> getActiveChannel() {
        return handle.getActiveChannel();
    }

    @Override
    public @NotNull @Unmodifiable List<Channel> getChannels() {
        return handle.getChannels();
    }

    @Override
    public void setActiveChannel(Channel channel) {
        join(channel);
        handle.setActiveChannel(channel);
    }

    @Override
    public void join(Channel channel) {
        handle.addChannel(channel);
        channel.addTarget(this);
    }

    @Override
    public void leave(Channel channel) {
        handle.removeChannel(channel);
        channel.removeTarget(this);
        if (handle.isActiveChannel(channel))
            handle.clearActiveChannel();
    }

    @Override
    public void sendMessage(@NonNull Message message) {
        handle.sendMessage(message);
    }
}
