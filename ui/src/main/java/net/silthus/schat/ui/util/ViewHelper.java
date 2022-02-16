package net.silthus.schat.ui.util;

import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.silthus.schat.channel.ChannelSettings.PRIVATE;

public final class ViewHelper {

    @NotNull
    public static Component renderPrivateChannelName(Chatter chatter, Channel channel) {
        return channel.targets().stream()
            .filter(target -> target instanceof Chatter)
            .filter(target -> !target.equals(chatter))
            .findFirst()
            .map(target -> (Chatter) target)
            .map(Identified::displayName)
            .orElse(channel.displayName());
    }

    public static Component renderPartnerName(Chatter viewer, Message message) {
        return message.targets().stream()
            .filter(target -> target instanceof Channel)
            .map(target -> (Channel) target)
            .filter(channel -> channel.is(PRIVATE))
            .map(channel -> renderPrivateChannelName(viewer, channel))
            .findFirst()
            .orElse(Component.empty());
    }

    public static Component renderPrivateMessage(Chatter viewer, Message message) {
        Component name;
        if (message.source().equals(viewer))
            name = translatable("schat.chat.private.you").color(YELLOW);
        else
            name = renderPartnerName(viewer, message).colorIfAbsent(AQUA);

        return name.append(text(": ", GRAY))
            .append(message.text().colorIfAbsent(GRAY));
    }

    private ViewHelper() {
    }
}
