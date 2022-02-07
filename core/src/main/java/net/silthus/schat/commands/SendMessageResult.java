package net.silthus.schat.commands;

import net.silthus.schat.command.Result;
import net.silthus.schat.message.Message;

public record SendMessageResult(Message message, boolean success) implements Result {

    @Override
    public boolean wasSuccessful() {
        return success;
    }
}
