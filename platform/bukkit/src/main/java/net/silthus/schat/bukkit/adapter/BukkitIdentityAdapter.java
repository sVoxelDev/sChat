/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.bukkit.adapter;

import java.util.function.Supplier;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silthus.schat.identity.Identity;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNullElse;
import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.pointer.Pointer.weak;
import static org.bukkit.Bukkit.getPlayer;

public final class BukkitIdentityAdapter {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    private BukkitIdentityAdapter() {
    }

    public static @NotNull Identity identity(@NonNull OfflinePlayer player) {
        return Identity.identity(
            player.getUniqueId(),
            player.getName(),
            displayName(player)
        );
    }

    @NotNull
    private static Supplier<Component> displayName(@NonNull OfflinePlayer offlinePlayer) {
        if (offlinePlayer.isOnline())
            if (offlinePlayer instanceof Player player)
                return getOnlinePlayerDisplayName(player);
            else
                return getOnlinePlayerDisplayName(getPlayer(offlinePlayer.getUniqueId()));
        else
            return getDisplayNameFromName(offlinePlayer);
    }

    @NotNull
    private static Supplier<Component> getOnlinePlayerDisplayName(Player player) {
        return weak(player, BukkitIdentityAdapter::deserializeDisplayName, text(player.getName()));
    }

    @NotNull
    private static Supplier<Component> getDisplayNameFromName(@NotNull OfflinePlayer offlinePlayer) {
        return () -> text(requireNonNullElse(offlinePlayer.getName(), ""));
    }

    @NotNull
    private static TextComponent deserializeDisplayName(Player p) {
        return LEGACY_SERIALIZER.deserialize(p.getDisplayName());
    }
}
