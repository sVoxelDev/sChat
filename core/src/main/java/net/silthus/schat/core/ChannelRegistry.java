package net.silthus.schat.core;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.NonNull;
import net.silthus.schat.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class ChannelRegistry {

    private final Map<String, Channel> channels = new HashMap<>();

    public @NotNull @Unmodifiable List<Channel> all() {
        return List.copyOf(channels.values());
    }

    public Channel create(final @NonNull String alias) {
        if (contains(alias))
            throw new DuplicateAlias();
        return channels.computeIfAbsent(formatAlias(alias), ChannelImpl::new);
    }

    public boolean contains(final @NonNull String alias) {
        return channels.containsKey(formatAlias(alias));
    }

    @NotNull
    private String formatAlias(@NotNull String alias) {
        return alias.toLowerCase(Locale.ROOT);
    }

    public static final class DuplicateAlias extends RuntimeException {
    }
}
