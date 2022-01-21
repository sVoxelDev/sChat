package net.silthus.schat.velocity.adapter;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.Optional;
import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.ui.View;
import net.silthus.schat.ui.ViewModel;
import net.silthus.schat.ui.views.Views;

import static net.silthus.schat.velocity.adapter.VelocitySenderFactory.identity;

public class VelocityChatterFactory implements ChatterFactory {

    private final ProxyServer proxy;

    public VelocityChatterFactory(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public Chatter createChatter(UUID id) {
        final Optional<Player> optionalPlayer = proxy.getPlayer(id);
        if (optionalPlayer.isEmpty())
            return Chatter.empty();
        final Player player = optionalPlayer.get();
        return Chatter.chatter(identity(player))
            .messageHandler((message, context) -> {
                final View view = Views.tabbedChannels(ViewModel.of(context.chatter()));
                player.sendMessage(view.render());
            }).permissionHandler(player::hasPermission)
            .create();
    }
}
