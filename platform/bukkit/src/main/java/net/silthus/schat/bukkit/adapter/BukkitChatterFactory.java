package net.silthus.schat.bukkit.adapter;

import java.util.UUID;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.ui.View;
import net.silthus.schat.ui.ViewModel;
import net.silthus.schat.ui.views.Views;

import static net.silthus.schat.bukkit.adapter.BukkitIdentityAdapter.identity;
import static net.silthus.schat.chatter.Chatter.chatter;
import static org.bukkit.Bukkit.getOfflinePlayer;

public final class BukkitChatterFactory implements ChatterFactory {

    private final BukkitAudiences audiences;

    public BukkitChatterFactory(BukkitAudiences audiences) {
        this.audiences = audiences;
    }

    @Override
    public Chatter createChatter(UUID id) {
        return chatter(identity(getOfflinePlayer(id)))
            .messageHandler((message, context) -> {
                final View view = Views.tabbedChannels(ViewModel.of(context.chatter()));
                audiences.player(id).sendMessage(view.render());
            })
            .create();
    }
}
