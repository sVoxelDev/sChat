/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.schat.platform.plugin;

import java.util.Map;
import java.util.WeakHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.chatter.ChatterStore;
import net.silthus.schat.chatter.Chatters;
import net.silthus.schat.sender.PlayerAdapter;
import net.silthus.schat.sender.Sender;
import net.silthus.schat.ui.View;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.ui.Renderer.TABBED_CHANNELS;

final class ChatterManager implements Chatters {

    @Getter
    private final ChatterRepository repository;
    private final ChatterStore store;
    private final PlayerAdapter<?> playerAdapter;
    private final Map<Sender, View> viewCache = new WeakHashMap<>();

    ChatterManager(ChatterRepository repository, ChatterStore store, PlayerAdapter<?> playerAdapter) {
        this.repository = repository;
        this.store = store;
        this.playerAdapter = playerAdapter;
    }

    @Override
    public Chatter getChatter(Sender sender) {
        if (contains(sender.getUniqueId()))
            return get(sender.getUniqueId());
        return createAndCacheChatterFor(sender);
    }

    @Override
    public @NotNull View getView(@NonNull Sender sender) {
        return viewCache.computeIfAbsent(sender, c -> View.chatterView(sender, getChatter(sender), TABBED_CHANNELS));
    }

    @Override
    public @NotNull View getView(@NotNull Chatter chatter) {
        return getView(playerAdapter.getSender(chatter.getUniqueId()).orElseThrow());
    }

    @Override
    public void load(Chatter chatter) {
        store.load(chatter);
    }

    @Override
    public void save(Chatter chatter) {
        store.save(chatter);
    }

    @NotNull
    private Chatter createAndCacheChatterFor(Sender sender) {
        final Chatter chatter = createChatterFor(sender);
        add(chatter);
        return chatter;
    }

    private Chatter createChatterFor(Sender sender) {
        return Chatter.chatter(sender.getIdentity())
            .messenger(context -> getView(sender).update())
            .permissionHandler(sender::hasPermission)
            .create();
    }
}
