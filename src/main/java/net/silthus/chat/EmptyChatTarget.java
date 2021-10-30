package net.silthus.chat;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = {"identifier"}, callSuper = false)
final class EmptyChatTarget extends AbstractChatTarget implements ChatTarget {

    private final String identifier = Constants.SYSTEM_TARGET_IDENTIFIER;

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void sendMessage(Message message) {
        addReceivedMessage(message);
    }
}
