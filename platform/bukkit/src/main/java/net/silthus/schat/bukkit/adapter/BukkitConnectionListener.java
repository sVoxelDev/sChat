package net.silthus.schat.bukkit.adapter;

import net.silthus.schat.bukkit.SChatBukkitServer;
import net.silthus.schat.platform.chatter.ConnectionListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class BukkitConnectionListener extends ConnectionListener implements Listener {

    private final BukkitSenderFactory senderFactory;

    public BukkitConnectionListener(SChatBukkitServer server) {
        super(server.chatterRepository(), server.chatterFactory(), server.messenger());
        this.senderFactory = server.senderFactory();
        Bukkit.getServer().getPluginManager().registerEvents(this, server.bootstrap().loader());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        onJoin(senderFactory.wrap(event.getPlayer()));
    }
}
