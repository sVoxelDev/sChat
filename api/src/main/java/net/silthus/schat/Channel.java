package net.silthus.schat;

import java.util.List;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public interface Channel extends Target {

    @NotNull String getAlias();

    @NotNull Component getDisplayName();

    @NotNull @Unmodifiable List<Target> getTargets();

    void addTarget(@NonNull Target target);

    void removeTarget(@NonNull Target target);

    final class InvalidAlias extends RuntimeException {
    }
}
