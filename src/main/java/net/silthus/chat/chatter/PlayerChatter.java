package net.silthus.chat.chatter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.silthus.chat.*;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@EqualsAndHashCode(of = "player", callSuper = false)
public class PlayerChatter extends AbstractChatTarget implements Chatter {

    private final Player player;
    private Channel focusedChannel;

    public PlayerChatter(Player player) {
        this.player = player;
    }

    @Override
    public UUID getUniqueId() {
        return getPlayer().getUniqueId();
    }

    @Override
    public String getDisplayName() {
        return getPlayer().getDisplayName();
    }

    @Override
    public void sendMessage(Message message) {
        if (!message.formatted())
            message = message.format(Format.defaultFormat());
        player.sendMessage(message.message());
        setLastMessage(message);
    }

    @Override
    public void sendMessageTo(ChatTarget target, String message) {
        target.sendMessage(Message.of(this, message));
    }
}
