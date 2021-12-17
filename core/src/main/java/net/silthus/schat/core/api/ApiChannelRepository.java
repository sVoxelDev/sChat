package net.silthus.schat.core.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.Channel;
import net.silthus.schat.Channels;
import net.silthus.schat.core.channel.ChannelRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class ApiChannelRepository implements Channels {

    private final ChannelRepository handle;

    public ApiChannelRepository(ChannelRepository handle) {
        this.handle = handle;
    }

    @Override
    public @NotNull @Unmodifiable List<Channel> all() {
        final ArrayList<Channel> channels = new ArrayList<>();
        for (net.silthus.schat.core.channel.Channel channel : handle.all()) {
            channels.add(channel.getApiProxy());
        }
        return Collections.unmodifiableList(channels);
    }

    @Override
    public @NotNull Optional<Channel> get(@NonNull String alias) {
        return handle.get(alias).map(net.silthus.schat.core.channel.Channel::getApiProxy);
    }

    @Override
    public @NotNull Channel create(@NonNull String alias) throws DuplicateAlias {
        return handle.create(alias).getApiProxy();
    }

    @Override
    public boolean contains(@NonNull String alias) {
        return handle.contains(alias);
    }

    @Override
    public @NotNull Channel create(@NonNull String alias, @NonNull Component displayName) {
        return handle.create(alias, displayName).getApiProxy();
    }
}
