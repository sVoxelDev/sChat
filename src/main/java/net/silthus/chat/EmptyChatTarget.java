package net.silthus.chat;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = {"identifier"}, callSuper = false)
final class EmptyChatTarget extends AbstractChatTarget implements ChatTarget {

    private final String identifier = Constants.Targets.EMPTY;

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void sendMessage(Message message) {
        addReceivedMessage(message);
    }
}
