package net.silthus.schat.velocity.adapter;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.platform.sender.SenderFactory;

public final class VelocitySenderFactory extends SenderFactory<CommandSource> {

    private final ProxyServer proxy;

    public VelocitySenderFactory(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean isPlayerOnline(UUID playerId) {
        return proxy.getPlayer(playerId).isPresent();
    }

    @Override
    protected Class<CommandSource> getSenderType() {
        return CommandSource.class;
    }

    @Override
    protected Identity getIdentity(CommandSource sender) {
        if (sender instanceof Player player)
            return identity(player);
        return CONSOLE;
    }

    public static Identity identity(Player player) {
        return Identity.identity(player.getUniqueId(), player.getUsername());
    }

    @Override
    protected void sendMessage(CommandSource sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    protected boolean hasPermission(CommandSource sender, String node) {
        return sender.hasPermission(node);
    }

    @Override
    protected void performCommand(CommandSource sender, String command) {
        proxy.getCommandManager().executeAsync(sender, command);
    }

    @Override
    protected boolean isConsole(CommandSource sender) {
        return sender instanceof ConsoleCommandSource;
    }
}
