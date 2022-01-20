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
