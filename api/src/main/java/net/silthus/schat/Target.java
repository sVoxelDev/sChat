package net.silthus.schat;

import lombok.NonNull;

@FunctionalInterface
public interface Target {

    void sendMessage(@NonNull Message message);
}
