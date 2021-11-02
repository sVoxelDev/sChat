package net.silthus.chat;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
final class NilChatSource extends NamedChatSource {

    NilChatSource() {
        super("");
    }
}
