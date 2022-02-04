package net.silthus.schat.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.silthus.schat.platform.messaging.StubMessengerGatewayProvider;
import net.silthus.schat.platform.plugin.TestServer;

import static net.silthus.schat.platform.messaging.CrossServerMessengerGateway.provideCrossServerMessenger;

public class Context {

    private final Map<String, Server> servers = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();

    public Context() {
    }

    @Before(order = 10, value = "create servers")
    public void setup() {
        servers.put("server1", createServer());
        servers.put("server2", createServer());
    }

    @Before(order = 20, value = "load servers")
    public void loadServers() {
        servers.values().forEach(Server::load);
    }

    @Before(order = 30, value = "inject cross server messenger")
    public void injectCrossServerMessenger() {
        final List<TestServer> servers = this.servers.values().stream().map(Server::plugin).toList();
        final StubMessengerGatewayProvider messenger = provideCrossServerMessenger(servers);
        this.servers.values().forEach(server -> server.injectMessenger(messenger));
    }

    @Before(order = 40, value = "enable servers")
    public void enableServers() {
        servers.values().forEach(Server::enable);
    }

    @After
    public void tearDown() {
        servers.values().forEach(Server::disable);
    }

    @ParameterType("[a-zA-Z0-9]+")
    public Server server(String name) {
        return servers.computeIfAbsent(name, n -> createServer());
    }

    private Server createServer() {
        return new Server();
    }
}
