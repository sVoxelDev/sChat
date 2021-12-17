package net.silthus.schat.core.api;

import java.util.List;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.Message;
import net.silthus.schat.Target;
import net.silthus.schat.core.channel.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class ApiChannel implements net.silthus.schat.Channel {

    private final Channel handle;

    public ApiChannel(Channel handle) {
        this.handle = handle;
    }

    @Override
    public String getAlias() {
        return handle.getAlias();
    }

    @Override
    public Component getDisplayName() {
        return handle.getDisplayName();
    }

    @Override
    public void setDisplayName(Component displayName) {
        handle.setDisplayName(displayName);
    }

    @Override
    public @NotNull @Unmodifiable List<Target> getTargets() {
        return handle.getTargets();
    }

    @Override
    public void addTarget(@NonNull Target target) {
        handle.addTarget(target);
    }

    @Override
    public void removeTarget(@NonNull Target target) {
        handle.removeTarget(target);
    }

    @Override
    public void sendMessage(@NonNull Message message) {
        handle.sendMessage(message);
    }
}
