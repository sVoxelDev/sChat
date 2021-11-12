package net.silthus.chat.scopes;

import net.silthus.chat.ChatTarget;
import net.silthus.chat.Message;
import net.silthus.chat.Scope;
import net.silthus.chat.conversations.Channel;
import net.silthus.configmapper.ConfigOption;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

@Scope.Name("local")
public final class LocalScope implements Scope {

    @ConfigOption
    int range = 100;

    @Override
    public Collection<ChatTarget> apply(Channel channel, Message message) {
        Player source = Bukkit.getPlayer(message.getSource().getUniqueId());
        if (source == null) return channel.getTargets();
        return channel.getTargets().stream()
                .filter(target -> isNoPlayerOrInRange(target, source.getLocation()))
                .collect(Collectors.toList());
    }

    private boolean isNoPlayerOrInRange(ChatTarget target, Location source) {
        Player player = Bukkit.getPlayer(target.getUniqueId());
        if (player == null) return true;
        return player.getLocation().distance(source) <= range;
    }
}
