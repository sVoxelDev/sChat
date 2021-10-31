package net.silthus.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

@Data
@EqualsAndHashCode(of = {"identifier"}, callSuper = false)
public final class Console extends AbstractChatTarget implements ChatSource, Listener {

    static Console instance;

    public static Console instance() {
        if (instance == null)
            throw new UnsupportedOperationException("The console chat target is not initialized! Is sChat enabled?");
        return instance;
    }

    static Console init(Channel activeChannel) {
        if (instance != null)
            throw new UnsupportedOperationException("The console chat target is already initialized. Can only initialize once!");
        instance = new Console(activeChannel);
        return instance;
    }

    private final String identifier = Constants.Targets.CONSOLE;
    private Channel activeChannel;

    private String displayName = "Console";

    private Console(Channel activeChannel) {
        this.activeChannel = activeChannel;
    }

    private ConsoleCommandSender getConsole() {
        return Bukkit.getConsoleSender();
    }

    @Override
    public void sendMessage(Message message) {
        getConsole().sendMessage(message.formattedMessage());
        addReceivedMessage(message);
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsoleChat(ServerCommandEvent event) {

    }
}
