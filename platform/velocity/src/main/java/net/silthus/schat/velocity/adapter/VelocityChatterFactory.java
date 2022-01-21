package net.silthus.schat.velocity.adapter;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.Optional;
import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.ui.ViewProvider;

import static net.silthus.schat.velocity.adapter.VelocitySenderFactory.identity;

public class VelocityChatterFactory implements ChatterFactory {

    private final ProxyServer proxy;
    private final ViewProvider viewProvider;

    public VelocityChatterFactory(ProxyServer proxy, ViewProvider viewProvider) {
        this.proxy = proxy;
        this.viewProvider = viewProvider;
    }

    @Override
    public Chatter createChatter(UUID id) {
        final Optional<Player> optionalPlayer = proxy.getPlayer(id);
        if (optionalPlayer.isEmpty())
            return Chatter.empty();
        final Player player = optionalPlayer.get();
        return Chatter.chatter(identity(player))
            .messageHandler(
                (message, context) -> viewProvider.updateView(context.chatter(), player::sendMessage)
            ).permissionHandler(player::hasPermission)
            .create();
    }
}
