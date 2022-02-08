package net.silthus.schat.platform.chatter;

import com.google.gson.InstanceCreator;
import java.lang.reflect.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.Messenger;
import net.silthus.schat.PluginMessage;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.util.gson.GsonProvider;
import org.jetbrains.annotations.NotNull;

public abstract class ConnectionListener {
    private final ChatterRepository chatterRepository;
    private final ChatterFactory chatterFactory;
    private final Messenger messenger;

    public ConnectionListener(ChatterRepository chatterRepository, ChatterFactory chatterFactory, Messenger messenger) {
        this.chatterRepository = chatterRepository;
        this.chatterFactory = chatterFactory;
        this.messenger = messenger;
        registerMessageType();
    }

    protected final void onJoin(Sender sender) {
        sendGlobalJoinPing(getOrCreateChatter(sender));
    }

    private void registerMessageType() {
        messenger.registerMessageType(ChatterJoined.class);
        GsonProvider.registerTypeAdapter(ChatterJoined.class, new MessageCreator());
    }

    @NotNull
    private Chatter getOrCreateChatter(Sender sender) {
        return chatterRepository.find(sender.uniqueId())
            .orElseGet(() -> createChatter(sender));
    }

    @NotNull
    private Chatter createChatter(Sender sender) {
        final Chatter c = chatterFactory.createChatter(sender.uniqueId());
        chatterRepository.add(c);
        return c;
    }

    private void sendGlobalJoinPing(Chatter chatter) {
        messenger.sendPluginMessage(new ChatterJoined(chatter.identity()));
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    @NoArgsConstructor
    @EqualsAndHashCode(of = {"identity"}, callSuper = true)
    final static class ChatterJoined extends PluginMessage {
        private Identity identity;
        private transient ChatterRepository repository;
        private transient ChatterFactory factory;

        ChatterJoined(Identity identity) {
            this.identity = identity;
        }

        @Override
        public void process() {
            if (!repository.contains(identity.uniqueId()))
                repository.add(factory.createChatter(identity.uniqueId()));
        }
    }

    private final class MessageCreator implements InstanceCreator<ChatterJoined> {
        @Override
        public ChatterJoined createInstance(Type type) {
            return new ChatterJoined().repository(chatterRepository).factory(chatterFactory);
        }
    }
}
