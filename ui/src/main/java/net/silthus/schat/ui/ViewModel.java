package net.silthus.schat.ui;

import java.util.List;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.settings.Configured;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public interface ViewModel extends Configured.Modifiable<ViewModel> {

    static ChatterViewModel of(Chatter chatter) {
        return new ChatterViewModel(chatter);
    }

    @NotNull Chatter getChatter();

    @NotNull @Unmodifiable List<Message> getMessages();

    @NotNull @Unmodifiable List<Channel> getChannels();

    boolean isActiveChannel(Channel channel);
}
