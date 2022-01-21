package net.silthus.schat.usecases;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;

public interface OnChat {

    Message onChat(@NonNull Chatter chatter, @NonNull Component text) throws NoActiveChannel;

    final class NoActiveChannel extends RuntimeException {
    }
}
