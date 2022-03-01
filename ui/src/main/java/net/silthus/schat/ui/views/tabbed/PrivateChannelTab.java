package net.silthus.schat.ui.views.tabbed;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;

public class PrivateChannelTab extends ChannelTab implements Tab {

    protected PrivateChannelTab(@NonNull TabbedChannelsView view, @NonNull Channel channel, TabFormatConfig config) {
        super(view, channel, config);
    }

    @Override
    public Component name() {
        return channel().targets().stream()
            .filter(target -> target instanceof Chatter)
            .filter(target -> !target.equals(Chatter.empty()))
            .filter(target -> !target.equals(view().chatter()))
            .findFirst()
            .map(target -> (Chatter) target)
            .map(Identified::displayName)
            .orElse(channel().displayName());
    }

    @Override
    protected boolean isMessageDisplayed(Message message) {
        if (message.source() instanceof MessageTarget target)
            return channel().targets().contains(target);
        else
            return message.channels().contains(channel());
    }
}
