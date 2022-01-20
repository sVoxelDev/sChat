package net.silthus.schat.chatter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.repository.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public sealed interface Chatter extends Entity<UUID>, MessageTarget, Identified permits ChatterImpl {

    static Chatter createChatter(@NonNull Identity identity) {
        return chatter(identity).create();
    }

    static Builder chatter(@NonNull Identity identity) {
        return ChatterImpl.builder(identity);
    }

    @Override
    default @NotNull UUID getKey() {
        return getUniqueId();
    }

    default @NotNull UUID getUniqueId() {
        return getIdentity().getUniqueId();
    }

    default @NotNull String getName() {
        return getIdentity().getName();
    }

    default @NotNull Component getDisplayName() {
        return getIdentity().getDisplayName();
    }

    @NotNull @Unmodifiable List<Channel> getChannels();

    @NotNull Optional<Channel> getActiveChannel();

    boolean isActiveChannel(@Nullable Channel channel);

    void setActiveChannel(@Nullable Channel activeChannel);

    void join(@NonNull Channel channel);

    boolean isJoined(@Nullable Channel channel);

    void leave(Channel channel);

    @NotNull @Unmodifiable Set<Message> getMessages();

    boolean hasPermission(String permission);

    interface Builder {
        Builder messageHandler(MessageHandler messageHandler);

        Builder permissionHandler(PermissionHandler permissionHandler);

        Chatter create();
    }

    interface MessageHandler {
        void handleMessage(Message message, Context context);

        record Context(Chatter chatter) {
        }
    }

    interface PermissionHandler {
        boolean hasPermission(String permission);
    }
}
