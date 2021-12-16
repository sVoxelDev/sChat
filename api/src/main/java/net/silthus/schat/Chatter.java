package net.silthus.schat;

import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public interface Chatter extends Target {

    @NotNull @Unmodifiable List<Message> getMessages();

    @NotNull @Unmodifiable List<Channel> getChannels();

    @NotNull Optional<Channel> getActiveChannel();

    void setActiveChannel(@NonNull Channel channel);

    boolean isActiveChannel(@NonNull Channel channel);

    void clearActiveChannel();

    void join(@NonNull Channel channel);

    void leave(@NonNull Channel channel);
}
