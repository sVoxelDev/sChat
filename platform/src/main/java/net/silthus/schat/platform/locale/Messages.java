/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.silthus.schat.platform.locale;

import java.util.Collection;
import java.util.Iterator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.platform.plugin.bootstrap.Bootstrap;
import net.silthus.schat.platform.sender.Sender;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.JoinConfiguration.newlines;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.platform.locale.Messages.DisplayMode.ACTION_BAR;
import static net.silthus.schat.platform.locale.Messages.DisplayMode.TEXT;

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

    Args1<Bootstrap> STARTUP_BANNER = bootstrap -> {
        Component infoLine1 = text()
            .append(text("sChat", DARK_GREEN))
            .append(space())
            .append(text("v" + bootstrap.version(), AQUA))
            .build();

        Component infoLine2 = text()
            .color(DARK_GRAY)
            .append(text("Running on "))
            .append(text(bootstrap.type().friendlyName()))
            .append(text(" - "))
            .append(text(bootstrap.serverBrand())).append(text(bootstrap.serverVersion()))
            .build();

        //        _________ .__            __
        //   _____\_   ___ \|  |__ _____ _/  |_
        //  /  ___/    \  \/|  |  \\__  \\   __\
        //  \___ \\     \___|   Y  \/ __ \|  |
        // /____  >\______  |___|  (____  |__|
        //      \/        \/     \/     \/

        return join(newlines(),
            empty(),
            text("       _________ .__            __   "),
            text("  _____\\_   ___ \\|  |__ _____ _/  |_ "),
            text(" /  ___/    \\  \\/|  |  \\\\__  \\\\   __\\").append(space()).append(infoLine1),
            text(" \\___ \\\\     \\___|   Y  \\/ __ \\|  |  ").append(space()).append(infoLine2),
            text("/____  >\\______  |___|  (____  |__|  "),
            text("     \\/        \\/     \\/     \\/      "),
            empty()
        );
    };

    /**
     * Joined the channel: {0}.
     */
    DisplayArgs1<Channel> JOINED_CHANNEL = displayMode(channel -> prefixed(translatable()
        .key("schat.command.channel.join.success")
        .color(GREEN)
        .args(channel.displayName())
        .append(FULL_STOP)
    ), ACTION_BAR);

    /**
     * Unable to join the channel: {0}.
     */
    DisplayArgs1<Channel> JOIN_CHANNEL_ERROR = displayMode(channel -> prefixed(translatable()
        .key("schat.command.channel.join.error")
        .color(RED)
        .args(channel.displayName())
        .append(FULL_STOP)
    ), TEXT);

    /**
     * Left the channel: {0}.
     */
    DisplayArgs1<Channel> LEFT_CHANNEL = displayMode(channel -> prefixed(translatable()
        .key("schat.command.channel.leave.success")
        .color(GREEN)
        .args(channel.displayName())
        .append(FULL_STOP)
    ), ACTION_BAR);

    /**
     * Unable to leave the channel: {0}.
     */
    DisplayArgs1<Channel> LEAVE_CHANNEL_ERROR = displayMode(channel -> prefixed(translatable()
        .key("schat.command.channel.leave.error")
        .color(RED)
        .args(channel.displayName())
        .append(FULL_STOP)
    ), TEXT);

    /**
     * Join a channel with {@code /channel join <channel>}.
     */
    Args0 JOIN_CHANNEL_COMMAND = () -> translatable()
        .key("schat.suggest.command.join-channel")
        .color(RED)
        .args(text("/ch <channel>", GOLD)
            .clickEvent(suggestCommand("/ch "))
            .hoverEvent(showText(translatable("schat.hover.join-channel").color(GRAY)))
        ).append(FULL_STOP)
        .build();

    /**
     * No active channel to send message to. Join a channel with {@code /channel join <channel>}.
     */
    Args0 CANNOT_CHAT_NO_ACTIVE_CHANNEL = () -> prefixed(translatable()
        .key("schat.chat.no-active-channel")
        .color(RED)
        .append(FULL_STOP).append(space())
        .append(JOIN_CHANNEL_COMMAND.build())
        .build()
    );

    /**
     * You cannot chat with yourself, sorry :(.
     */
    Args0 CANNOT_SEND_PM_TO_SELF = () -> prefixed(translatable()
        .key("schat.command.pm.cannot-send-to-self")
        .color(RED)
    );

    /**
     * Reloaded the sChat configuration and plugin.
     */
    Args0 RELOAD_SUCCESS = () -> prefixed(translatable()
        .key("schat.command.reload")
        .color(GREEN)
        .append(FULL_STOP)
    );

    static TextComponent prefixed(ComponentLike component) {
        return text()
            .append(PREFIX_COMPONENT)
            .append(space())
            .append(component)
            .build();
    }

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

    interface Args0 {
        default void send(Sender sender) {
            sender.sendMessage(build());
        }

        default void actionBar(Sender sender) {
            sender.sendActionBar(build());
        }

        default void send(Chatter chatter) {
            message(build()).to(chatter).send();
        }

        Component build();
    }

    @Data
    @AllArgsConstructor
    @Accessors(fluent = true)
    class DynamicArgs0 implements Args0 {
        private final Args0 builder;
        private DisplayMode mode;

        public void send(Sender sender) {
            switch (mode) {
                case ACTION_BAR -> actionBar(sender);
                case TEXT -> Args0.super.send(sender);
            }
        }

        @Override
        public Component build() {
            return builder.build();
        }
    }

    static Args0 displayMode(Args0 builder, DisplayMode mode) {
        return new DynamicArgs0(builder, mode);
    }

    interface Args1<A0> {
        default void send(Sender sender, A0 arg0) {
            sender.sendMessage(build(arg0));
        }

        default void actionBar(Sender sender, A0 arg0) {
            sender.sendActionBar(build(arg0));
        }

        default void send(Chatter chatter, A0 arg0) {
            message(build(arg0)).to(chatter).send();
        }

        Component build(A0 arg0);
    }

    @Data
    @AllArgsConstructor
    @Accessors(fluent = true)
    class DisplayArgs1<A0> implements Args1<A0> {
        private final Args1<A0> builder;
        private DisplayMode mode;

        public void send(Sender sender, A0 arg0) {
            switch (mode) {
                case ACTION_BAR -> actionBar(sender, arg0);
                case TEXT -> Args1.super.send(sender, arg0);
            }
        }

        @Override
        public Component build(A0 arg0) {
            return builder.build(arg0);
        }
    }

    static <A extends Args1<A0>, A0> DisplayArgs1<A0> displayMode(A builder, DisplayMode mode) {
        return new DisplayArgs1<>(builder, mode);
    }

    interface Args2<A0, A1> {
        default void send(Sender sender, A0 arg0, A1 arg1) {
            sender.sendMessage(build(arg0, arg1));
        }

        Component build(A0 arg0, A1 arg1);
    }

    interface Args3<A0, A1, A2> {
        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2) {
            sender.sendMessage(build(arg0, arg1, arg2));
        }

        Component build(A0 arg0, A1 arg1, A2 arg2);
    }

    interface Args4<A0, A1, A2, A3> {
        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3));
        }

        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3);
    }

    interface Args5<A0, A1, A2, A3, A4> {
        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4));
        }

        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4);
    }

    interface Args6<A0, A1, A2, A3, A4, A5> {
        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4, arg5));
        }

        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);
    }

    enum DisplayMode {
        NONE,
        TEXT,
        ACTION_BAR
    }
}
