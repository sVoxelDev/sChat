package net.silthus.chat;

import lombok.NonNull;
import net.silthus.chat.targets.PlayerChatter;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public interface Chatter extends ChatTarget, ChatSource {

    static PlayerChatter of(Player player) {
        return SChat.instance().getChatterManager().registerChatter(player);
    }

    UUID getUniqueId();

    void setActiveConversation(Conversation conversation);

    Collection<Conversation> getConversations();

    void subscribe(@NonNull Conversation conversation);

    void unsubscribe(@NonNull Conversation conversation);
}
