package net.silthus.schat;

import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Message {

    static @NotNull Message message(@NonNull Component message) {
        return message(null, message);
    }

    static @NotNull Message message(@Nullable String source, @NonNull Component text) {
        return new MessageImpl(source, text);
    }

    UUID getId();

    String getSource();

    Component getMessage();
}
