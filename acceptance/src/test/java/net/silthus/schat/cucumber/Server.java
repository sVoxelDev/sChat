package net.silthus.schat.cucumber;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.platform.messaging.StubMessengerGatewayProvider;
import net.silthus.schat.platform.plugin.TestServer;

import static net.silthus.schat.platform.messaging.CrossServerMessengerGateway.GATEWAY_TYPE;

@Getter
@Setter
@Accessors(fluent = true)
public class Server {

    private final TestServer plugin = new TestServer();

    public Server() {
    }

    public void load() {
        plugin().load();
    }

    public void enable() {
        plugin().enable();
    }

    public void disable() {
        plugin().disable();
    }

    public void injectMessenger(StubMessengerGatewayProvider messenger) {
        plugin().getGatewayProviderRegistry().register(GATEWAY_TYPE, messenger);
    }
}
