package net.silthus.chat.layout;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.silthus.chat.*;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static net.kyori.adventure.text.minimessage.template.TemplateResolver.templates;
import static net.silthus.chat.Constants.Commands.JOIN_CHANNEL;
import static net.silthus.chat.Constants.View.*;

public class TabbedChatLayout implements ChatLayout {

    @Override
    public Component render(Chatter chatter, Message... messages) {

        return text().append(clearChat())
                .append(renderMessages(messages))
                .append(footer())
                .append(channelTabs(chatter))
                .build();
    }

    public Component footer() {
        return empty();
    }

    Component channelTabs(Chatter chatter) {
        if (chatter.getSubscriptions().isEmpty()) {
            return noChannels();
        }

        TextComponent.Builder builder = text().append(text(CHANNEL_DIVIDER + " ").color(FRAME_COLOR));
        for (ChannelSubscription subscription : chatter.getSubscriptions()) {
            builder.append(channel(chatter, subscription.getChannel()));
        }
        return builder.build();
    }

    Component renderMessages(Message[] messages) {
        TextComponent.Builder builder = text();
        for (Message message : messages) {
            builder.append(message.formatted()).append(newline());
        }
        return builder.build();
    }

    Component clearChat() {
        TextComponent.Builder builder = text();
        for (int i = 0; i < 100; i++) {
            builder.append(newline());
        }
        return builder.build();
    }

    private TextComponent noChannels() {
        return text(CHANNEL_DIVIDER + " ").color(FRAME_COLOR)
                .append(text("No Channels selected. Use ").color(INFO_COLOR))
                .append(text("/ch join <channel> ").color(COMMAND).clickEvent(suggestCommand("/ch join ")))
                .append(text("to join a channel.").color(INFO_COLOR));
    }

    private Component channel(Chatter chatter, Channel channel) {
        boolean isActive = channel.equals(chatter.getActiveChannel());
        return text().append(channelName(chatter, channel, isActive))
                .append(text(" " + CHANNEL_DIVIDER + " ").color(FRAME_COLOR))
                .build();
    }

    private Component channelName(Chatter chatter, Channel channel, boolean isActive) {
        Component channelName = MiniMessage.miniMessage().deserialize(channel.getName(), templates(
                playerName(chatter)
        )).clickEvent(clickEvent(ClickEvent.Action.RUN_COMMAND, JOIN_CHANNEL.apply(channel)));
        if (isActive)
            channelName = channelName.color(ACTIVE_COLOR).decorate(ACTIVE_DECORATION);
        else
            channelName = channelName.color(INACTIVE_COLOR);
        return channelName;
    }

    private Template playerName(Chatter chatter) {
        return Template.template("player_name", chatter.getName());
    }
}
