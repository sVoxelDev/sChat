package net.silthus.schat.platform.sender;

import java.util.LinkedList;
import java.util.Queue;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;

import static net.silthus.schat.IdentityHelper.randomIdentity;
import static org.assertj.core.api.Assertions.assertThat;

@Getter
public class SenderMock implements Sender {

    public static SenderMock senderMock() {
        return new SenderMock(randomIdentity());
    }

    public static SenderMock senderMock(Identity identity) {
        return new SenderMock(identity);
    }

    private final Identity identity;
    private final Queue<Component> messages = new LinkedList<>();

    public SenderMock(Identity identity) {
        this.identity = identity;
    }

    @Override
    public void sendMessage(Component message) {
        messages.add(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return false;
    }

    @Override
    public void performCommand(String commandLine) {

    }

    @Override
    public boolean isConsole() {
        return false;
    }

    public void assertLastMessageIs(Component component) {
        assertThat(messages.peek()).isEqualTo(component);
    }
}
