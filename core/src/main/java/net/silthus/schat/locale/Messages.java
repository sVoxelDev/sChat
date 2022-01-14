package net.silthus.schat.locale;

import java.util.Collection;
import java.util.Iterator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public interface Messages {

    TextComponent OPEN_BRACKET = Component.text('(');
    TextComponent CLOSE_BRACKET = Component.text(')');
    TextComponent FULL_STOP = Component.text('.');

    Component PREFIX_COMPONENT = text()
        .color(GRAY)
        .append(text('['))
        .append(text()
            .decoration(BOLD, true)
            .append(text('s', GOLD, ITALIC))
            .append(text("Chat", DARK_AQUA))
        )
        .append(text(']'))
        .build();

    static TextComponent prefixed(ComponentLike component) {
        return text()
            .append(PREFIX_COMPONENT)
            .append(space())
            .append(component)
            .build();
    }

    // Unable to join the channel: {0}.
    Args1<Channel> JOIN_CHANNEL_ERROR = channel -> prefixed(translatable()
        .key("schat.command.channel.join.error")
        .color(RED)
        .args(channel.getDisplayName())
        .append(FULL_STOP)
    );

    static Component formatStringList(Collection<String> strings) {
        Iterator<String> it = strings.iterator();
        if (!it.hasNext()) {
            return translatable("schat.command.misc.none", AQUA); // "&bNone"
        }

        TextComponent.Builder builder = text().color(DARK_AQUA).content(it.next());
        while (it.hasNext()) {
            builder.append(text(", ", GRAY));
            builder.append(text(it.next()));
        }

        return builder.build();
    }

    static Component formatBoolean(boolean bool) {
        return bool ? text("true", GREEN) : text("false", RED);
    }

    static Message.Builder toMessage(Component text) {
        return Message.message(text).type(Message.Type.SYSTEM);
    }

    interface Args0 {
        Component build();

        default void send(MessageTarget sender) {
            toMessage(build()).to(sender).send();
        }
    }

    interface Args1<A0> {
        Component build(A0 arg0);

        default void send(MessageTarget sender, A0 arg0) {
            toMessage(build(arg0)).to(sender).send();
        }
    }

    interface Args2<A0, A1> {
        Component build(A0 arg0, A1 arg1);

        default void send(MessageTarget sender, A0 arg0, A1 arg1) {
            toMessage(build(arg0, arg1)).to(sender).send();
        }
    }

    interface Args3<A0, A1, A2> {
        Component build(A0 arg0, A1 arg1, A2 arg2);

        default void send(MessageTarget sender, A0 arg0, A1 arg1, A2 arg2) {
            toMessage(build(arg0, arg1, arg2)).to(sender).send();
        }
    }

    interface Args4<A0, A1, A2, A3> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3);

        default void send(MessageTarget sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3) {
            toMessage(build(arg0, arg1, arg2, arg3)).to(sender).send();
        }
    }

    interface Args5<A0, A1, A2, A3, A4> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4);

        default void send(MessageTarget sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4) {
            toMessage(build(arg0, arg1, arg2, arg3, arg4)).to(sender).send();
        }
    }

    interface Args6<A0, A1, A2, A3, A4, A5> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);

        default void send(MessageTarget sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) {
            toMessage(build(arg0, arg1, arg2, arg3, arg4, arg5)).to(sender).send();
        }
    }
}
