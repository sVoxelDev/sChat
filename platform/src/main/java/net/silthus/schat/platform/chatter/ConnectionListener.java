package net.silthus.schat.platform.chatter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.silthus.schat.Messenger;
import net.silthus.schat.PluginMessage;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.platform.sender.Sender;

public class ConnectionListener {
    private final ChatterRepository chatterRepository;
    private final ChatterFactory chatterFactory;
    private final Messenger messenger;

    public ConnectionListener(ChatterRepository chatterRepository, ChatterFactory chatterFactory, Messenger messenger) {
        this.chatterRepository = chatterRepository;
        this.chatterFactory = chatterFactory;
        this.messenger = messenger;
        messenger.registerMessageType(ChatterJoined.class);
    }

    public void onJoin(Sender sender) {
        final Chatter chatter = chatterRepository.find(sender.uniqueId())
            .orElseGet(() -> {
                final Chatter c = chatterFactory.createChatter(sender.uniqueId());
                chatterRepository.add(c);
                return c;
            });
        messenger.sendPluginMessage(new ChatterJoined(chatter.identity()));
    }

    @Getter
    @Accessors(fluent = true)
    @EqualsAndHashCode(of = {"identity"}, callSuper = true)
    final static class ChatterJoined extends PluginMessage {
        private final Identity identity;

        ChatterJoined(Identity identity) {
            this.identity = identity;
        }

        @Override
        public void process() {

        }
    }
}
