package net.silthus.schat.bukkit.adapter;

import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.ui.ViewProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.silthus.schat.bukkit.adapter.BukkitIdentityAdapter.identity;
import static net.silthus.schat.chatter.Chatter.chatter;
import static org.bukkit.Bukkit.getOfflinePlayer;

public final class BukkitChatterFactory implements ChatterFactory {

    private final BukkitAudiences audiences;
    private final ViewProvider viewProvider;

    public BukkitChatterFactory(BukkitAudiences audiences, ViewProvider viewProvider) {
        this.audiences = audiences;
        this.viewProvider = viewProvider;
    }

    @Override
    public Chatter createChatter(UUID id) {
        return chatter(identity(getOfflinePlayer(id)))
            .messageHandler((message, context) -> {
                final Audience audience = audiences.player(id);
                viewProvider.updateView(context.chatter(), audience::sendMessage);
            })
            .permissionHandler(permission -> {
                final Player player = Bukkit.getPlayer(id);
                return player != null && player.hasPermission(permission);
            })
            .create();
    }
}
