package net.silthus.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.silthus.chat.config.ConsoleConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

@Data
@EqualsAndHashCode(of = {"identifier"}, callSuper = false)
public final class Console extends AbstractChatTarget implements ChatSource, Listener {

    static Console instance;

    public static Console console() {
        if (instance == null)
            throw new UnsupportedOperationException("The console chat target is not initialized! Is sChat enabled?");
        return instance;
    }

    static Console init(@NonNull ConsoleConfig config) {
        if (instance != null)
            throw new UnsupportedOperationException("The console chat target is already initialized. Can only initialize once!");
        instance = new Console(config);
        return instance;
    }

    private final String identifier = Constants.Targets.CONSOLE;
    private ChatTarget target;

    private String name = "Console";

    private Console(ConsoleConfig config) {
        this.target = SChat.instance().getChannelRegistry().get(config.defaultChannel()).orElse(null);
    }

    @Override
    public void sendMessage(Message message) {
        SChat.instance()
                .getAudiences().console()
                .sendMessage(message.getText());
        addReceivedMessage(message);
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsoleChat(ServerCommandEvent event) {
        if (event.getCommand().startsWith("/")) return;

        sendMessage(message(event.getCommand()).to(getTarget()).send());
    }
}
