package net.silthus.schat.handler.types;

import net.silthus.schat.User;
import net.silthus.schat.chatter.ChatterRegistry;
import net.silthus.schat.handler.Handler;
import net.silthus.schat.handler.UserHandlerFactory;

import static net.silthus.schat.chatter.Chatter.chatter;

public class DefaultJoinGameHandler implements Handler.JoinGame {

    private final ChatterRegistry registry;
    private final UserHandlerFactory<Handler.JoinChannel> joinChannelFactory;

    public DefaultJoinGameHandler(ChatterRegistry registry, UserHandlerFactory<JoinChannel> joinChannelFactory) {
        this.registry = registry;
        this.joinChannelFactory = joinChannelFactory;
    }

    @Override
    public void joinGame(final User user) {
        registry.add(chatter(user.getIdentity()).joinChannelHandler(joinChannelFactory.create(user)).create());
    }
}
